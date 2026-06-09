package com.deliciachef.ui.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.deliciachef.R
import com.deliciachef.databinding.FragmentHomeContainerBinding

class HomeContainerFragment : Fragment(R.layout.fragment_home_container) {

    private var _binding: FragmentHomeContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menu = binding.bottomNavigation.menu

            for (i in 0 until menu.size()) {
                menu.getItem(i).isCheckable = true
            }

            when (destination.id) {
                R.id.searchFragment -> menu.findItem(R.id.searchFragment)?.isChecked = true
                R.id.savedFragment -> menu.findItem(R.id.savedFragment)?.isChecked = true
                R.id.calendarFragment -> menu.findItem(R.id.calendarFragment)?.isChecked = true
                R.id.profileFragment -> menu.findItem(R.id.profileFragment)?.isChecked = true
                else -> {
                    for (i in 0 until menu.size()) {
                        val item = menu.getItem(i)
                        if (!item.isChecked) {
                            item.isCheckable = false
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