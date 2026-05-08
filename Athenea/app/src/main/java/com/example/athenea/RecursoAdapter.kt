package com.example.athenea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.athenea.databinding.ItemRecursoBinding

class RecursoAdapter(private val recursos: List<Recurso>) :
    RecyclerView.Adapter<RecursoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRecursoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecursoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recurso = recursos[position]
        holder.binding.apply {
            tvTitulo.text = recurso.titulo
            tvTipo.text = recurso.tipo
            tvDescripcion.text = recurso.descripcion

            Glide.with(holder.itemView.context)
                .load(recurso.imagen)
                .into(ivRecurso)
        }
    }

    override fun getItemCount(): Int = recursos.size
}