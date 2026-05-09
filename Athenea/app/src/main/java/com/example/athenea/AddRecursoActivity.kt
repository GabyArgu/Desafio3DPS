package com.example.athenea

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        setupSpinner()

        recursoAEditar = intent.getSerializableExtra("RECURSO_EDITAR") as? Recurso

        if (recursoAEditar != null) {
            prellenarDatos(recursoAEditar!!)
        }

        // Botón de cerrar (X)
        binding.btnCerrar.setOnClickListener { finish() }

        binding.btnGuardar.setOnClickListener {
            if (validarFormulario()) ejecutarAccion()
        }

        binding.btnEliminar.setOnClickListener {
            mostrarDialogoEliminar()
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.tipos_recursos, R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapter
    }

    private fun prellenarDatos(recurso: Recurso) {
        binding.tvTitleAdd.text = "Editar Recurso"
        binding.btnGuardar.text = "Actualizar Cambios"
        binding.btnEliminar.visibility = View.VISIBLE // Mostrar eliminar solo al editar

        binding.etTitulo.setText(recurso.titulo)
        binding.etDescripcion.setText(recurso.descripcion)
        binding.etEnlace.setText(recurso.enlace)
        binding.etImagen.setText(recurso.imagen)

        val adapter = binding.spinnerTipo.adapter as ArrayAdapter<String>
        val posicion = (0 until adapter.count).firstOrNull { adapter.getItem(it) == recurso.tipo } ?: 0
        binding.spinnerTipo.setSelection(posicion)
    }

    private fun mostrarDialogoEliminar() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage(R.string.confirm_delete)
            .setPositiveButton("Sí, eliminar") { _, _ -> eliminarRecurso() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarRecurso() {
        recursoAEditar?.id?.let { id ->
            RetrofitClient.instance.deleteRecurso(id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddRecursoActivity, R.string.msg_recurso_eliminado, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddRecursoActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun validarFormulario(): Boolean {
        var valido = true
        if (binding.etTitulo.text.toString().isBlank()) {
            binding.etTitulo.error = getString(R.string.error_campo_obligatorio)
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
                }
            }
            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                Toast.makeText(this@AddRecursoActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}