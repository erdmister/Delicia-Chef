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
import com.deliciachef.R
import com.deliciachef.databinding.FragmentRecipeDetailBinding
import com.deliciachef.data.remote.api.RetrofitClient
import com.deliciachef.data.repository.RecipeRepositoryImpl
import com.deliciachef.data.repository.SavedRecipeRepositoryImpl
import com.deliciachef.domain.model.Recipe
import com.deliciachef.domain.usecase.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeDetailViewModel by viewModels {
        val apiService = RetrofitClient.recipeApiService
        val recipeRepo = RecipeRepositoryImpl(apiService)
        val getByIdUseCase = GetRecipeByIdUseCase(recipeRepo)

        val savedRepo = SavedRecipeRepositoryImpl(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
        val saveUseCase = SaveRecipeUseCase(savedRepo)
        val deleteUseCase = DeleteSavedRecipeUseCase(savedRepo) // CORREGIDO: Eliminado el "Recipe" extra
        val checkSavedUseCase = CheckIfRecipeSavedUseCase(savedRepo)

        RecipeDetailViewModelFactory(getByIdUseCase, saveUseCase, deleteUseCase, checkSavedUseCase)
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
        setupListeners()

        val recipeId = arguments?.getInt("recipeId") ?: -1
        if (recipeId != -1) {
            viewModel.loadRecipe(recipeId)
        } else {
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun setupListeners() {
        binding.fabSave.setOnClickListener {
            viewModel.toggleSaveRecipe()
        }

        binding.fabCalendar.setOnClickListener {
            showDaySelectionDialog()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Hilo 1: Escucha el estado de la receta (carga/éxito/error)
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is RecipeDetailUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.contentScroll.visibility = View.GONE
                                binding.fabSave.visibility = View.GONE
                            }
                            is RecipeDetailUiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.contentScroll.visibility = View.VISIBLE
                                binding.fabSave.visibility = View.VISIBLE
                                bindRecipeToUi(state.recipe)
                            }
                            is RecipeDetailUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                // Hilo 2: Escucha de forma directa y reactiva el estado de la estrella
                launch {
                    viewModel.isSaved.collect { isSaved ->
                        // Asignamos el icono dependiendo del estado
                        val iconRes = if (isSaved) {
                            R.drawable.ic_heart_filled
                        } else {
                            R.drawable.ic_heart_outline
                        }

                        // Asignamos el color: Rojo si está guardado, Gris si no lo está
                        val iconColor = if (isSaved) "#E53935" else "#9E9E9E"

                        binding.fabSave.setImageResource(iconRes)
                        binding.fabSave.imageTintList = android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor(iconColor)
                        )
                    }
                }
            }
        }
    }

    private fun bindRecipeToUi(recipe: Recipe) {
        binding.tvDetailTitle.text = recipe.name
        binding.tvDetailDescription.text = recipe.description.ifEmpty { "Sin descripción disponible." }

        // 🔥 TRUCO MAGISTRAL PARA LAS IMÁGENES 🔥
        // Genera una foto de comida de alta calidad anclada al ID de tu receta
        val mockImageUrl = "https://loremflickr.com/800/600/food,meal?lock=${recipe.id}"

        com.bumptech.glide.Glide.with(this)
            .load(mockImageUrl)
            .placeholder(android.R.drawable.ic_menu_report_image) // Mientras carga de internet
            .into(binding.ivDetailImage)

        val totalTime = recipe.prepTime + recipe.cookTime
        binding.tvDetailTime.text = "⏱ $totalTime min"
        binding.tvDetailServings.text = "👥 ${recipe.servings} porciones"

        // Ingredientes
        val ingredientsText = recipe.ingredients.joinToString(separator = "\n") { ingredient ->
            "• ${ingredient.quantity} ${ingredient.unit} de ${ingredient.name}"
        }
        binding.tvDetailIngredients.text = ingredientsText.ifEmpty { "Ingredientes no detallados." }

        // Instrucciones
        val instructionsText = recipe.instructions.mapIndexed { index, step ->
            "${index + 1}. $step"
        }.joinToString(separator = "\n\n")
        binding.tvDetailInstructions.text = instructionsText.ifEmpty { "Instrucciones no detalladas." }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDaySelectionDialog() {
        val days = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("¿Qué día quieres agendar esta receta?")
            .setItems(days) { _, which ->
                val daySelected = days[which]

                val state = viewModel.uiState.value
                if (state is RecipeDetailUiState.Success) {
                    val recipe = state.recipe

                    lifecycleScope.launch {
                        val repo = com.deliciachef.data.repository.PlannedRecipeRepositoryImpl(
                            com.google.firebase.auth.FirebaseAuth.getInstance(),
                            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        )
                        repo.savePlannedRecipe(
                            com.deliciachef.data.remote.firebase.PlannedRecipeDto(
                                id = recipe.id,
                                name = recipe.name,
                                cuisine = recipe.cuisine,
                                dayOfWeek = daySelected,
                                totalTime = recipe.prepTime + recipe.cookTime
                            )
                        )
                        Toast.makeText(requireContext(), "Agendada para el $daySelected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}