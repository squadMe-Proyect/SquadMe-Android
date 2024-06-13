package com.example.squadme.LoginRegister.Fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.squadme.MainActivity.MainActivity
import com.example.squadme.data.Models.Coach
import com.example.squadme.databinding.FragmentRegisterBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.R
import com.example.squadme.utils.UserManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
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


        binding.registerButton.setOnClickListener {
            val email:String = binding.emailInput.text.toString()
            val password:String = binding.passwordInput.text.toString()
            val confirmPassword: String = binding.confirmPasswordInput.text.toString()
            val name: String = binding.nameInput.text.toString()
            val nationality:String = binding.nacionalityInput.text.toString()
            val surname = binding.surnameInput.text.toString()
            val role = "Admin"
            val equipo = binding.teamInput.text.toString()

            if (password != confirmPassword) {
                showToast(getString(R.string.toast_error_password_mismatch))
                return@setOnClickListener
            }
            val coach = Coach(name, email, nationality, equipo, role, surname)
            register(coach, password)
        }
    }

    /**
     * Handle register logic
     *
     * @param coach The Coach object containing the user's details
     * @param password The password entered by the user
     */

    private fun register(coach:Coach, password:String){
        if (coach.email.isEmpty() || password.isEmpty()){
            showToast(getString(R.string.toast_error_user_register))
            return
        }else{
            firebaseAuth.createUserWithEmailAndPassword(coach.email, password).addOnCompleteListener{
                if (it.isSuccessful){
                    saveUserDataToPreferences(coach, password)
                    postRegister(coach)

                }else{
                    showToast(getString(R.string.toast_error_user_register2))
                }
            }
        }
    }


    /**
     * Save user data to Firestore and navigate to the main activity
     *
     * @param coach The Coach object containing the user's details
     */
    private fun postRegister(coach: Coach) {
        if (coach.name.isEmpty() || coach.email.isEmpty() || coach.nationality.isEmpty() || coach.surname.isEmpty() || coach.team.isEmpty()) {
            showToast(getString(R.string.toast_error_user_register_empty_values))
            return
        }

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val coachData = hashMapOf(
                "name" to coach.name,
                "email" to coach.email,
                "nationality" to coach.nationality,
                "surname" to coach.surname,
                "team" to coach.team,
                "role" to "Admin"
            )

            db.collection("coaches").document(userId).set(coachData)
                .addOnSuccessListener {
                    UserManager.isAdmin = true
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .addOnFailureListener {
                    Log.d("LoginFragment","Error al añadir un usuario a la colección" )
                }
        } else {
            Log.d("LoginFragment","Error: Usuario actual nulo" )
        }

    }


    /**
     * Save user data to shared preferences
     *
     * @param coach The Coach object containing the user's details
     * @param password The password entered by the user
     */
    private fun saveUserDataToPreferences(coach: Coach, password: String) {
        with(sharedPreferences.edit()) {
            putString("userName", coach.name)
            putString("userSurname", coach.surname)
            putString("userEmail", coach.email)
            putString("userNationality", coach.nationality)
            putString("userRole", coach.rol)
            putString("userTeam", coach.team)
            putString("coachPassword", password)
            apply()
        }
    }



    /**
     * Show a toast message
     *
     * @param message The message to be shown in the toast
     */
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}