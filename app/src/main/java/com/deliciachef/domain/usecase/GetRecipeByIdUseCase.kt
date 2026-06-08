package com.deliciachef.domain.usecase

import com.deliciachef.core.common.Resource
import com.deliciachef.domain.model.Recipe
import com.deliciachef.domain.repository.RecipeRepository

class GetRecipeByIdUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: Int): Resource<Recipe> {
        return repository.getRecipeById(id)
    }
}