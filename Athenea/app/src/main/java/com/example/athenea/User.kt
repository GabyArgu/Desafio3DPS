package com.example.athenea

data class User(
    val id: String? = null,
    val email: String,
    val password: String,
    val role: String
)