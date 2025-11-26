package com.example.truequesperu.Models

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SunatAPI {
    @POST("ruc")
    suspend fun consultarRuc(
        @Body request: RucRequest
    ): ApiPeruResponse
}