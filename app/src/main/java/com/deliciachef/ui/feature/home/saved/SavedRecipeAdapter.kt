package com.deliciachef.ui.feature.home.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deliciachef.databinding.ItemRecipeBinding
import com.deliciachef.data.remote.firebase.SavedRecipeDto
import com.bumptech.glide.Glide

class SavedRecipeAdapter(
    private val onClick: (Int) -> Unit,
    private val onLongClick: (SavedRecipeDto) -> Unit
) : ListAdapter<SavedRecipeDto, SavedRecipeAdapter.SavedViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavedViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SavedRecipeDto) {
            // 1. Vinculamos los textos con los nuevos IDs estéticos
            binding.tvRecipeTitle.text = item.name
            binding.tvRecipeCuisine.text = item.cuisine
            binding.tvRecipeTime.text = "⏱ ${item.totalTime} min"
            binding.tvRecipeDifficulty.text = "❤️ Guardada"

            // 2. 🔥 TRUCO MÁGICO: Cargamos la imagen dinámica usando el ID de Firebase 🔥
            val mockImageUrl = "https://loremflickr.com/600/400/food,meal?lock=${item.id}"

            Glide.with(binding.root.context)
                .load(mockImageUrl)
                .placeholder(android.R.drawable.ic_menu_report_image) // Imagen de espera
                .error(android.R.drawable.ic_menu_gallery)           // Imagen de error
                .centerCrop()
                .into(binding.ivRecipeImage) // ID de la imagen en tu item_recipe.xml

            // 3. Configuración de los clics (Toque rápido y toque largo)
            binding.root.setOnClickListener {
                onClick(item.id)
            }

            binding.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SavedRecipeDto>() {
        override fun areItemsTheSame(oldItem: SavedRecipeDto, newItem: SavedRecipeDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SavedRecipeDto, newItem: SavedRecipeDto) = oldItem == newItem
    }
}