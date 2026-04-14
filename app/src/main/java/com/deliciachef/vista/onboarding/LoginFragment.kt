package com.deliciachef.vista.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deliciachef.R

import androidx.navigation.fragment.findNavController
import android.widget.TextView

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvGoToRegister = view.findViewById<TextView>(R.id.tv_go_to_register)
        val tvForgotPassword = view.findViewById<TextView>(R.id.tv_forgot_password)

        // Navegar a Registro
        tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        // Navegar a Recuperar Contraseña
        tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgotPassword)
        }
    }
}