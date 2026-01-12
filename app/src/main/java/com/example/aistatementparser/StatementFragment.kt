package com.example.aistatementparser

import StatementViewModel
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aistatementparser.Adaptor.StatementAdapter
import com.example.aistatementparser.databinding.FragmentStatementBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class StatementFragment : Fragment() {
    private var _binding: FragmentStatementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatementViewModel by activityViewModels(){
        StatementViewModelFactory(StatementRepository())
    }

    private lateinit var adapter: StatementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStatementBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObserver()
        setupClickListner()

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_statementFragment_to_homeFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = StatementAdapter { categoryName ->
            viewModel.setSelectedCategory(categoryName)

            findNavController().navigate(R.id.action_statementFragment_to_categoryFragment)
        }

        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
            binding.rvTransactions.adapter = adapter
        }


    private fun setupObserver() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allTransactions.collect { list ->
                if (list.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.tvEmptyState.text = "No transactions found"
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    adapter.submitList(list)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loading.collect { isLoading ->
                binding.progressBar.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collect { errorMsg ->
                errorMsg?.let {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.tvEmptyState.text = it
                }
            }
        }
    }


    private fun setupClickListner() {

        binding.btnUploadPdf.setOnClickListener {
            pickPdfFile()
        }
    }


    private fun pickPdfFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pdfPickerLauncher.launch(intent)
    }

    private val pdfPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    Log.d(TAG, "PDF selected: $uri")
                    uploadPdf(uri)
                }
            }
        }

    private fun uploadPdf(uri: Uri) {
        val file = uriToFile(requireContext(), uri)
        val part = fileToMultipart(file)
        viewModel.uploadPdf(part)
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream")

        val tempFile = File.createTempFile("                            ", ".pdf", context.cacheDir)

        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        Log.d(TAG, "Temp PDF created: ${tempFile.absolutePath}")
        return tempFile
    }

    private fun fileToMultipart(file: File): MultipartBody.Part {
        val requestBody = file.asRequestBody("application/pdf".toMediaType())

        return MultipartBody.Part.createFormData(
            name = "file",          // MUST match FastAPI parameter
            filename = file.name,
            body = requestBody
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }
}