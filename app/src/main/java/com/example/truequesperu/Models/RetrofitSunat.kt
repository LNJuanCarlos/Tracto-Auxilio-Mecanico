package com.example.truequesperu.Models

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitSunat {

    private const val BASE_URL = "https://apiperu.dev/api/"
    private const val TOKEN = "4a73d8cf86bd6cba293256dd76bb1f966dde78fc2828551f741243cc18823a1c" // https://apiperu.dev/api/

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $TOKEN")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            chain.proceed(request)
        }
        .build()

    val api: SunatAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SunatAPI::class.java)
}