package com.example.squadme.MainActivity.UserProfile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.squadme.LoginRegister.LoginActivity
import com.example.squadme.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.squadme.databinding.FragmentUserProfileBinding
import com.example.squadme.utils.FirestoreSingleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirestoreSingleton.getInstance()

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

        if (currentUser != null) {
            val uid = currentUser.uid
            Log.d("UserProfileFragment", "Current UID: $uid")

            // Primero intenta obtener los datos del caché
            obtenerDatosDesdeCache(uid)
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

    private fun obtenerDatosDesdeCache(uid: String) {
        db.collection("coaches").document(uid)
            .get(Source.CACHE)
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    mostrarDatosUsuario(document)
                } else {
                    // Si no es entrenador, verifica si es jugador
                    obtenerDatosJugadorDesdeCache(uid)
                }
            }
            .addOnFailureListener {
                // Si falla el caché, intenta obtener los datos del servidor
                obtenerDatosDesdeServidor(uid)
            }
    }

    private fun obtenerDatosJugadorDesdeCache(uid: String) {
        db.collection("players").document(uid)
            .get(Source.CACHE)
            .addOnSuccessListener { playerDocument ->
                if (playerDocument.exists()) {
                    mostrarDatosUsuario(playerDocument)
                } else {
                    // Si no existen datos en el caché, intenta obtener del servidor
                    obtenerDatosDesdeServidor(uid)
                }
            }
            .addOnFailureListener {
                // Si falla el caché, intenta obtener los datos del servidor
                obtenerDatosDesdeServidor(uid)
            }
    }

    private fun obtenerDatosDesdeServidor(uid: String) {
        db.collection("coaches").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    mostrarDatosUsuario(document)
                } else {
                    // Si no es entrenador, verifica si es jugador
                    db.collection("players").document(uid).get()
                        .addOnSuccessListener { playerDocument ->
                            if (playerDocument.exists()) {
                                mostrarDatosUsuario(playerDocument)
                            } else {
                                val message = getString(R.string.toast_doc_notExist)
                                showToast(message)
                            }
                        }
                        .addOnFailureListener { exception ->
                            showToast(getString(R.string.toast_error_user))
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("UserProfileFragment", "Error al obtener los datos del usuario: ${exception.message}")
                val message = getString(R.string.toast_error_user)
                showToast(message)
            }
    }

    private fun mostrarDatosUsuario(document: DocumentSnapshot) {
        val name = document.getString("name")
        val surname = document.getString("surname")
        val email = document.getString("email")
        val nationality = document.getString("nationality") ?: document.getString("nation")
        val role = document.getString("role")
        Log.d("UserProfileFragment", "Datos del usuario: Nombre: $name, Apellido: $surname, Email: $email, Nacionalidad: $nationality, Rol: $role")

        binding.userName.text = getString(R.string.profile_name) + " $name"
        binding.userSurname.text = getString(R.string.profile_surname) + " $surname"
        binding.userEmail.text = getString(R.string.profile_email) + " $email"
        binding.userNacionality.text = getString(R.string.profile_nacionality) + " $nationality"
        binding.userRole.text = getString(R.string.profile_rol) + " $role"
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}




