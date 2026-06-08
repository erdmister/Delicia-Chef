package com.deliciachef.ui.feature.home.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.deliciachef.core.common.Resource
import com.deliciachef.domain.model.Recipe
import com.deliciachef.domain.usecase.GetRecipeByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecipeDetailUiState {
    object Loading : RecipeDetailUiState()
    data class Success(val recipe: Recipe) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

class RecipeDetailViewModel(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(id: Int) {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            when (val result = getRecipeByIdUseCase(id)) {
                is Resource.Success -> {
                    result.data?.let {
                        _uiState.value = RecipeDetailUiState.Success(it)
                    } ?: run {
                        _uiState.value = RecipeDetailUiState.Error("Receta vacía")
                    }
                }
                is Resource.Error -> {
                    _uiState.value = RecipeDetailUiState.Error(result.message ?: "Error desconocido")
                }
                is Resource.Loading -> { }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class RecipeDetailViewModelFactory(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            return RecipeDetailViewModel(getRecipeByIdUseCase) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}