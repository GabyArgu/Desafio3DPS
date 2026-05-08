package com.example.athenea

data class Recurso(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val enlace: String,
    val imagen: String,
    val rating: Float = 0f
)