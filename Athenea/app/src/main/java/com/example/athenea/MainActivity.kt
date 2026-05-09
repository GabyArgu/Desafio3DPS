package com.example.athenea

import android.content.Intent
import android.os.Bundle
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
                Toast.makeText(this, "Por favor completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llamada a la API para obtener todos los usuarios y validar
            RetrofitClient.instance.getUsuarios().enqueue(object : Callback<List<User>> {
                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        val usuarios = response.body()
                        // Buscamos si existe un usuario con esas credenciales
                        val usuarioEncontrado = usuarios?.find {
                            it.email == emailEntrada && it.password == passEntrada
                        }

                        if (usuarioEncontrado != null) {
                            Toast.makeText(this@MainActivity, "¡Bienvenido, ${usuarioEncontrado.role}!", Toast.LENGTH_SHORT).show()

                            // Pasamos a la siguiente pantalla enviando el ROL real
                            val intent = Intent(this@MainActivity, RecursosActivity::class.java)
                            intent.putExtra("USER_ROLE", usuarioEncontrado.role)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Error en el servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Fallo de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}