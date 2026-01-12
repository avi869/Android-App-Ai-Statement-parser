package com.example.aistatementparser.analytics


import com.example.aistatementparser.Model.CategorySpend
import com.example.aistatementparser.Model.TransactionDto

object TransactionAnalytics {

    fun totalDebit(transactions: List<TransactionDto>): Double {
        return transactions
            .filter { it.Type.equals("DEBIT", true) }
            .sumOf { it.Amount.toDoubleOrNull() ?: 0.0 }
    }

    fun totalCredit(transactions: List<TransactionDto>): Double {
        return transactions
            .filter { it.Type.equals("CREDIT", true) }
            .sumOf { it.Amount.toDoubleOrNull() ?: 0.0 }
    }


    // category wise total
    fun categoryTotals(transactions: List<TransactionDto>, type: String): Map<String, Double> {
        return transactions
            .filter { it.Type.equals(type, true) }
            .groupBy { it.Category ?: "Uncategorized" }
            .mapValues { entry ->
                entry.value.sumOf { it.Amount.toDoubleOrNull() ?: 0.0 }
            }
    }
        // calculate percentage
        fun categoryPercentage(
            transactions: List<TransactionDto>, type: String): List<CategorySpend> {
            val totals = categoryTotals(transactions, type)
            val grandTotal = totals.values.sum()

            if (grandTotal == 0.0) return emptyList()

            return totals.map { (category, total) ->
                CategorySpend(
                    category = category,
                    totalAmount = total,
                    percentage = ((total / grandTotal) * 100).toFloat()
                )
            }
        }
}