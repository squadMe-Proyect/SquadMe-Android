package com.example.squadme.MainActivity.matches.matchesUpdate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.navigation.fragment.navArgs
import com.example.squadme.MainActivity.matches.matchesDetail.MatchDetailFragmentArgs
import com.example.squadme.MainActivity.matches.matchesDetail.MatchDetailFragmentDirections
import com.example.squadme.R
import com.example.squadme.data.Models.Match
import com.example.squadme.databinding.FragmentMatchListBinding
import com.example.squadme.databinding.FragmentMatchUpdateBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MatchUpdateFragment : Fragment() {
    private lateinit var binding: FragmentMatchUpdateBinding
    private val calendar: Calendar = Calendar.getInstance()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchUpdateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: MatchDetailFragmentArgs by navArgs()
        val match: Match = args.match

        val dateTimeString = match.date

        val partes = dateTimeString?.split(" - ")

        val fecha = partes?.get(0)
        val hora = partes?.get(1)

        // Mostrar los valores del objeto Match en los elementos correspondientes
        binding.opponentInput.setText(match?.opponent ?: "")
        binding.editTextFecha.setText(fecha)
        binding.editTextTHora.setText(hora)
        binding.resultInput.setText(match?.result ?:"")



        binding.Cancelar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnFecha.setOnClickListener {
            showDatePicker()
        }

        binding.btnHora.setOnClickListener {
            showTimePicker()
        }

        binding.Actualizar.setOnClickListener {
            val date = "${binding.editTextFecha.text} - ${binding.editTextTHora.text}"
            val opponent = binding.opponentInput.text.toString()
            val result = binding.resultInput.text.toString()

            // Verificamos que todos los campos obligatorios estén llenos
                // Creamos el objeto Player con los datos recolectados
                val match = Match(
                    date = date,
                    opponent = opponent,
                    result = result
                )
            updateMatch(match)
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
        val formattedDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(
            Calendar.YEAR)}"
        binding.editTextFecha.setText(formattedDate)
    }

    private fun updateTimeEditText() {
        val formattedTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        binding.editTextTHora.setText(formattedTime)
    }

    private fun updateMatch(match:Match){
        db.collection("matches")
            .whereEqualTo("date", match.date)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val matchDocument = documents.first()
                    val docId = matchDocument.id

                    // Obtener los nuevos valores del partido desde los elementos de la interfaz de usuario
                    val newOpponent = binding.opponentInput.text.toString()
                    val newDate = binding.editTextFecha.text.toString()
                    val newTime = binding.editTextTHora.text.toString()
                    val newResult = binding.resultInput.text.toString()

                    // Crear un objeto Map con los nuevos valores del partido
                    val updatedValues = hashMapOf(
                        "opponent" to newOpponent,
                        "date" to "$newDate - $newTime", // Combinar fecha y hora en un solo campo
                        "result" to newResult
                        // Agrega otros campos según sea necesario
                    )

                    // Actualizar el partido en Firestore
                    db.collection("matches").document(docId)
                        .update(updatedValues as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Partido actualizado", Toast.LENGTH_SHORT).show()
                            val action = MatchUpdateFragmentDirections.actionMatchUpdateFragmentToMatchListFragment()
                            findNavController().navigate(action)
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(context, "Error al actualizar el partido", Toast.LENGTH_SHORT).show()
                            Log.e("Firestore", "Error al actualizar los valores del partido: ${error.message}")
                        }
                } else {
                    Log.d("Firestore", "No se encontró el documento del partido con la fecha: ${match.date}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener el documento del partido", e)
                Toast.makeText(context, "Error al actualizar el partido", Toast.LENGTH_SHORT).show()
            }
    }
}