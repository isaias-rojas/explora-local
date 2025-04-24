package com.example.exploralocal.presentation.main

import com.example.exploralocal.R


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.exploralocal.databinding.ActivityMainBinding
import com.example.exploralocal.presentation.list.PlaceListFragment
import com.example.exploralocal.presentation.map.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // Set default fragment if this is the first time loading the activity
        if (savedInstanceState == null) {
            showMapFragment()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_map -> {
                    showMapFragment()
                    true
                }
                R.id.menu_places -> {
                    showPlaceListFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun showMapFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MapFragment())
            .commit()
    }

    private fun showPlaceListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlaceListFragment())
            .commit()
    }
}