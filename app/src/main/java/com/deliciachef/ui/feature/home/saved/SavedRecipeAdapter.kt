package com.deliciachef.ui.feature.home.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deliciachef.databinding.ItemRecipeBinding
import com.deliciachef.data.remote.firebase.SavedRecipeDto

class SavedRecipeAdapter(
    private val onRecipeClick: (Int) -> Unit
) : ListAdapter<SavedRecipeDto, SavedRecipeAdapter.SavedViewHolder>(SavedRecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavedViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: SavedRecipeDto) {
            binding.tvRecipeTitle.text = recipe.name
            binding.tvRecipeCuisine.text = recipe.cuisine.replaceFirstChar { it.uppercase() }
            binding.tvRecipeDifficulty.text = recipe.difficulty.replaceFirstChar { it.uppercase() }
            binding.tvRecipeTime.text = "${recipe.totalTime} min"

            binding.root.setOnClickListener {
                onRecipeClick(recipe.id)
            }
        }
    }

    class SavedRecipeDiffCallback : DiffUtil.ItemCallback<SavedRecipeDto>() {
        override fun areItemsTheSame(oldItem: SavedRecipeDto, newItem: SavedRecipeDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavedRecipeDto, newItem: SavedRecipeDto): Boolean {
            return oldItem == newItem
        }
    }
}