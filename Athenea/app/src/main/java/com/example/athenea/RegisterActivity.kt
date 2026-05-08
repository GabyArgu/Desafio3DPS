package com.example.athenea

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.athenea.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val email = binding.etRegEmail.text.toString()
            val pass = binding.etRegPassword.text.toString()
            val role = if (binding.rbDocente.isChecked) "Docente" else "Estudiante"

            if (email.isNotEmpty() && Validator.isPasswordValid(pass)) {
                Toast.makeText(this, "Registro exitoso como $role", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.error_password_invalid), Toast.LENGTH_LONG).show()
            }
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}