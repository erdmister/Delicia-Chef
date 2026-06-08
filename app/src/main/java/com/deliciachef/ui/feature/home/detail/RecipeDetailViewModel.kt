package com.deliciachef.ui.feature.home.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.deliciachef.core.common.Resource
import com.deliciachef.data.remote.firebase.SavedRecipeDto
import com.deliciachef.domain.model.Recipe
import com.deliciachef.domain.usecase.CheckIfRecipeSavedUseCase
import com.deliciachef.domain.usecase.DeleteSavedRecipeUseCase
import com.deliciachef.domain.usecase.GetRecipeByIdUseCase
import com.deliciachef.domain.usecase.SaveRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

sealed class RecipeDetailUiState {
    object Loading : RecipeDetailUiState()
    data class Success(val recipe: Recipe) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

class RecipeDetailViewModel(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val saveRecipeUseCase: SaveRecipeUseCase,
    private val deleteSavedRecipeUseCase: DeleteSavedRecipeUseCase,
    private val checkIfRecipeSavedUseCase: CheckIfRecipeSavedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    // saber si la receta está guardada o no
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun loadRecipe(id: Int) {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading

            // Verificamos en Firebase si el usuario ya la tenía guardada
            _isSaved.value = checkIfRecipeSavedUseCase(id)

            when (val result = getRecipeByIdUseCase(id)) {
                is Resource.Success -> {
                    result.data?.let {
                        _uiState.value = RecipeDetailUiState.Success(it)
                    } ?: run { _uiState.value = RecipeDetailUiState.Error("Receta vacía") }
                }
                is Resource.Error -> _uiState.value = RecipeDetailUiState.Error(result.message ?: "Error desconocido")
                is Resource.Loading -> { }
            }
        }
    }

    fun toggleSaveRecipe() {
        val currentState = _uiState.value
        if (currentState is RecipeDetailUiState.Success) {
            val recipe = currentState.recipe
            val currentlySaved = _isSaved.value

            viewModelScope.launch {
                if (currentlySaved) {
                    val result = deleteSavedRecipeUseCase(recipe.id)
                    if (result is Resource.Success) {
                        _isSaved.update { false }
                    }
                } else {
                    val dto = SavedRecipeDto(
                        id = recipe.id,
                        name = recipe.name,
                        cuisine = recipe.cuisine,
                        difficulty = recipe.difficulty,
                        totalTime = recipe.prepTime + recipe.cookTime
                    )
                    val result = saveRecipeUseCase(dto)
                    if (result is Resource.Success) {
                        _isSaved.update { true }
                    }
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class RecipeDetailViewModelFactory(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val saveRecipeUseCase: SaveRecipeUseCase,
    private val deleteSavedRecipeUseCase: DeleteSavedRecipeUseCase,
    private val checkIfRecipeSavedUseCase: CheckIfRecipeSavedUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            return RecipeDetailViewModel(
                getRecipeByIdUseCase,
                saveRecipeUseCase,
                deleteSavedRecipeUseCase,
                checkIfRecipeSavedUseCase
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}