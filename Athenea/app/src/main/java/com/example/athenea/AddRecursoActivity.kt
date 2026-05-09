package com.example.athenea

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.athenea.databinding.ActivityAddRecursoBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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

        binding.btnCerrar.setOnClickListener {
            finish()
        }

        binding.btnGuardar.setOnClickListener {
            if (validarFormulario()) ejecutarAccion()
        }

        binding.btnEliminar.setOnClickListener {
            mostrarDialogoEliminar()
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

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.tipos_recursos, R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapter
    }

    private fun prellenarDatos(recurso: Recurso) {
        binding.tvTitleAdd.text = "Editar Recurso"
        binding.btnGuardar.text = "Actualizar Cambios"
        binding.btnEliminar.visibility = View.VISIBLE

        binding.etTitulo.setText(recurso.titulo)
        binding.etDescripcion.setText(recurso.descripcion)
        binding.etEnlace.setText(recurso.enlace)
        binding.etImagen.setText(recurso.imagen)

        val adapter = binding.spinnerTipo.adapter as ArrayAdapter<String>
        val posicion = (0 until adapter.count).firstOrNull { adapter.getItem(it) == recurso.tipo } ?: 0
        binding.spinnerTipo.setSelection(posicion)
    }

    private fun mostrarDialogoEliminar() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_confirm_delete, null)

        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarEliminar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarEliminar)

        btnConfirmar.setOnClickListener {
            dialog.dismiss()
            eliminarRecurso()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun eliminarRecurso() {
        recursoAEditar?.id?.let { id ->
            RetrofitClient.instance.deleteRecurso(id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        showCustomToast("Recurso eliminado correctamente")
                        finish()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showCustomToast("Error de red al eliminar")
                }
            })
        }
    }

    private fun validarFormulario(): Boolean {
        var valido = true
        if (binding.etTitulo.text.toString().isBlank()) {
            binding.etTitulo.error = "Campo obligatorio"
            valido = false
        }
        if (binding.etEnlace.text.toString().isBlank()) {
            binding.etEnlace.error = "Campo obligatorio"
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
                    val msg = if (recursoAEditar == null) "Recurso guardado" else "Recurso editado"
                    showCustomToast(msg)
                    finish()
                }
            }
            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                showCustomToast("Error de conexión")
            }
        })
    }
}