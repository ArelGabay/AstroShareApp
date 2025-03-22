package com.example.astroshare.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.astroshare.AstroShareApp
import com.example.astroshare.R
import com.example.astroshare.data.local.db.entity.UserEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            // Create a UserEntity instance using the updated fields.
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

            // Insert the user into Room
            db.userDao().insertUser(user)

            // Retrieve the user by id and log the result
            val retrieved = db.userDao().getUserById("testId")
            Log.d("RoomTest", "Retrieved user: $retrieved")
        }
    }
}