package com.deliciachef.data.repository

import com.deliciachef.core.common.Resource
import com.deliciachef.data.remote.api.RecipeApiService
import com.deliciachef.data.remote.dto.IngredientDto
import com.deliciachef.data.remote.dto.RecipeDto
import com.deliciachef.domain.model.Ingredient
import com.deliciachef.domain.model.Recipe
import com.deliciachef.domain.repository.RecipeRepository
import retrofit2.HttpException
import java.io.IOException

class RecipeRepositoryImpl(
    private val apiService: RecipeApiService
) : RecipeRepository {

    override suspend fun getRecipes(
        search: String?,
        cuisine: String?,
        mealType: String?,
        difficulty: String?,
        page: Int
    ): Resource<List<Recipe>> {
        return try {
            val response = apiService.getRecipes(
                search = search,
                cuisine = cuisine,
                mealType = mealType,
                difficulty = difficulty,
                page = page
            )
            // Convertimos la lista de DTOs a lista de Dominoi
            val recipes = response.data.map { it.toDomain() }
            Resource.Success(recipes)
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "Ocurrio un error inesperado en el servidor")
        } catch (e: IOException) {
            Resource.Error("No se pudo conectar al servidor.")
        }
    }

    override suspend fun getRecipeById(id: Int): Resource<Recipe> {
        return try {
            val response = apiService.getRecipeById(id)
            Resource.Success(response.data.toDomain())
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "No se encontro la receta solicitada")
        } catch (e: IOException) {
            Resource.Error("Error de red. Asegurate de estar conectado a internet")
        }
    }

    override suspend fun getRandomRecipe(cuisine: String?): Resource<Recipe> {
        return try {
            val response = apiService.getRandomRecipe(cuisine = cuisine)
            Resource.Success(response.data.toDomain())
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "Error al intentar obtener una receta aleatoria")
        } catch (e: IOException) {
            Resource.Error("Error de red. Asegúrate de estar conectado a internet")
        }
    }
}

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        name = this.name,
        description = this.description ?: "",
        difficulty = this.difficulty ?: "easy",
        mealType = this.mealType ?: "",
        cuisine = this.cuisine ?: "",
        dietaryTags = this.dietaryTags ?: emptyList(),
        servings = this.servings ?: 1,
        prepTime = this.prepTime ?: 0,
        cookTime = this.cookTime ?: 0,
        caloriesPerServing = this.caloriesPerServing ?: 0,
        protein = this.protein ?: 0,
        instructions = this.instructions ?: emptyList(),
        ingredients = this.ingredients?.map { it.toDomain() } ?: emptyList()
    )
}

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        category = this.category ?: "",
        quantity = this.quantity ?: 0.0,
        unit = this.unit ?: "",
        optional = this.optional
    )
}