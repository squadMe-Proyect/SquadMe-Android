package com.example.squadme.LoginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.squadme.LoginRegister.Fragments.ResetPasswordFragment
import com.example.squadme.R
import com.example.squadme.databinding.ActivityLoginBinding
import com.example.squadme.utils.FirestoreSingleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var navController: NavController
    private lateinit var firestore: FirebaseFirestore


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

        // Obtén el ID del fragmento a abrir desde los extras del intent
        val fragmentToOpen = intent.getStringExtra("fragment_to_open")
        if (fragmentToOpen == "reset_password_fragment") {
            navController.navigate(R.id.resetPasswordFragment)
        } else {
            // Si no hay un fragmento específico para abrir, navega al fragmento predeterminado
            navController.navigate(R.id.login_register)
        }
    }
}