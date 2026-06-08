package com.deliciachef.domain.usecase

import com.deliciachef.domain.repository.SavedRecipeRepository

class CheckIfRecipeSavedUseCase(private val repository: SavedRecipeRepository) {
    suspend operator fun invoke(id: Int) = repository.isRecipeSaved(id)
}