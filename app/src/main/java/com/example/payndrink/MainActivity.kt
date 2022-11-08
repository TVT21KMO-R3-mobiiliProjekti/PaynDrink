package com.example.payndrink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        setupActionBarWithNavController(hostFragment.navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val hostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        return hostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}