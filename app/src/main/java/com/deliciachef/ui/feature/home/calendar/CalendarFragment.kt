package com.deliciachef.ui.feature.home.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deliciachef.R
import com.deliciachef.databinding.FragmentCalendarBinding
import com.deliciachef.databinding.ItemRecipeBinding
import com.deliciachef.data.remote.firebase.PlannedRecipeDto
import com.deliciachef.data.repository.PlannedRecipeRepositoryImpl
import com.deliciachef.core.common.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide // IMPORTANTE: Agregamos Glide

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadCalendarData()
    }

    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter(
            onClick = { recipeId ->
                val bundle = Bundle().apply { putInt("recipeId", recipeId) }
                findNavController().navigate(R.id.action_calendarFragment_to_recipeDetailFragment, bundle)
            },
            onDeleteClick = { plannedRecipe ->
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar del menú")
                    .setMessage("¿Quieres quitar ${plannedRecipe.name} del día ${plannedRecipe.dayOfWeek}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        lifecycleScope.launch {
                            val repo = PlannedRecipeRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
                            val result = repo.deletePlannedRecipe(plannedRecipe)
                            if (result is Resource.Error) {
                                Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Eliminada del calendario", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        // 🔥 LA SOLUCIÓN A PRUEBA DE BALAS PARA EL CALENDARIO 🔥
        binding.rvPlannedRecipes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.rvPlannedRecipes.adapter = calendarAdapter
    }

    private fun loadCalendarData() {
        val repo = PlannedRecipeRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            repo.getPlannedRecipes().collect { result ->
                binding.progressBar.visibility = View.GONE
                when (result) {
                    is Resource.Success -> {
                        val list = result.data ?: emptyList()
                        if (list.isEmpty()) {
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.rvPlannedRecipes.visibility = View.GONE
                        } else {
                            binding.tvEmptyState.visibility = View.GONE
                            binding.rvPlannedRecipes.visibility = View.VISIBLE

                            val dayOrder = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
                            val sortedList = list.sortedBy { dayOrder.indexOf(it.dayOfWeek) }

                            calendarAdapter.submitList(sortedList)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CalendarAdapter(
    private val onClick: (Int) -> Unit,
    private val onDeleteClick: (PlannedRecipeDto) -> Unit
) : ListAdapter<PlannedRecipeDto, CalendarAdapter.CalendarViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CalendarViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlannedRecipeDto) {
            // Textos básicos
            binding.tvRecipeTitle.text = item.name
            binding.tvRecipeCuisine.text = item.cuisine
            binding.tvRecipeTime.text = "⏱ ${item.totalTime} min"

            // Etiqueta del día de la semana
            binding.tvRecipeDifficulty.text = "📅 ${item.dayOfWeek}"
            binding.tvRecipeDifficulty.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            binding.tvRecipeDifficulty.setTypeface(null, android.graphics.Typeface.BOLD)

            // 🔥 TRUCO MÁGICO: Cargamos la imagen dinámica usando el ID único 🔥
            val mockImageUrl = "https://loremflickr.com/600/400/food,meal?lock=${item.id}"

            Glide.with(binding.root.context)
                .load(mockImageUrl)
                .placeholder(android.R.drawable.ic_menu_report_image) // Imagen mientras carga
                .error(android.R.drawable.ic_menu_gallery)           // Imagen por si falla internet
                .centerCrop()
                .into(binding.ivRecipeImage)

            // Clic normal para ir al detalle
            binding.root.setOnClickListener { onClick(item.id) }

            // Toque largo para eliminar la receta del menú semanal
            binding.root.setOnLongClickListener {
                onDeleteClick(item)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PlannedRecipeDto>() {
        override fun areItemsTheSame(oldItem: PlannedRecipeDto, newItem: PlannedRecipeDto) =
            oldItem.id == newItem.id && oldItem.dayOfWeek == newItem.dayOfWeek
        override fun areContentsTheSame(oldItem: PlannedRecipeDto, newItem: PlannedRecipeDto) = oldItem == newItem
    }
}