package com.deliciachef.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecipeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("difficulty") val difficulty: String?,
    @SerializedName("meal_type") val mealType: String?,
    @SerializedName("cuisine") val cuisine: String?,
    @SerializedName("dietary_tags") val dietaryTags: List<String>?,
    @SerializedName("servings") val servings: Int?,
    @SerializedName("prep_time") val prepTime: Int?,
    @SerializedName("cook_time") val cookTime: Int?,
    @SerializedName("calories_per_serving") val caloriesPerServing: Int?,
    @SerializedName("protein") val protein: Int?,
    @SerializedName("instructions") val instructions: List<String>?,
    @SerializedName("ingredients") val ingredients: List<IngredientDto>?
)