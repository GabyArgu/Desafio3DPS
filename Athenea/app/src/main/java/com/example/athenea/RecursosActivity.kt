package com.example.athenea

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athenea.databinding.ActivityRecursosBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecursosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecursosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecursosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRole = intent.getStringExtra("USER_ROLE") ?: "Estudiante"

        // El FAB solo es para docentes
        binding.fabAdd.visibility = if (userRole == "Docente") View.VISIBLE else View.GONE

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddRecursoActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
    }

    // Usamos onResume para que la lista se refresque al volver de AddRecursoActivity
    override fun onResume() {
        super.onResume()
        cargarRecursosDesdeApi()
    }

    private fun setupRecyclerView() {
        binding.rvRecursos.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarRecursosDesdeApi() {
        RetrofitClient.instance.getRecursos().enqueue(object : Callback<List<Recurso>> {
            override fun onResponse(call: Call<List<Recurso>>, response: Response<List<Recurso>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    binding.rvRecursos.adapter = RecursoAdapter(lista)
                } else {
                    Toast.makeText(this@RecursosActivity, "Error al sincronizar datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Recurso>>, t: Throwable) {
                Toast.makeText(this@RecursosActivity, "Sin conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}