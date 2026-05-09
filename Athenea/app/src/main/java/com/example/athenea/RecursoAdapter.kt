package com.example.athenea

import android.view.LayoutInflater
import android.view.ViewGroup
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

        // Lógica de la estrella: rellena si es favorito, vacía si no
        val starIcon = if (recurso.isFavorite) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        holder.binding.btnFavorite.setImageResource(starIcon)

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