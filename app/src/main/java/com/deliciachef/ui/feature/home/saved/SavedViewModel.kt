package com.deliciachef.ui.feature.home.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.deliciachef.core.common.Resource
import com.deliciachef.data.remote.firebase.SavedRecipeDto
import com.deliciachef.domain.usecase.GetSavedRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SavedUiState {
    object Loading : SavedUiState()
    data class Success(val recipes: List<SavedRecipeDto>) : SavedUiState()
    object Empty : SavedUiState()
    data class Error(val message: String) : SavedUiState()
}

class SavedViewModel(
    private val getSavedRecipesUseCase: GetSavedRecipesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SavedUiState>(SavedUiState.Loading)
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    init {
        loadSavedRecipes()
    }

    private fun loadSavedRecipes() {
        viewModelScope.launch {
            _uiState.value = SavedUiState.Loading

            getSavedRecipesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val recipes = result.data ?: emptyList()
                        if (recipes.isEmpty()) {
                            _uiState.value = SavedUiState.Empty
                        } else {
                            _uiState.value = SavedUiState.Success(recipes)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = SavedUiState.Error(result.message ?: "Error al cargar favoritos")
                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SavedViewModelFactory(
    private val getSavedRecipesUseCase: GetSavedRecipesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedViewModel::class.java)) {
            return SavedViewModel(getSavedRecipesUseCase) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}