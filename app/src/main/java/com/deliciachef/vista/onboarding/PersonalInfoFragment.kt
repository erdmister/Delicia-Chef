package com.deliciachef.vista.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.deliciachef.R

import android.widget.Toast

class PersonalInfoFragment : Fragment(R.layout.fragment_personal_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnFinish = view.findViewById<Button>(R.id.btn_finish_profile)

        btnFinish.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "¡Registro completado en Delicia Chef!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}