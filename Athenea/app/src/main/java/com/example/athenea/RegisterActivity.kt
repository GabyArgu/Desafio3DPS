package com.example.athenea

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
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
            val email = binding.etRegEmail.text.toString().trim()
            val pass = binding.etRegPassword.text.toString()
            val role = if (binding.rbDocente.isChecked) "Docente" else "Estudiante"

            if (email.isEmpty()) {
                binding.etRegEmail.error = "El correo es obligatorio"
                return@setOnClickListener
            }

            if (!Validator.isPasswordValid(pass)) {
                binding.etRegPassword.error = getString(R.string.error_password_invalid2)
                showCustomToast("Contraseña no válida")
                return@setOnClickListener
            }

            val nuevoUsuario = User(email = email, password = pass, role = role)

            RetrofitClient.instance.registrarUsuario(nuevoUsuario).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        showCustomToast("¡Usuario $email registrado!")
                        finish()
                    } else {
                        showCustomToast("Error al guardar: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    showCustomToast("Error de red: ${t.message}")
                }
            })
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun showCustomToast(message: String) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val text = layout.findViewById<TextView>(R.id.toast_text)
        text.text = message

        val customToast = Toast(applicationContext)
        customToast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 150)
        customToast.duration = Toast.LENGTH_LONG
        customToast.view = layout
        customToast.show()
    }
}