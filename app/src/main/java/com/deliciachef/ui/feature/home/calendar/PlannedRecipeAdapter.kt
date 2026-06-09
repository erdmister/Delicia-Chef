package com.deliciachef.ui.feature.home.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deliciachef.databinding.ItemRecipeBinding
import com.deliciachef.data.remote.firebase.PlannedRecipeDto
import com.bumptech.glide.Glide

class PlannedRecipeAdapter(
    private val onClick: (Int) -> Unit,
    private val onLongClick: (PlannedRecipeDto) -> Unit
) : ListAdapter<PlannedRecipeDto, PlannedRecipeAdapter.PlannedViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlannedViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlannedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlannedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlannedViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlannedRecipeDto) {
            // 1. Vinculamos los datos de la receta agendada
            binding.tvRecipeTitle.text = item.name
            binding.tvRecipeCuisine.text = item.cuisine
            binding.tvRecipeTime.text = "⏱ ${item.totalTime} min"

            // En el calendario, mostramos el día de la semana agendado en la etiqueta derecha
            binding.tvRecipeDifficulty.text = item.dayOfWeek

            // 2. 🔥 TRUCO MÁGICO: Cargamos la imagen dinámica usando el ID único de la receta 🔥
            val mockImageUrl = "https://loremflickr.com/600/400/food,meal?lock=${item.id}"

            Glide.with(binding.root.context)
                .load(mockImageUrl)
                .placeholder(android.R.drawable.ic_menu_report_image) // Imagen de espera
                .error(android.R.drawable.ic_menu_gallery)           // Imagen de error si falla internet
                .centerCrop()
                .into(binding.ivRecipeImage) // ID de la vista que pusimos en tu XML

            // 3. Configuración de clics (Un toque para ir al detalle, toque largo para eliminar)
            binding.root.setOnClickListener {
                onClick(item.id)
            }

            binding.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PlannedRecipeDto>() {
        override fun areItemsTheSame(oldItem: PlannedRecipeDto, newItem: PlannedRecipeDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PlannedRecipeDto, newItem: PlannedRecipeDto) = oldItem == newItem
    }
}