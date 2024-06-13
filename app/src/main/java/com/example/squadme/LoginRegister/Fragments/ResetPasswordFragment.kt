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
        binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
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
        firebaseAuth = Firebase.auth
        val email = binding.editTextTextEmailAddress

        binding.button.setOnClickListener {
            sendPasswordReset(email.text.toString())
        }
    }

    /**
     * Password reset logic
     *
     * @param email The email address entered by the user
     */
    private fun sendPasswordReset(email: String){
        if(email.isNullOrEmpty()){
            Toast.makeText(this.requireContext(),getString(R.string.toast_error_email_empty), Toast.LENGTH_SHORT).show()
        }else{
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this.requireContext(),getString(R.string.toast_send_email), Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this.requireContext(),getString(R.string.toast_error_rese_password_process_ended), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}