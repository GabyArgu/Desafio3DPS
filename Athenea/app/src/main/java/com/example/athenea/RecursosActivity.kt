package com.example.athenea

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athenea.databinding.ActivityRecursosBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecursosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecursosBinding
    private var listaOriginal = mutableListOf<Recurso>()
    private lateinit var adapter: RecursosAdapter
    private var userRole: String = "Estudiante"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecursosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar el rol del usuario desde el Intent
        userRole = intent.getStringExtra("USER_ROLE") ?: "Estudiante"

        // Control de visibilidad del botón flotante según el rol
        binding.fabAdd.visibility = if (userRole == "Docente") View.VISIBLE else View.GONE

        setupRecyclerView()
        setupSearchView()
        cargarRecursos()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddRecursoActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = RecursosAdapter(
            recursos = mutableListOf(),
            onFavoriteClick = { recurso ->
                toggleFavorite(recurso)
            },
            onItemClick = { recurso ->
                if (userRole == "Docente") {
                    // El docente va a la pantalla de edición
                    val intent = Intent(this, AddRecursoActivity::class.java)
                    intent.putExtra("RECURSO_EDITAR", recurso)
                    startActivity(intent)
                } else {
                    // El estudiante va a la pantalla de detalle para ver y calificar
                    val intent = Intent(this, DetalleRecursoActivity::class.java)
                    intent.putExtra("RECURSO", recurso)
                    startActivity(intent)
                }
            }
        )
        binding.rvRecursos.layoutManager = LinearLayoutManager(this)
        binding.rvRecursos.adapter = adapter
    }

    private fun toggleFavorite(recurso: Recurso) {
        val idRecurso = recurso.id ?: return

        val originalState = recurso.isFavorite
        val nuevoEstado = !originalState

        // Cambio visual inmediato
        recurso.isFavorite = nuevoEstado
        adapter.notifyDataSetChanged()

        RetrofitClient.instance.updateRecurso(idRecurso, recurso).enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    val mensaje = if (nuevoEstado) "Añadido a favoritos" else "Quitado de favoritos"
                    showCustomToast(mensaje)
                } else {
                    // Revertir en caso de error en el servidor
                    recurso.isFavorite = originalState
                    adapter.notifyDataSetChanged()
                    showCustomToast("Error al sincronizar con el servidor")
                }
            }

            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                // Revertir en caso de fallo de red
                recurso.isFavorite = originalState
                adapter.notifyDataSetChanged()
                showCustomToast("Sin conexión: No se guardó el cambio")
            }
        })
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrar(newText)
                return true
            }
        })
    }

    private fun filtrar(texto: String?) {
        val query = texto?.lowercase() ?: ""
        val filtrados = if (query.isEmpty()) {
            listaOriginal
        } else {
            listaOriginal.filter {
                it.titulo.lowercase().contains(query) ||
                        it.tipo.lowercase().contains(query) ||
                        it.id.toString() == query
            }
        }
        adapter.actualizarLista(filtrados)
    }

    private fun cargarRecursos() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getRecursos().enqueue(object : Callback<List<Recurso>> {
            override fun onResponse(call: Call<List<Recurso>>, response: Response<List<Recurso>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    listaOriginal = response.body()?.toMutableList() ?: mutableListOf()
                    adapter.actualizarLista(listaOriginal)
                }
            }

            override fun onFailure(call: Call<List<Recurso>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                showCustomToast("Error al cargar recursos")
            }
        })
    }

    private fun showCustomToast(message: String) {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)
        val text = layout.findViewById<TextView>(R.id.toast_text)
        text.text = message

        val customToast = Toast(applicationContext)
        customToast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 150)
        customToast.duration = Toast.LENGTH_SHORT
        customToast.view = layout
        customToast.show()
    }

    override fun onResume() {
        super.onResume()
        cargarRecursos()
    }
}