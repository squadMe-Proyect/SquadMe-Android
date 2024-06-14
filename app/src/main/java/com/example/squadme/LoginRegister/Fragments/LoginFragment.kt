package com.example.squadme.LoginRegister.Fragments


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.squadme.MainActivity.MainActivity
import com.example.squadme.databinding.FragmentLoginBinding
import com.example.squadme.utils.FirestoreSingleton
import android.util.Log
import com.example.squadme.R
import com.example.squadme.utils.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.Tasks
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {
    /*
     * Declare variables for view binding, Firebase authentication, Firestore instance, and shared preferences
     */
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirestoreSingleton.getInstance()
    private val sharedPreferences by lazy { requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }


    /**
     * Inflate the layout for this fragment and initialize FirebaseAuth
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    /**
     * Set up the view once it has been created
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            login(email, password)
        }

        binding.toRegisterFragment.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            view.findNavController().navigate(action)
        }

        binding.passwordReset.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment()
            findNavController().navigate(action)
        }
    }


    /**
     * Login logic
     *
     * @param email The email entered by the user
     * @param password The password entered by the user
     */
    private fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    saveUserRoleToPreferences(email, password)
                } else {
                    Toast.makeText(context, getString(R.string.error_login), Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Log.d("LoginFragment", "Error: ${exception.message}")
            }
        } else {
            Toast.makeText(context, getString(R.string.error_login), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Save the user's role to shared preferences
     */
    private fun saveUserRoleToPreferences(email: String, password: String) {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val coachesRef = db.collection("coaches").document(userId)
            val playersRef = db.collection("players").document(userId)

            Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
                .addOnSuccessListener { results ->
                    val coachDocument = results[0]
                    val playerDocument = results[1]

                    with(sharedPreferences.edit()) {
                        if (coachDocument.exists()) {
                            putString("coachEmail", email)
                            putString("coachPassword", password)
                            putString("userRole", "admin")
                            UserManager.isAdmin = true
                        } else if (playerDocument.exists()) {
                            putString("userRole", "player")
                            UserManager.isAdmin = false
                            val coachId = playerDocument.getString("coachId")
                            putString("coachId", coachId)
                        } else {
                            Log.d("LoginFragment","No se encontraron datos del usuario")
                        }
                        apply()
                    }

                    // Navega a la siguiente pantalla o actividad
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .addOnFailureListener {
                    Log.d("LoginFragment", "Error al obtener los datos del usuario: ${it.message}")
                }
        }
    }
}
