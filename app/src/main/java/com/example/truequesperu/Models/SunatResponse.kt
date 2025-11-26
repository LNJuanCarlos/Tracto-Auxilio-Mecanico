package com.example.truequesperu.Models

data class SunatResponse(
    val numero: String,
    val nombre: String,
    val direccion: String?,
    val estado: String,
    val condicion: String
)