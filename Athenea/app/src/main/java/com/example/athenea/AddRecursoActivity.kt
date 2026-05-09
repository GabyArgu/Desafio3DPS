package com.example.athenea

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.athenea.databinding.ActivityAddRecursoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRecursoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecursoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecursoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGuardar.setOnClickListener {
            if (validarCampos()) {
                guardarEnServidor()
            }
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        val titulo = binding.etTitulo.text.toString().trim()
        val desc = binding.etDescripcion.text.toString().trim()
        val enlace = binding.etEnlace.text.toString().trim()

        if (titulo.isEmpty()) {
            binding.etTitulo.error = getString(R.string.error_campo_obligatorio)
            esValido = false
        }

        if (desc.isEmpty()) {
            binding.etDescripcion.error = getString(R.string.error_campo_obligatorio)
            esValido = false
        }

        if (enlace.isEmpty()) {
            binding.etEnlace.error = getString(R.string.error_campo_obligatorio)
            esValido = false
        }

        return esValido
    }

    private fun guardarEnServidor() {
        val nuevoRecurso = Recurso(
            id = null,
            titulo = binding.etTitulo.text.toString().trim(),
            descripcion = binding.etDescripcion.text.toString().trim(),
            tipo = binding.etTipo.text.toString().trim(),
            enlace = binding.etEnlace.text.toString().trim(),
            imagen = binding.etImagen.text.toString().trim().ifEmpty { "https://via.placeholder.com/300" }
        )

        RetrofitClient.instance.addRecurso(nuevoRecurso).enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddRecursoActivity, getString(R.string.msg_recurso_guardado), Toast.LENGTH_LONG).show()
                    finish() // Regresa a la lista automáticamente
                } else {
                    Toast.makeText(this@AddRecursoActivity, "Error al procesar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                Toast.makeText(this@AddRecursoActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}