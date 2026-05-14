package com.deliciachef.ui.feature.onboarding.recovery

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
import com.deliciachef.databinding.FragmentForgotPasswordBinding
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgotPasswordViewModel by viewModels {
        com.deliciachef.ui.feature.onboarding.AuthViewModelFactory()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        // Botón regresar
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Botón enviar instrucciones
        binding.btnSendInstructions.setOnClickListener {
            val email = binding.etEmailForgot.text.toString().trim()
            viewModel.onEvent(ForgotPasswordEvent.SubmitEmail(email))
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    // Manejo de la interfaz de carga
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnSendInstructions.isEnabled = !state.isLoading

                    // Manejo de éxito
                    if (state.isSuccess) {
                        Toast.makeText(
                            requireContext(),
                            "Si el correo existe, recibirás un enlace de recuperación.",
                            Toast.LENGTH_LONG
                        ).show()

                        // Regresamos automáticamente a la pantalla de Login
                        findNavController().popBackStack()
                    }

                    // Manejo de errores
                    state.errorMessage?.let { error ->
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        viewModel.clearError() // Limpiamos para evitar doble ejecución de error
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpieza para evitar fugas de memoria
    }
}