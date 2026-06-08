package com.deliciachef.data.remote.firebase

data class SavedRecipeDto(
    val id: Int = 0,
    val name: String = "",
    val cuisine: String = "",
    val difficulty: String = "",
    val totalTime: Int = 0,
    val savedAt: Long = System.currentTimeMillis()
)