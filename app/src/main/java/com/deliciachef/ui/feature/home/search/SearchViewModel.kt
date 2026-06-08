package com.deliciachef.ui.feature.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.deliciachef.core.common.Resource
import com.deliciachef.domain.model.Recipe
import com.deliciachef.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Definimos los estados posibles de la interfaz grafica
sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val recipes: List<Recipe>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel(
    private val getRecipesUseCase: GetRecipesUseCase
) : ViewModel() {

    // cambios en tiempo real o al dar click????
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Al abrir la pantalla, buscamos algunas recetas por defecto
        searchRecipes(cuisine = "italian")
    }

    fun searchRecipes(query: String? = null, cuisine: String? = null) {
        // viewModelScope se asegura de que si el usuario cierra la app, la descarga se cancele
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading

            when (val result = getRecipesUseCase(search = query, cuisine = cuisine)) {
                is Resource.Success -> {
                    val recipes = result.data ?: emptyList()
                    if (recipes.isEmpty()) {
                        _uiState.value = SearchUiState.Error("No se encontraron recetas.")
                    } else {
                        _uiState.value = SearchUiState.Success(recipes)
                    }
                }
                is Resource.Error -> {
                    _uiState.value = SearchUiState.Error(result.message ?: "Ocurrió un error inesperado.")
                }
                is Resource.Loading -> {

                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(
    private val getRecipesUseCase: GetRecipesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(getRecipesUseCase) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}