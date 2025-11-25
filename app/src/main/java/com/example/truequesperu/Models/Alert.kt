package com.example.truequesperu.Models

import java.io.Serializable

data class Alert(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    var tipo: String = "",
    val fotos: List<String> = emptyList(),
    val vehiculo: Vehiculo = Vehiculo(),      // AQUÍ
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val descripcion: String = "",
    val status: String = "Pendiente",
    val tecnicoId: String = "",
    val tecnicoName: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable