package com.example.truequesperu.Models

data class User(
    val id: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val tipoUsuario: String = "",   // "CLIENTE" o "TECNICO"
    val correo: String = ""
)