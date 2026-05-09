package com.example.athenea

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.athenea.databinding.ActivityAddRecursoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRecursoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecursoBinding
    private var recursoAEditar: Recurso? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecursoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Configurar el Spinner primero
        setupSpinner()

        // 2. Recibir datos si es edición
        recursoAEditar = intent.getSerializableExtra("RECURSO_EDITAR") as? Recurso

        if (recursoAEditar != null) {
            prellenarDatos(recursoAEditar!!)
        }

        binding.btnGuardar.setOnClickListener {
            if (validarFormulario()) {
                ejecutarAccion()
            }
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.tipos_recursos,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapter
    }

    private fun prellenarDatos(recurso: Recurso) {
        binding.btnGuardar.text = "Actualizar Cambios"
        // Si tienes un TextView para el título de la pantalla:
        // binding.tvTitleAdd.text = "Editar Recurso"

        binding.etTitulo.setText(recurso.titulo)
        binding.etDescripcion.setText(recurso.descripcion)
        binding.etEnlace.setText(recurso.enlace)
        binding.etImagen.setText(recurso.imagen)

        val adapter = binding.spinnerTipo.adapter as ArrayAdapter<String>

        // Buscamos la posición del texto que viene de la API
        val posicion = (0 until adapter.count).firstOrNull {
            adapter.getItem(it) == recurso.tipo
        } ?: 0 // Si no lo encuentra, selecciona el primero por defecto

        binding.spinnerTipo.setSelection(posicion)
    }

    private fun validarFormulario(): Boolean {
        var valido = true
        if (binding.etTitulo.text.toString().isBlank()) {
            binding.etTitulo.error = getString(R.string.error_campo_obligatorio)
            valido = false
        }
        if (binding.etDescripcion.text.toString().isBlank()) {
            binding.etDescripcion.error = getString(R.string.error_campo_obligatorio)
            valido = false
        }
        if (binding.etEnlace.text.toString().isBlank()) {
            binding.etEnlace.error = getString(R.string.error_campo_obligatorio)
            valido = false
        }
        return valido
    }

    private fun ejecutarAccion() {
        val recursoFinal = Recurso(
            id = recursoAEditar?.id,
            titulo = binding.etTitulo.text.toString().trim(),
            descripcion = binding.etDescripcion.text.toString().trim(),
            tipo = binding.spinnerTipo.selectedItem.toString(),
            enlace = binding.etEnlace.text.toString().trim(),
            imagen = binding.etImagen.text.toString().trim().ifEmpty { "https://via.placeholder.com/300" }
        )

        val call = if (recursoAEditar == null) {
            RetrofitClient.instance.addRecurso(recursoFinal)
        } else {
            RetrofitClient.instance.updateRecurso(recursoAEditar!!.id!!, recursoFinal)
        }

        call.enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    val msg = if (recursoAEditar == null) R.string.msg_recurso_guardado else R.string.msg_recurso_editado
                    Toast.makeText(this@AddRecursoActivity, getString(msg), Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@AddRecursoActivity, getString(R.string.error_guardar), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                Toast.makeText(this@AddRecursoActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}