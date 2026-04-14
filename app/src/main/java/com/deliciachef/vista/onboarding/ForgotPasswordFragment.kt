package com.deliciachef.vista.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.deliciachef.R

import androidx.navigation.fragment.findNavController
import android.widget.TextView

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btn_back)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}