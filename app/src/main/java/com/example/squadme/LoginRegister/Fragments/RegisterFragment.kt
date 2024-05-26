package com.example.squadme.LoginRegister.Fragments


import android.app.AlertDialog
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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore
    private val sharedPreferences by lazy { requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.registerButton.setOnClickListener {
            val email:String = binding.emailInput.text.toString()
            val password:String = binding.passwordInput.text.toString()
            val name: String = binding.nameInput.text.toString()
            val nationality:String = binding.nacionalityInput.text.toString()
            val surname = binding.surnameInput.text.toString()
            val role = "Admin"
            val equipo = binding.teamInput.text.toString()
            val coach = Coach(name, email, nationality, equipo, role, surname)
            register(coach, password)
        }
    }

    private fun register(coach:Coach, password:String){
        if (coach.email.isEmpty() || password.isEmpty()){
            showAlert("Rellene bien los cambios")
            return
        }else{
            firebaseAuth.createUserWithEmailAndPassword(coach.email, password).addOnCompleteListener{
                if (it.isSuccessful){
                    savePasswordToPreferences(password)
                    postRegister(coach)
                }else{
                    showAlert("Error al registrar un usuario ")
                }
            }
        }
    }


    private fun postRegister(coach: Coach) {
        if (coach.name.isEmpty() || coach.email.isEmpty() || coach.nationality.isEmpty() || coach.surname.isEmpty() || coach.team.isEmpty()) {
            showToast("Por favor, complete todos los campos")
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
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .addOnFailureListener {
                    showToast("Error al añadir un usuario a la colección")
                }
        } else {
            showToast("Error: Usuario actual nulo")
        }

    }

    private fun savePasswordToPreferences(password: String) {
        with(sharedPreferences.edit()) {
            putString("coachPassword", password)
            apply()
        }
    }



    private fun showAlert(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}