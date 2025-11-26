package com.example.truequesperu.Models

import com.google.gson.annotations.SerializedName

data class SunatData(
    val ruc: String?,

    @SerializedName("nombre_o_razon_social")
    val nombre: String?,

    val direccion: String?,

    val estado: String?,

    val condicion: String?
)