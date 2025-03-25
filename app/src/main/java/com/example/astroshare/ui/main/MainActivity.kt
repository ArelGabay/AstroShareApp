package com.example.astroshare.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.data.local.db.entity.UserEntity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // (1) ROOM DEBUG SNIPPET: Insert & retrieve a test user
        lifecycleScope.launch {
            val user = UserEntity(
                id = "testId",
                displayName = "Test User",
                email = "test@example.com",
                bio = "This is a test bio.",
                lastLogin = System.currentTimeMillis(),
                loggedIn = true,
                postsCount = 0,
                profilePicture = ""
            )

            val db = (application as AstroShareApp).database
            db.userDao().insertUser(user)

            val retrieved = db.userDao().getUserById("testId")
            Log.d("RoomTest", "Retrieved user: $retrieved")
        }

        // (2) SETUP NAVIGATION
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // (3) SETUP BOTTOM NAVIGATION
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)
        NavigationUI.setupWithNavController(bottomNavView, navController)

        // (4) HIDE BOTTOM NAV ON LOGIN/REGISTER SCREENS
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment,
                R.id.registerFragment -> {
                    bottomNavView.visibility = View.GONE
                }
                else -> {
                    bottomNavView.visibility = View.VISIBLE
                }
            }
        }
    }
}