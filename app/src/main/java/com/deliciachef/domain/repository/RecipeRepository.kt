package com.deliciachef.domain.repository

import com.deliciachef.core.common.Resource
import com.deliciachef.domain.model.Recipe

interface RecipeRepository {

    suspend fun getRecipes(
        search: String? = null,
        cuisine: String? = null,
        mealType: String? = null,
        difficulty: String? = null,
        page: Int = 1
    ): Resource<List<Recipe>>

    suspend fun getRecipeById(id: Int): Resource<Recipe>

    suspend fun getRandomRecipe(cuisine: String? = null): Resource<Recipe>
}