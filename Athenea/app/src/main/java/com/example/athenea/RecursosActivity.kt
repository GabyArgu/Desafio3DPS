package com.example.athenea

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athenea.databinding.ActivityRecursosBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecursosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecursosBinding
    private var listaOriginal = mutableListOf<Recurso>()
    private lateinit var adapter: RecursosAdapter
    private var userRole: String = "Estudiante"
    private var filtrandoSoloFavoritos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecursosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRole = intent.getStringExtra("USER_ROLE") ?: "Estudiante"

        // El botón de agregar solo es para docentes
        binding.fabAdd.visibility = if (userRole == "Docente") View.VISIBLE else View.GONE

        setupRecyclerView()
        setupSearchView()
        setupFiltrosRapidos()
        cargarRecursos()

        // Logout con confirmación tipo BottomSheet
        binding.fabLogout.setOnClickListener {
            mostrarDialogoLogout()
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddRecursoActivity::class.java))
        }
    }

    private fun mostrarDialogoLogout() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_confirm_logout, null)

        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarLogout)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarLogout)

        btnConfirmar.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun setupFiltrosRapidos() {
        binding.btnFilterAll.setOnClickListener {
            filtrandoSoloFavoritos = false
            aplicarBusquedaYFiltro(binding.searchView.query.toString())
        }
        binding.btnFilterFavs.setOnClickListener {
            filtrandoSoloFavoritos = true
            aplicarBusquedaYFiltro(binding.searchView.query.toString())
        }
    }

    private fun aplicarBusquedaYFiltro(texto: String?) {
        val query = texto?.lowercase() ?: ""
        val filtrados = if (query.isEmpty() && !filtrandoSoloFavoritos) {
            listaOriginal
        } else {
            listaOriginal.filter { recurso ->
                val coincideTexto = recurso.titulo.lowercase().contains(query) ||
                        recurso.tipo.lowercase().contains(query) ||
                        recurso.id.toString() == query
                val coincideFav = if (filtrandoSoloFavoritos) recurso.isFavorite else true
                coincideTexto && coincideFav
            }
        }
        adapter.actualizarLista(filtrados)
    }

    private fun setupRecyclerView() {
        adapter = RecursosAdapter(
            recursos = mutableListOf(),
            onFavoriteClick = { recurso -> toggleFavorite(recurso) },
            onItemClick = { recurso ->
                if (userRole == "Docente") {
                    val intent = Intent(this, AddRecursoActivity::class.java)
                    intent.putExtra("RECURSO_EDITAR", recurso)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, DetalleRecursoActivity::class.java)
                    intent.putExtra("RECURSO", recurso)
                    startActivity(intent)
                }
            }
        )
        binding.rvRecursos.layoutManager = LinearLayoutManager(this)
        binding.rvRecursos.adapter = adapter
    }

    private fun cargarRecursos() {
        binding.progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getRecursos().enqueue(object : Callback<List<Recurso>> {
            override fun onResponse(call: Call<List<Recurso>>, response: Response<List<Recurso>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    listaOriginal = response.body()?.toMutableList() ?: mutableListOf()
                    aplicarBusquedaYFiltro(binding.searchView.query.toString())
                }
            }
            override fun onFailure(call: Call<List<Recurso>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                showCustomToast("Error al cargar recursos")
            }
        })
    }

    private fun toggleFavorite(recurso: Recurso) {
        val idRecurso = recurso.id ?: return
        val originalState = recurso.isFavorite
        recurso.isFavorite = !originalState
        adapter.notifyDataSetChanged()

        RetrofitClient.instance.updateRecurso(idRecurso, recurso).enqueue(object : Callback<Recurso> {
            override fun onResponse(call: Call<Recurso>, response: Response<Recurso>) {
                if (response.isSuccessful) {
                    showCustomToast(if (recurso.isFavorite) "Añadido a favoritos" else "Quitado de favoritos")
                } else {
                    recurso.isFavorite = originalState
                    adapter.notifyDataSetChanged()
                    showCustomToast("Error al sincronizar favorito")
                }
            }
            override fun onFailure(call: Call<Recurso>, t: Throwable) {
                recurso.isFavorite = originalState
                adapter.notifyDataSetChanged()
                showCustomToast("Sin conexión")
            }
        })
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                aplicarBusquedaYFiltro(newText)
                return true
            }
        })
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

    override fun onResume() {
        super.onResume()
        cargarRecursos()
    }
}