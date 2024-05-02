package com.example.squadme.MainActivity.matches.matchesCreation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.data.Models.Match
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentMatchCreationBinding
import com.example.squadme.databinding.FragmentPlayerCreationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MatchCreationFragment : Fragment() {
    private lateinit var binding: FragmentMatchCreationBinding
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchCreationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth


        binding.Cancelar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.Crear.setOnClickListener {
            val coachId = firebaseAuth.currentUser?.uid
            val date = "${binding.editTextFecha.text} - ${binding.editTextTHora.text}"
            val opponent = binding.opponentInput.text.toString()

            // Verificamos que todos los campos obligatorios estén llenos
            if (coachId != null && date.isNotEmpty() && opponent.isNotEmpty()) {
                // Creamos el objeto Player con los datos recolectados
                val match = Match(
                    coachId = coachId,
                    date = date,
                    opponent = opponent,
                    result = "0-0"
                )

                // Llamamos a la función para crear el jugador en Firestore
                createMatch(match)
            }
        }

        binding.btnFecha.setOnClickListener {
            showDatePicker()
        }

        binding.btnHora.setOnClickListener {
            showTimePicker()
        }
    }


    private fun showDatePicker() {
        val dateListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateEditText()
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timeListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateTimeEditText()
        }

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            timeListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDateEditText() {
        val formattedDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        binding.editTextFecha.setText(formattedDate)
    }

    private fun updateTimeEditText() {
        val formattedTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        binding.editTextTHora.setText(formattedTime)
    }


    private fun createMatch(match: Match) {
        // Agregamos el jugador a la colección "players" en Firestore
        db.collection("matches")
            .add(match)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "Partido agregado con ID: ${documentReference.id}")
                Toast.makeText(context, "Partido creado exitosamente", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_matchCreationFragment_to_matchListFragment)

            }
            .addOnFailureListener { e ->
                Log.d(ContentValues.TAG, "Error al agregar el partido", e)
                Toast.makeText(context, "Error al crear el partido", Toast.LENGTH_SHORT).show()
            }
    }

}