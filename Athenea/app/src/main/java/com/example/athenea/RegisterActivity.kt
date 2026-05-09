package com.example.athenea

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.athenea.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

            // 1. Validaciones de UI
            if (email.isEmpty()) {
                binding.etRegEmail.error = "El correo es obligatorio"
                return@setOnClickListener
            }

            if (!Validator.isPasswordValid(pass)) {
                binding.etRegPassword.error = getString(R.string.error_password_invalid2)
                Toast.makeText(this, "Contraseña no válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Crear el objeto usuario
            val nuevoUsuario = User(email = email, password = pass, role = role)

            // 3. Mandar a la API
            RetrofitClient.instance.registrarUsuario(nuevoUsuario).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "¡Usuario $email registrado con éxito!", Toast.LENGTH_LONG).show()
                        finish() // Regresa al Login
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error al guardar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}