package com.deliciachef.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.deliciachef.R
import com.deliciachef.core.common.Resource
import com.deliciachef.data.remote.api.RetrofitClient
import com.deliciachef.data.repository.RecipeRepositoryImpl
import com.deliciachef.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    /// CCODIGO DE PRUEBA PARA TEST, REGRESAR AL CODIGO ANTERIOR DE MAIN
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testApiConnection()
    }

    private fun testApiConnection() {
        val apiService = RetrofitClient.recipeApiService
        val repository = RecipeRepositoryImpl(apiService)
        val getRecipesUseCase = GetRecipesUseCase(repository)

        lifecycleScope.launch {
            Log.d("API_TEST", "Intentando conectar con recipeapi.io...")

            val result = getRecipesUseCase(cuisine = "italian")

            when (result) {
                is Resource.Success -> {
                    val recetas = result.data ?: emptyList()
                    Log.d("API_TEST", "Recibido ${recetas.size} recetas.")

                    recetas.forEach { receta ->
                        Log.d("API_TEST", " Receta encontrada: ${receta.name} (Dificultad: ${receta.difficulty})")
                    }
                }
                is Resource.Error -> {
                    Log.e("API_TEST", "¡ERROR!: ${result.message}")
                }
                is Resource.Loading -> {

                }
            }
        }
    }
}