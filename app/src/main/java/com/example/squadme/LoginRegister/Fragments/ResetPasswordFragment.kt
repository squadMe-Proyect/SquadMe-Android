package com.example.squadme.LoginRegister.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.squadme.R
import com.example.squadme.databinding.FragmentResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = Firebase.auth
        val email = binding.editTextTextEmailAddress

        binding.button.setOnClickListener {
            sendPasswordReset(email.text.toString())
        }
    }

    private fun sendPasswordReset(email: String){
        if(email.isNullOrEmpty()){
            Toast.makeText(this.requireContext(),"Error, email sin rellenar", Toast.LENGTH_SHORT).show()
        }else{
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this.requireContext(),"Correo de cambio de contraseña enviado", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this.requireContext(),"Error, no se pudo completar el proceso, introduzca bien el email", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}