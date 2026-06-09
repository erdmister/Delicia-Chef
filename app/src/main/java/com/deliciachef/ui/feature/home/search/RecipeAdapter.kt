package com.deliciachef.ui.feature.home.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deliciachef.databinding.ItemRecipeBinding
import com.deliciachef.domain.model.Recipe
import com.bumptech.glide.Glide

class RecipeAdapter(
    private val onClick: (Recipe) -> Unit // AQUÍ DECLARAMOS EL ONCLICK CORRECTAMENTE
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            // 1. Llenamos los textos de la tarjeta
            binding.tvRecipeTitle.text = item.name
            binding.tvRecipeCuisine.text = "👥 ${item.servings} porciones"

            val totalTime = item.prepTime + item.cookTime
            binding.tvRecipeTime.text = "⏱ $totalTime min"

            // Ocultamos la etiqueta de "Guardada" o le damos un uso (opcional)
            binding.tvRecipeDifficulty.text = "⭐ ${item.difficulty}"

            // 2. 🔥 TRUCO MAGISTRAL: Cargamos la imagen dinámica atada al ID de la receta 🔥
            val mockImageUrl = "https://loremflickr.com/600/400/food,meal?lock=${item.id}"

            Glide.with(binding.root.context)
                .load(mockImageUrl)
                .placeholder(android.R.drawable.ic_menu_report_image) // Imagen de espera
                .error(android.R.drawable.ic_menu_gallery) // Por si falla el internet
                .centerCrop()
                .into(binding.ivRecipeImage)

            // 3. Activamos el clic en toda la tarjeta para ir a los detalles
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem == newItem
    }
}