package com.deliciachef.domain.repository

import com.deliciachef.core.common.Resource
import com.deliciachef.data.remote.firebase.PlannedRecipeDto
import kotlinx.coroutines.flow.Flow

interface PlannedRecipeRepository {
    fun getPlannedRecipes(): Flow<Resource<List<PlannedRecipeDto>>>
    suspend fun savePlannedRecipe(recipe: PlannedRecipeDto): Resource<Unit>
    suspend fun deletePlannedRecipe(recipe: PlannedRecipeDto): Resource<Unit>
}