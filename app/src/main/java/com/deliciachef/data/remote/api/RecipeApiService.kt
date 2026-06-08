package com.deliciachef.data.remote.api

import com.deliciachef.data.remote.dto.RecipeDetailResponse
import com.deliciachef.data.remote.dto.RecipeListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {

    @GET("recipes")
    suspend fun getRecipes(
        @Query("lang") lang: String = "en",
        @Query("search") search: String? = null,
        @Query("cuisine") cuisine: String? = null,
        @Query("meal_type") mealType: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): RecipeListResponse

    @GET("recipes/{id}")
    suspend fun getRecipeById(
        @Path("id") id: Int,
        @Query("lang") lang: String = "en"
    ): RecipeDetailResponse

    @GET("recipes/random")
    suspend fun getRandomRecipe(
        @Query("lang") lang: String = "en",
        @Query("cuisine") cuisine: String? = null
    ): RecipeDetailResponse
}