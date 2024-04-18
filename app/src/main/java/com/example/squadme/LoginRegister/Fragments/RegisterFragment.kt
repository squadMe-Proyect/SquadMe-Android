package com.example.squadme.LoginRegister.Fragments


import android.app.AlertDialog
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
                    postRegister(coach)
                }else{
                    showAlert("Error al registrar un usuario ")
                }
            }
        }
    }

    private fun postRegister(coach:Coach){
        if (coach.name.isEmpty() || coach.email.isEmpty() || coach.nationality.isEmpty() || coach.surname.isEmpty() || coach.team.isEmpty()) {
            showToast("Por favor, complete todos los campos")
            return
        }else{
            db.collection("coaches").add(coach).addOnCompleteListener{
                if (it.isSuccessful){
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }else{
                    showToast("Error al añadir un user a la coleccion")
                }
            }
        }

    }


    private fun showAlert(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /*
    private fun register(coach: Coach, password:String){
        if (coach.email.isNotEmpty() && password.isNotEmpty()){

            firebaseAuth.createUserWithEmailAndPassword(coach.email, password).addOnCompleteListener{
                if (task.isSuccessful){
                    postRegister(coach)
                }else{
                    showAlert()
                }
            }
        }else{
            showToast("Error")
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al loguear un usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private fun postRegister(coach: Coach) {
        if (coach.name.isEmpty() || coach.email.isEmpty() || coach.nationality.isEmpty() || coach.surname.isEmpty() || coach.team.isEmpty()) {
            showToast("Por favor, complete todos los campos")
            return
        }

        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            // Obtén una instancia de Firebase Firestore
            val db = Firebase.firestore

            // Crea un HashMap con los datos del coach
            val coachData = hashMapOf(
                "name" to coach.name,
                "email" to coach.email,
                "nationality" to coach.nationality,
                "surname" to coach.surname,
                "team" to coach.team,
                "role" to "Coach"
            )

            // Agrega los datos del coach a la colección "coaches" con el UID del usuario como identificador del documento
            db.collection("coaches")
                .document(userId)
                .set(coachData)
                .addOnSuccessListener {
                    showToast("Coach registrado correctamente")
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .addOnFailureListener { exception ->
                    showToast("Error al agregar coach a Firebase Firestore: ${exception.message}")
                }
        } else {
            showToast("Error: Usuario actual no encontrado")
        }
    }
    */


}