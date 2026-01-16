package com.example.aistatementparser

import StatementViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.aistatementparser.Model.CategorySpend
import com.example.aistatementparser.databinding.FragmentCategoryBinding
import com.example.aistatementparser.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var debitChart: PieChart
    private lateinit var creditChart: PieChart

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel
    private val viewModel: StatementViewModel by activityViewModels {
        StatementViewModelFactory(StatementRepository())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        debitChart = binding.testPieChart
        creditChart = binding.testPieChart2

        setupChart(debitChart)
        setupChart(creditChart)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeDebitChart()
        observeCreditChart()
        observeTotals()
    }

    private fun observeDebitChart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.debitCategorySpend.collect { list ->
                updatePie(
                    chart = debitChart,
                    data = list,
                    emptyText = "No Debit Data"
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalDebit.collect { total ->
                debitChart.centerText = formatCenterText(total, "Debit")
            }
        }
    }

    private fun observeCreditChart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.creditCategorySpend.collect { list ->
                updatePie(
                    chart = creditChart,
                    data = list,
                    emptyText = "No Credit Data"
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalCredit.collect { total ->
                creditChart.centerText = formatCenterText(total, "Credit")
            }
        }
    }

    private fun observeTotals(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalDebit.collect { total->
                binding.debit.text = "Debit\n₹%,.0f".format(total)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalCredit.collect { total->
                binding.credit.text = "Credit\n₹%,.0f".format(total)
            }
        }
    }

    private fun setupChart(chart: PieChart) {
        chart.apply {
            setHoleColor(Color.TRANSPARENT)
            setCenterTextColor(Color.BLACK)
            setCenterTextSize(12f)
            description.isEnabled = false
        }
    }

    private fun updatePie(
        chart: PieChart,
        data: List<CategorySpend>,
        emptyText: String
    ) {
        if (data.isEmpty()) {
            chart.clear()
            chart.centerText = emptyText
            return
        }

        val entries = data.map {
            PieEntry(it.percentage, it.category)
        }

        val colors = ArrayList<Int>().apply {
            ColorTemplate.MATERIAL_COLORS.forEach { add(it) }
        }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextColor = Color.WHITE
            valueTextSize = 14f
        }

        chart.data = PieData(dataSet)
        chart.animateY(1000)
        chart.invalidate()
    }

    private fun formatCenterText(amount: Double, label: String): String {
        val formatted = "₹%,.0f".format(amount)
        return "$formatted\nTotal $label"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}