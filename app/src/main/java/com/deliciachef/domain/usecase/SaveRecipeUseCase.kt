package com.deliciachef.domain.usecase

import com.deliciachef.data.remote.firebase.SavedRecipeDto
import com.deliciachef.domain.repository.SavedRecipeRepository

class SaveRecipeUseCase(private val repository: SavedRecipeRepository) {
    suspend operator fun invoke(recipe: SavedRecipeDto) = repository.saveRecipe(recipe)
}