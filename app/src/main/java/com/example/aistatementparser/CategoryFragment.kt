package com.example.aistatementparser

import StatementViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aistatementparser.Adaptor.StatementAdapter
import com.example.aistatementparser.databinding.FragmentCategoryBinding
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatementViewModel by activityViewModels {
        StatementViewModelFactory(StatementRepository())
    }

    private lateinit var adapter: StatementAdapter
    private var currentType = "DEBIT"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwitch()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = StatementAdapter {}
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter
    }

    private fun setupSwitch() {
        binding.debitCreditSwitch.isChecked = false
        binding.debitCreditSwitch.text = "Debit"

        binding.debitCreditSwitch.setOnCheckedChangeListener { _, isChecked ->
            currentType = if (isChecked) {
                binding.debitCreditSwitch.text = "Credit"
                "CREDIT"
            } else {
                binding.debitCreditSwitch.text = "Debit"
                "DEBIT"
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredTransactions.collect { list ->

                binding.categoryName.text =
                    viewModel.selectedCategory.value ?: "All"

                val debitList = list.filter { it.Type.equals("DEBIT", true) }
                val creditList = list.filter { it.Type.equals("CREDIT", true) }

                val showList =
                    if (currentType == "DEBIT") debitList else creditList

                adapter.submitList(showList)

                val total = showList.sumOf {
                    it.Amount.toDoubleOrNull() ?: 0.0
                }

                binding.totalAmount.text =
                    "₹%,.2f".format(total)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
