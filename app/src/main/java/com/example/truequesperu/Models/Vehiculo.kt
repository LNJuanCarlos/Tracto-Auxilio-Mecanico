package com.example.truequesperu.Models

import java.io.Serializable

data class Vehiculo(
    val placa: String = "",
    val modelo: String = "",
    val anio: String = "",
    val vin: String = ""
) : Serializable