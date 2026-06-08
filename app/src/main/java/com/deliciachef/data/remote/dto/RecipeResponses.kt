package com.deliciachef.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecipeListResponse(
    @SerializedName("data") val data: List<RecipeDto>,
    @SerializedName("meta") val meta: MetaDto?
)

data class RecipeDetailResponse(
    @SerializedName("data") val data: RecipeDto,
    @SerializedName("meta") val meta: MetaDto?
)

data class MetaDto(
    @SerializedName("current_page") val currentPage: Int?,
    @SerializedName("last_page") val lastPage: Int?,
    @SerializedName("total") val total: Int?,
    @SerializedName("language") val language: String?
)