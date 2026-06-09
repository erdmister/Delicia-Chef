package com.deliciachef.ui.feature.home.saved

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
import com.deliciachef.databinding.FragmentSavedBinding
import com.deliciachef.data.repository.SavedRecipeRepositoryImpl
import com.deliciachef.domain.usecase.GetSavedRecipesUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var savedAdapter: SavedRecipeAdapter

    private val viewModel: SavedViewModel by viewModels {
        val repository = SavedRecipeRepositoryImpl(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
        val useCase = GetSavedRecipesUseCase(repository)
        SavedViewModelFactory(useCase)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        savedAdapter = SavedRecipeAdapter(
            onClick = { recipeId ->
                val bundle = Bundle().apply { putInt("recipeId", recipeId) }
                findNavController().navigate(R.id.action_savedFragment_to_recipeDetailFragment, bundle)
            },
            onLongClick = { recipeToDelete ->
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Quitar de favoritos")
                    .setMessage("¿Quieres eliminar \"${recipeToDelete.name}\" de tus recetas guardadas?")
                    .setPositiveButton("Eliminar") { _, _ ->

                        viewLifecycleOwner.lifecycleScope.launch {
                            val savedRepo = SavedRecipeRepositoryImpl(
                                com.google.firebase.auth.FirebaseAuth.getInstance(),
                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            )

                            savedRepo.deleteSavedRecipe(recipeToDelete.id)

                            Toast.makeText(requireContext(), "Eliminado de guardados", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        binding.rvSavedRecipes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.rvSavedRecipes.adapter = savedAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SavedUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvSavedRecipes.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.GONE
                        }
                        is SavedUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSavedRecipes.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                            savedAdapter.submitList(state.recipes)
                        }
                        is SavedUiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSavedRecipes.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                        }
                        is SavedUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}