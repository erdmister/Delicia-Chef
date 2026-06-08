package com.deliciachef.ui.feature.home.search

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
import com.deliciachef.databinding.FragmentSearchBinding
import com.deliciachef.data.remote.api.RetrofitClient
import com.deliciachef.data.repository.RecipeRepositoryImpl
import com.deliciachef.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.launch
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.content.Context
import androidx.navigation.fragment.findNavController
import com.deliciachef.R

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels {
        val apiService = RetrofitClient.recipeApiService
        val repository = RecipeRepositoryImpl(apiService)
        val useCase = GetRecipesUseCase(repository)
        SearchViewModelFactory(useCase)
    }

    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter { recipe ->
            // creamos el paqeute de la receta usando un id
            val bundle = Bundle().apply {
                putInt("recipeId", recipe.id)
            }
            // Navegamos al detalle entregando el paquete
            findNavController().navigate(
                R.id.action_searchFragment_to_recipeDetailFragment,
                bundle
            )
        }
        binding.rvRecipes.adapter = recipeAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->
                    when (state) {
                        is SearchUiState.Initial -> {

                        }
                        is SearchUiState.Loading -> {
                            // Mostramos la bolita de carga y ocultamos lo demas
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvRecipes.visibility = View.GONE
                            binding.tvErrorMessage.visibility = View.GONE
                        }
                        is SearchUiState.Success -> {
                            // Ocultamos carga, mostramos lista y le pasamos las recetas al adaptador
                            binding.progressBar.visibility = View.GONE
                            binding.tvErrorMessage.visibility = View.GONE
                            binding.rvRecipes.visibility = View.VISIBLE

                            recipeAdapter.submitList(state.recipes)
                        }
                        is SearchUiState.Error -> {
                            // Mostramos el error en pantalla
                            binding.progressBar.visibility = View.GONE
                            binding.rvRecipes.visibility = View.GONE
                            binding.tvErrorMessage.visibility = View.VISIBLE

                            binding.tvErrorMessage.text = state.message
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

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString().trim()

                viewModel.searchRecipes(query = query.ifEmpty { null }, cuisine = null)

                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)

                true
            } else {
                false
            }
        }
    }
}