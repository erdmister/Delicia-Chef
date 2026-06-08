package com.deliciachef.domain.usecase

import com.deliciachef.domain.repository.SavedRecipeRepository

class GetSavedRecipesUseCase(private val repository: SavedRecipeRepository) {
    operator fun invoke() = repository.getSavedRecipes()
}