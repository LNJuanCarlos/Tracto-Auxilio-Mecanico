package com.example.truequesperu.Models

import com.google.gson.annotations.SerializedName

data class ApiPeruResponse(
    val success: Boolean,
    val data: SunatData?
)

