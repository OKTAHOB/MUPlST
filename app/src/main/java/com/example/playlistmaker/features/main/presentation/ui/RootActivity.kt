package com.example.playlistmaker.features.main.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.AppBarConfiguration

class RootActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)
        
        // Hide bottom navigation on player screen
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideBottomNavigationDestinations = setOf(
                R.id.playerFragment,
                R.id.createPlaylistFragment
            )
            bottomNav.visibility = if (destination.id in hideBottomNavigationDestinations) {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }
        }
    }
} 