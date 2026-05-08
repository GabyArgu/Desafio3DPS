package com.example.athenea

object Validator {
    fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\\$%^&*])(?=\\S+$).{12,}$"
        return password.matches(passwordPattern.toRegex())
    }
}