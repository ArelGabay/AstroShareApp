package com.example.astroshare

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference

class MainActivity : AppCompatActivity() {

    // Declare variables for views
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addUserButton: Button
    private lateinit var resultMessageTextView: TextView

    // Firestore reference
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection: CollectionReference = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        addUserButton = findViewById(R.id.addUserButton)
        resultMessageTextView = findViewById(R.id.resultMessageTextView)

        // Set up button click listener
        addUserButton.setOnClickListener {
            addUserToDatabase()
        }
    }

    // Function to add a user to Firestore
    private fun addUserToDatabase() {
        val username = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()

        if (username.isNotEmpty() && email.isNotEmpty()) {
            val user = hashMapOf(
                "username" to username,
                "email" to email
            )

            // Add user to Firestore
            usersCollection.add(user)
                .addOnSuccessListener {
                    // On success, update the result message on the screen
                    resultMessageTextView.text = "User added successfully!"
                }
                .addOnFailureListener {
                    // On failure, show error message
                    resultMessageTextView.text = "Error adding user. Please try again."
                }
        } else {
            resultMessageTextView.text = "Please enter both username and email."
        }
    }
}
