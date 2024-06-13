package com.example.squadme.LoginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.squadme.R
import com.example.squadme.databinding.ActivityLoginBinding
import com.example.squadme.utils.FirestoreSingleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var navController: NavController
    private lateinit var firestore: FirebaseFirestore


    /**
     * Set up the activity, configure Firestore settings, and navigate based on intent extras
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed from a previous saved state as given here
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore = FirestoreSingleton.getInstance()
        firestore.firestoreSettings = firestoreSettings
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.LoginActivity) as NavHostFragment
        navController = navHostFragment.navController

        val fragmentToOpen = intent.getStringExtra("fragment_to_open")
        if (fragmentToOpen == "reset_password_fragment") {
            navController.navigate(R.id.resetPasswordFragment)
        } else {
            navController.navigate(R.id.login_register)
        }
    }
}