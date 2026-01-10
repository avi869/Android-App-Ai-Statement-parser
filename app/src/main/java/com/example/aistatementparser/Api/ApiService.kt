package com.example.aistatementparser.Api

import com.example.aistatementparser.Model.ExtractResponse
import okhttp3.MultipartBody
import retrofit2.Response

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("extract-transactions")
    suspend fun uploadStatement(
        // backend receive Multipart/form-data
        @Part file: MultipartBody.Part
    ): Response<ExtractResponse>
}