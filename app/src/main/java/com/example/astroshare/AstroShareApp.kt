package com.example.astroshare

import android.app.Application
import androidx.room.Room
import com.example.astroshare.data.local.UserLocalDataSource
import com.example.astroshare.data.local.TripLocalDataSource
import com.example.astroshare.data.local.db.AppDatabase
import com.example.astroshare.data.remote.firebase.FirebaseAuthService
import com.example.astroshare.data.remote.firebase.FirebaseFirestoreService
import com.example.astroshare.data.remote.firebase.CloudinaryStorageService
import com.example.astroshare.data.repository.TripRepository
import com.example.astroshare.data.repository.TripRepositoryImpl
import com.example.astroshare.data.repository.UserRepository
import com.example.astroshare.data.repository.UserRepositoryImpl
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AstroShareApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var userRepository: UserRepository
        private set

    lateinit var tripRepository: TripRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize the Room database (using destructive migration for development)
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "astroshare_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // Initialize local data sources
        val userLocalDataSource = UserLocalDataSource(database.userDao())
        val tripLocalDataSource = TripLocalDataSource(database.tripDao())

        // Initialize Firebase services
        val firebaseAuthService = FirebaseAuthService(FirebaseAuth.getInstance())
        val firebaseFirestoreService = FirebaseFirestoreService(FirebaseFirestore.getInstance())

        // Initialize Cloudinary Storage Service (used for both user and trip images)
        val cloudinaryStorageService = CloudinaryStorageService()

        // Initialize repositories
        userRepository = UserRepositoryImpl(
            localDataSource = userLocalDataSource,
            authService = firebaseAuthService,
            firestoreService = firebaseFirestoreService,
            cloudinaryService = cloudinaryStorageService,
            appContext = applicationContext
        )

        tripRepository = TripRepositoryImpl(
            localDataSource = tripLocalDataSource,
            firestore = FirebaseFirestore.getInstance(),
            cloudinaryService = cloudinaryStorageService,
            appContext = applicationContext
        )
    }
}