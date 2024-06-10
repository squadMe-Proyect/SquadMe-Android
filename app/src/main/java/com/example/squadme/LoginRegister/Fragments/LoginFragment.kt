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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint

/*
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private lateinit var firebaseAuth:FirebaseAuth
    private val db =FirestoreSingleton.getInstance()
    private val sharedPreferences by lazy { requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.loginButton.setOnClickListener {
            val email:String = binding.emailInput.text.toString()
            val password:String = binding.passwordInput.text.toString()
            login(email, password)
        }

        binding.toRegisterFragment.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            view.findNavController().navigate(action)
        }

        binding.passwordReset.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment()
            findNavController().navigate(action)
        }
    }


    private fun login(email:String, password:String){
        if (email.isNotEmpty() && password.isNotEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                if (it.isSuccessful){
                    //savePasswordToPreferences(password)
                    saveUserDataToPreferences(email, password)
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    showAlert()
                }
            }.addOnFailureListener { exception ->
                // Maneja la excepción aquí
                Log.d("LoginFragment","Error: ${exception.message}")
            }
        } else {
            showToast("Error")
        }
    }

    private fun saveUserDataToPreferences(email: String, password: String) {
        // Fetch user data from Firestore and save to SharedPreferences
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            db.collection("coaches").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        val surname = document.getString("surname")
                        val nationality = document.getString("nationality")
                        val role = document.getString("role")
                        val team = document.getString("team")

                        with(sharedPreferences.edit()) {
                            putString("userName", name)
                            putString("userSurname", surname)
                            putString("userEmail", email)
                            putString("userNationality", nationality)
                            putString("userRole", role)
                            putString("userTeam", team)
                            putString("coachPassword", password)
                            apply()
                        }
                    } else {
                        // Handle case where the document does not exist
                        showToast("No se encontraron datos del usuario")
                    }
                }
                .addOnFailureListener {
                    // Handle any errors that occur
                    Log.d("LoginFragment","Error al obtener los datos del usuario: ${it.message}")
                }
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

}
 */


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirestoreSingleton.getInstance()
    private val sharedPreferences by lazy { requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

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

    private fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    saveUserRoleToPreferences()
                } else {
                    showAlert()
                }
            }.addOnFailureListener { exception ->
                Log.d("LoginFragment", "Error: ${exception.message}")
            }
        } else {
            showToast("Error")
        }
    }

    private fun saveUserRoleToPreferences() {
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
                            putString("userRole", "admin")
                        } else if (playerDocument.exists()) {
                            putString("userRole", "player")
                            val coachId = playerDocument.getString("coachId")
                            putString("coachId", coachId)
                        } else {
                            showToast("No se encontraron datos del usuario")
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

    private fun showAlert() {
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
}
