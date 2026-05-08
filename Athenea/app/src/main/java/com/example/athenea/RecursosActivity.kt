package com.example.athenea

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athenea.databinding.ActivityRecursosBinding

class RecursosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecursosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecursosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRole = intent.getStringExtra("USER_ROLE") ?: "Estudiante"

        if (userRole == "Docente") {
            binding.fabAdd.visibility = View.VISIBLE
        } else {
            binding.fabAdd.visibility = View.GONE
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val listaTemporal = listOf(
            Recurso(1, "Libro de Kotlin", "Aprende programación móvil", "Libro", "https://google.com", "https://via.placeholder.com/150"),
            Recurso(2, "Video Tutorial MVC", "Arquitectura en Android", "Video", "https://youtube.com", "https://via.placeholder.com/150")
        )

        binding.rvRecursos.layoutManager = LinearLayoutManager(this)
        binding.rvRecursos.adapter = RecursoAdapter(listaTemporal)
    }
}