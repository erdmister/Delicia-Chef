package com.deliciachef.domain.usecase

import com.deliciachef.core.common.Resource
import com.deliciachef.domain.model.Recipe
import com.deliciachef.domain.repository.RecipeRepository

class GetRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        search: String? = null,
        cuisine: String? = null,
        mealType: String? = null,
        difficulty: String? = null,
        page: Int = 1
    ): Resource<List<Recipe>> {

        // Implementar los de mas aqui de momento solo tenemos esto
        return repository.getRecipes(
            search = search,
            cuisine = cuisine,
            mealType = mealType,
            difficulty = difficulty,
            page = page
        )
    }
}