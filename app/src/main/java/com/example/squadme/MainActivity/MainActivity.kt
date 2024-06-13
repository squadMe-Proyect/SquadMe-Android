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
import com.example.squadme.utils.UserManager
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

    /**
     * Set up the activity, configure the app navigation
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed from a previous saved state as given here
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostController.navController

        val fragments = if (UserManager.isAdmin) {
            setOf(
                R.id.match,
                R.id.player,
                R.id.profile,
                R.id.squad,
                R.id.train
            )
        } else {
            setOf(
                R.id.match,
                R.id.player,
                R.id.profile,
                R.id.train
            )
        }

        appBarConfiguration = AppBarConfiguration(fragments)

        navView.setupWithNavController(navController)

        navView.viewTreeObserver.addOnGlobalLayoutListener {
            val menu = navView.menu
            menu.findItem(R.id.squad)?.isVisible = UserManager.isAdmin
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerListFragment,
                R.id.matchListFragment,
                R.id.squadListFragment,
                R.id.trainingListFragment,
                R.id.user_profile-> binding.navView.visibility = View.VISIBLE
                else -> binding.navView.visibility = View.GONE
            }
        }

        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore = FirestoreSingleton.getInstance()
        firestore.firestoreSettings = firestoreSettings
    }
}

