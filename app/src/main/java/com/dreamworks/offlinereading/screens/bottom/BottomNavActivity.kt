package com.dreamworks.offlinereading.screens.bottom

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dreamworks.offlinereading.databinding.ActivityBottomNavBinding
import com.dreamworks.offlinereading.R

class BottomNavActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_nav)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Toast.makeText(
                applicationContext,
               destination.displayName,
                Toast.LENGTH_SHORT
            ).show()
        }
        navView.setupWithNavController(navController)
    }
}