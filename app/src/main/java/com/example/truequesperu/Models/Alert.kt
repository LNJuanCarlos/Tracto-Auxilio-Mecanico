package com.example.truequesperu.Models

import java.io.Serializable

data class Alert(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val descripcion: String = "",
    val status: String = "Pendiente",        // <--- Estado por defecto
    val tecnicoId: String = "",
    val tecnicoName: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable