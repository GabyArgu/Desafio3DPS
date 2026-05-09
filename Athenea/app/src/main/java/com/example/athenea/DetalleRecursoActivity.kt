package com.example.athenea

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.athenea.databinding.ActivityDetalleRecursoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleRecursoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleRecursoBinding
    private lateinit var recurso: Recurso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleRecursoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar el recurso enviado
        recurso = intent.getSerializableExtra("RECURSO") as Recurso

        mostrarDatos()

        // BOTÓN CERRAR: Ahora usa finish() para asegurar que regrese
        binding.btnCerrar.setOnClickListener {
            finish()
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                calcularNuevoPromedio(rating.toDouble())
            }
        }

        binding.btnGuardarFavorito.setOnClickListener { toggleFavorito() }

        binding.btnVerEnlace.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recurso.enlace))
            startActivity(intent)
        }
    }

    private fun mostrarDatos() {
        binding.tvDetalleTitulo.text = recurso.titulo
        binding.tvDetalleTipo.text = recurso.tipo
        binding.tvDetalleDescripcion.text = recurso.descripcion
        binding.ratingBar.rating = recurso.rating.toFloat()

        binding.tvConteoVotos.text = getString(R.string.conteo_votos_format, recurso.voteCount, recurso.rating)

        actualizarBotonFavorito()

        Glide.with(this)
            .load(recurso.imagen)
            .placeholder(R.drawable.logoaten)
            .into(binding.ivDetalleImagen)
    }

    private fun calcularNuevoPromedio(nuevaCalificacion: Double) {
        val totalVotosAnterior = recurso.voteCount
        val promedioAnterior = recurso.rating

        val nuevoTotalVotos = totalVotosAnterior + 1
        val nuevoPromedio = ((promedioAnterior * totalVotosAnterior) + nuevaCalificacion) / nuevoTotalVotos

        val promedioRedondeado = String.format("%.1f", nuevoPromedio).toDouble()

        recurso.rating = promedioRedondeado
        recurso.voteCount = nuevoTotalVotos

        sincronizarConServidor("¡Gracias por calificar!")
    }

    private fun sincronizarConServidor(mensaje: String) {
        RetrofitClient.instance.updateRecurso(recurso.id!!, recurso).enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    mostrarDatos()
                    showCustomToast(mensaje)
                }
            }
            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                showCustomToast("Error de conexión")
            }
        })
    }

    private fun toggleFavorito() {
        recurso.isFavorite = !recurso.isFavorite
        val msg = if (recurso.isFavorite) "Añadido a favoritos" else "Quitado de favoritos"
        sincronizarConServidor(msg)
    }

    private fun actualizarBotonFavorito() {
        if (recurso.isFavorite) {
            binding.btnGuardarFavorito.text = getString(R.string.btn_quitar_favorito)
            binding.btnGuardarFavorito.backgroundTintList = getColorStateList(android.R.color.darker_gray)
        } else {
            binding.btnGuardarFavorito.text = getString(R.string.btn_guardar_favorito)
            binding.btnGuardarFavorito.backgroundTintList = getColorStateList(android.R.color.holo_red_light)
        }
    }

    private fun showCustomToast(message: String) {
        val layout = LayoutInflater.from(this).inflate(R.layout.custom_toast, null)
        layout.findViewById<TextView>(R.id.toast_text).text = message
        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 150)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}