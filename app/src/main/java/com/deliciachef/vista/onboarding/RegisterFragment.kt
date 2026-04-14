package com.deliciachef.vista.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deliciachef.R
import android.widget.ImageView
import android.widget.Button
import androidx.navigation.fragment.findNavController

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btn_back)
        val btnContinue = view.findViewById<Button>(R.id.btn_register_action)

        // Volver a la pantalla anterior (Login)
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Ir a Información Personal
        btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_personalInfo)
        }
    }
}