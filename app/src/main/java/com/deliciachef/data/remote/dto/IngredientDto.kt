package com.deliciachef.data.remote.dto

import com.google.gson.annotations.SerializedName

data class IngredientDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String?,
    @SerializedName("quantity") val quantity: Double?,
    @SerializedName("unit") val unit: String?,
    @SerializedName("optional") val optional: Boolean
)