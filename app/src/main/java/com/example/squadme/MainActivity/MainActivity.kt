package com.example.squadme.MainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.squadme.R
import com.example.squadme.databinding.ActivityMainBinding
import com.example.squadme.utils.FirestoreSingleton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView :BottomNavigationView = binding.navView
        val navHostController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostController.navController


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.matchListFragment,
                R.id.playerListFragment,
                R.id.user_profile,
                R.id.squadListFragment,
                R.id.trainingListFragment
            )
        )
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener {
                _, destination,_ ->
            when(destination.id){
                R.id.cameraPreviewFragment, R.id.playerCreationFragment, R.id.playerUpdateFragment-> binding.navView.visibility = View.GONE
                else ->  binding.navView.visibility = View.VISIBLE
            }
        }
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore = FirestoreSingleton.getInstance()
        firestore.firestoreSettings = firestoreSettings
    }
}