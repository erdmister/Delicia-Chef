package com.deliciachef.domain.usecase

import com.deliciachef.domain.repository.SavedRecipeRepository

class DeleteSavedRecipeUseCase(private val repository: SavedRecipeRepository) {
    suspend operator fun invoke(id: Int) = repository.deleteRecipe(id)
}