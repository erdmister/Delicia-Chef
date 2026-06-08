package com.deliciachef.ui.feature.home.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.deliciachef.databinding.FragmentRecipeDetailBinding
import com.deliciachef.data.remote.api.RetrofitClient
import com.deliciachef.data.repository.RecipeRepositoryImpl
import com.deliciachef.domain.model.Recipe
import com.deliciachef.domain.usecase.GetRecipeByIdUseCase
import kotlinx.coroutines.launch

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeDetailViewModel by viewModels {
        val apiService = RetrofitClient.recipeApiService
        val repository = RecipeRepositoryImpl(apiService)
        val useCase = GetRecipeByIdUseCase(repository)
        RecipeDetailViewModelFactory(useCase)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        // recupearmos el ID que enviamos desde el SearchFragment
        val recipeId = arguments?.getInt("recipeId") ?: -1

        if (recipeId != -1) {
            // pedimos al ViewModel que muestr la receta
            viewModel.loadRecipe(recipeId)
        } else {
            Toast.makeText(requireContext(), "Error al cargar la receta", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun setupToolbar() {
        // Flecha para rehresar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is RecipeDetailUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.contentScroll.visibility = View.GONE
                        }
                        is RecipeDetailUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentScroll.visibility = View.VISIBLE
                            bindRecipeToUi(state.recipe)
                        }
                        is RecipeDetailUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun bindRecipeToUi(recipe: Recipe) {
        binding.tvDetailTitle.text = recipe.name
        binding.tvDetailDescription.text = recipe.description.ifEmpty { "Sin descripción disponible." }

        val totalTime = recipe.prepTime + recipe.cookTime
        binding.tvDetailTime.text = "⏱ $totalTime min"
        binding.tvDetailServings.text = "👥 ${recipe.servings} porciones"

        val ingredientsText = recipe.ingredients.joinToString(separator = "\n") { ingredient ->
            "• ${ingredient.quantity} ${ingredient.unit} de ${ingredient.name}"
        }
        binding.tvDetailIngredients.text = ingredientsText.ifEmpty { "Ingredientes no detallados." }

        val instructionsText = recipe.instructions.mapIndexed { index, step ->
            "${index + 1}. $step"
        }.joinToString(separator = "\n\n")
        binding.tvDetailInstructions.text = instructionsText.ifEmpty { "Instrucciones no detalladas." }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}