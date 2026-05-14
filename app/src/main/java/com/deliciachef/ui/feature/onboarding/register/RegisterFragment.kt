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
import androidx.navigation.fragment.findNavController
import com.deliciachef.R
import com.deliciachef.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels {
        com.deliciachef.ui.feature.onboarding.AuthViewModelFactory()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        // Botón volver
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Botón registrar
        binding.btnRegisterAction.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            viewModel.onEvent(RegisterEvent.SubmitRegister(name, email, password))
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    // Manejar interfaz de carga
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnRegisterAction.isEnabled = !state.isLoading

                    // Manejo de éxito
                    if (state.isSuccess) {
                        Toast.makeText(requireContext(), "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()

                        // NOTA: Usamos el ID de acción de tu nav_onboarding.xml
                        findNavController().navigate(R.id.action_registerFragment_to_personalInfoFragment)
                    }

                    // Manejo de errores
                    state.errorMessage?.let { error ->
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        viewModel.clearError() // Evita que el Toast se repita al rotar la pantalla
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevención obligatoria de Memory Leaks
    }
}