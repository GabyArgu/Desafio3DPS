package com.example.athenea

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.athenea.databinding.ItemRecursoBinding

class RecursosAdapter(
    private var recursos: List<Recurso>,
    private val onFavoriteClick: (Recurso) -> Unit,
    private val onItemClick: (Recurso) -> Unit
) : RecyclerView.Adapter<RecursosAdapter.RecursoViewHolder>() {

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

        // Lógica de colores y estados de la estrella
        if (recurso.isFavorite) {
            holder.binding.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on)
            // Rosado potente (#FF017E)
            holder.binding.btnFavorite.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF017E"))
        } else {
            holder.binding.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off)
            // Rosado clarito (#E19CBB)
            holder.binding.btnFavorite.imageTintList = ColorStateList.valueOf(Color.parseColor("#E19CBB"))
        }

        Glide.with(holder.itemView.context)
            .load(recurso.imagen)
            .placeholder(R.drawable.logoaten)
            .error(R.drawable.logoaten)
            .into(holder.binding.ivRecurso)

        holder.binding.btnFavorite.setOnClickListener {
            onFavoriteClick(recurso)
        }

        holder.itemView.setOnClickListener {
            onItemClick(recurso)
        }
    }

    override fun getItemCount(): Int = recursos.size

    fun actualizarLista(nuevaLista: List<Recurso>) {
        this.recursos = nuevaLista
        notifyDataSetChanged()
    }
}