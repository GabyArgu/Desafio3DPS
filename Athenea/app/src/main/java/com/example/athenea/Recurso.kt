package com.example.athenea

import java.io.Serializable

data class Recurso(
    val id: String? = null,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val enlace: String,
    val imagen: String,
    var isFavorite: Boolean = false
) : Serializable