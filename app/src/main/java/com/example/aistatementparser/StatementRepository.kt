package com.example.aistatementparser

import android.util.Log
import com.example.aistatementparser.Api.RetrofitClient
import com.example.aistatementparser.Model.TransactionDto
import okhttp3.MultipartBody

class StatementRepository {

    suspend fun uploadPdf(file : MultipartBody.Part): List<TransactionDto> {
        val response = RetrofitClient.api.uploadStatement(file)

        // Verify server response
        if(response.isSuccessful) {
            val body = response.body()

            Log.d("StatementRepo", "Response Body: $body")
            Log.d("StatementRepo", "Transaction count: ${body?.data?.size}")

            return body?.data ?: emptyList()
        }else{

            val errorBody = response.errorBody()?.string()

            Log.e("StatementRepo", "Server Error")
            Log.e("StatementRepo", "Error Code: ${response.code()}")
            Log.e("StatementRepo", "Error Body: $errorBody")

            throw Exception("Server Error ${response.code()}")
        }
    }
}