package com.deliciachef.data.remote.firebase

data class PlannedRecipeDto(
    val id: Int = 0,
    val name: String = "",
    val cuisine: String = "",
    val dayOfWeek: String = "",
    val totalTime: Int = 0,
    val difficulty: String = "",
    // 🔥 CORREGIDO: Cambiamos String por Long para que coincida con Firebase 🔥
    val plannedAt: Long = 0L
)