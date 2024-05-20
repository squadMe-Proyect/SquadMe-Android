package com.example.squadme.MainActivity.UserProfile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.squadme.LoginRegister.Fragments.ResetPasswordFragment
import com.example.squadme.LoginRegister.LoginActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.squadme.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class user_profileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth
        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        // Verifica si el usuario está autenticado
        if (currentUser != null) {
            val uid = currentUser.uid

            db.collection("coaches").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("UserProfileFragment", "Documento recuperado correctamente: $document")
                        val name = document.getString("name")
                        val surname = document.getString("surname")
                        val email = document.getString("email")
                        val nationality = document.getString("nationality")
                        val role = document.getString("role")
                        Log.d("UserProfileFragment", "Datos del usuario: Nombre: $name, Apellido: $surname, Email: $email, Nacionalidad: $nationality, Rol: $role")

                        // Actualiza las vistas con los datos del usuario
                        binding.userName.text = "Nombre: $name"
                        binding.userSurname.text = "Apellido: $surname"
                        binding.userEmail.text = "Email: $email"
                        binding.userNacionality.text = "Nacionalidad: $nationality"
                        binding.userRole.text = "Rol: $role"
                    } else {
                        // Si el documento no existe
                        showToast("El documento no existe")
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Error al obtener los datos del usuario: ${exception.message}")
                }
        }


            binding.logoutBtn.setOnClickListener {
                firebaseAuth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }

            binding.cambioPasswordBtn.setOnClickListener {
                val intent = Intent(this.requireContext(), LoginActivity::class.java)
                intent.putExtra("fragment_to_open", "reset_password_fragment")
                startActivity(intent)
                activity?.finish()
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}