package com.deliciachef.domain.model

data class Ingredient(
    val id: Int,
    val name: String,
    val category: String,
    val quantity: Double,
    val unit: String,
    val optional: Boolean
)