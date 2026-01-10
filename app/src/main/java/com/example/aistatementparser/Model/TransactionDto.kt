package com.example.aistatementparser.Model

data class TransactionDto(
    val Date: String,
    val Description: String,
    val Type: String,
    val Amount: String,
    val Category: String? = "Uncategorized"
)

data class ExtractResponse(
    val status: String,
    val filename: String,
    val transaction_count: Int,
    val data: List<TransactionDto>
)