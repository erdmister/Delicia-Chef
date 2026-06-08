package com.deliciachef.domain.repository

import com.deliciachef.core.common.Resource
import com.deliciachef.data.remote.firebase.SavedRecipeDto
import kotlinx.coroutines.flow.Flow

interface SavedRecipeRepository {
    // Usamos Flow porque Firebase actualiza los datos en tiempo real, si se actualiza en un telefono diferente y se accede en otro se ve el cambio.
    fun getSavedRecipes(): Flow<Resource<List<SavedRecipeDto>>>

    suspend fun saveRecipe(recipe: SavedRecipeDto): Resource<Unit>

    suspend fun deleteRecipe(recipeId: Int): Resource<Unit>

    suspend fun isRecipeSaved(recipeId: Int): Boolean
}