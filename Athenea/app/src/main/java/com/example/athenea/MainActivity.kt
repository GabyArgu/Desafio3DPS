package com.example.athenea

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.athenea.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val emailEntrada = binding.etEmail.text.toString().trim()
            val passEntrada = binding.etPassword.text.toString().trim()

            if (emailEntrada.isEmpty() || passEntrada.isEmpty()) {
                showCustomToast("Por favor completa los campos")
                return@setOnClickListener
            }

            RetrofitClient.instance.getUsuarios().enqueue(object : Callback<List<User>> {
                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        val usuarios = response.body()
                        val usuarioEncontrado = usuarios?.find {
                            it.email == emailEntrada && it.password == passEntrada
                        }

                        if (usuarioEncontrado != null) {
                            showCustomToast("¡Bienvenido, ${usuarioEncontrado.role}!")

                            val intent = Intent(this@MainActivity, RecursosActivity::class.java)
                            intent.putExtra("USER_ROLE", usuarioEncontrado.role)
                            startActivity(intent)
                            finish()
                        } else {
                            showCustomToast("Correo o contraseña incorrectos")
                        }
                    } else {
                        showCustomToast("Error en el servidor: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    showCustomToast("Fallo de conexión: ${t.message}")
                }
            })
        }

        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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