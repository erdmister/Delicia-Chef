package com.deliciachef.ui.feature.onboarding.register

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
import com.deliciachef.R
import com.deliciachef.databinding.FragmentPersonalInfoBinding
import com.google.android.material.datepicker.MaterialDatePicker // IMPORTANTE
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat // IMPORTANTE
import java.util.Date // IMPORTANTE
import java.util.Locale // IMPORTANTE

class PersonalInfoFragment : Fragment(R.layout.fragment_personal_info) {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonalInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {

        // -----------------------------------------------------------
        // CÓDIGO NUEVO: Escuchar el clic en la fecha de nacimiento
        // -----------------------------------------------------------
        binding.etBirthdate.setOnClickListener {
            showDatePicker()
        }

        binding.btnFinishProfile.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val birthdate = binding.etBirthdate.text.toString().trim()

            viewModel.onEvent(
                PersonalInfoEvent.SubmitInfo(firstName, lastName, username, phone, birthdate)
            )
        }
    }

    // -----------------------------------------------------------
    // CÓDIGO NUEVO: Función para mostrar el calendario de Material 3
    // -----------------------------------------------------------
    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona tu fecha de nacimiento")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Cuando el usuario le da "Aceptar" en el calendario
        datePicker.addOnPositiveButtonClickListener { selection ->
            // Convertimos los milisegundos seleccionados a formato de texto "DD/MM/AAAA"
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateString = sdf.format(Date(selection))

            // Ponemos el texto en nuestro campo visual
            binding.etBirthdate.setText(dateString)
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnFinishProfile.isEnabled = !state.isLoading

                    if (state.isSuccess) {
                        Toast.makeText(requireContext(), "¡Perfil creado en Delicia Chef!", Toast.LENGTH_SHORT).show()
                    }

                    state.errorMessage?.let { error ->
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
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