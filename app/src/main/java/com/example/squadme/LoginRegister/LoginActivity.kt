package com.example.squadme.LoginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.squadme.LoginRegister.Fragments.ResetPasswordFragment
import com.example.squadme.R
import com.example.squadme.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var navController: NavController

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getStringExtra("fragment_to_open") == "reset_password_fragment") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.resetPasswordFragment, ResetPasswordFragment() )
                .commit()
        }

        val navHostController = supportFragmentManager.findFragmentById(R.id.LoginActivity) as NavHostFragment
        navController = navHostController.navController
        navController.navigate(R.id.login_register)
    }

     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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