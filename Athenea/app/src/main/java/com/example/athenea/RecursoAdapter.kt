package com.example.athenea

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.athenea.databinding.ItemRecursoBinding

class RecursoAdapter(private val recursos: List<Recurso>) :
    RecyclerView.Adapter<RecursoAdapter.RecursoViewHolder>() {

    class RecursoViewHolder(val binding: ItemRecursoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        val binding = ItemRecursoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecursoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        val recurso = recursos[position]
        holder.binding.tvTitulo.text = recurso.titulo
        holder.binding.tvTipo.text = recurso.tipo
        holder.binding.tvDescripcion.text = recurso.descripcion

        // Usamos Glide para cargar la imagen y redondeamos con el estilo del XML
        Glide.with(holder.itemView.context)
            .load(recurso.imagen)
            .placeholder(R.drawable.iconoaten)
            .error(R.drawable.iconoaten)
            .into(holder.binding.ivRecurso)

        // Acción al tocar la tarjeta: Abrir edición
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AddRecursoActivity::class.java)
            // Esto solo funciona si Recurso es Serializable
            intent.putExtra("RECURSO_EDITAR", recurso)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = recursos.size
}