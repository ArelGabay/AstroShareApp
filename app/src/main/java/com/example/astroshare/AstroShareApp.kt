package com.example.astroshare

import android.app.Application
import androidx.room.Room
import com.example.astroshare.data.local.UserLocalDataSource
import com.example.astroshare.data.local.db.AppDatabase
import com.example.astroshare.data.remote.firebase.FirebaseAuthService
import com.example.astroshare.data.remote.firebase.FirebaseFirestoreService
import com.example.astroshare.data.remote.firebase.FirebaseStorageService
import com.example.astroshare.data.repository.UserRepository
import com.example.astroshare.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AstroShareApp : Application() {

    // Expose the Room database publicly.
    lateinit var database: AppDatabase
        private set

    lateinit var userRepository: UserRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize Room database and assign to our public property.
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "astroshare_db"
        ).build()

        // Initialize local data sources
        val userLocalDataSource = UserLocalDataSource(database.userDao())

        // Initialize Firebase services
        val firebaseAuthService = FirebaseAuthService(FirebaseAuth.getInstance())
        val firebaseFirestoreService = FirebaseFirestoreService(FirebaseFirestore.getInstance())
        val firebaseStorageService = FirebaseStorageService(FirebaseStorage.getInstance())

        // Initialize repositories
        userRepository = UserRepositoryImpl(
            localDataSource = userLocalDataSource,
            authService = firebaseAuthService,
            firestoreService = firebaseFirestoreService,
            storageService = firebaseStorageService
        )
    }
}