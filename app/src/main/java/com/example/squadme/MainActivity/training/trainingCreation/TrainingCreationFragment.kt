package com.example.squadme.MainActivity.training.trainingCreation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.squadme.R
import com.example.squadme.data.Models.Training
import com.example.squadme.data.repository.TrainingRepository
import com.example.squadme.databinding.FragmentTrainingCreationBinding
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TrainingCreationFragment : Fragment() {
    private lateinit var binding: FragmentTrainingCreationBinding
    private val calendar: Calendar = Calendar.getInstance()
    private val ejercicios = mutableListOf<String>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var repository: TrainingRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAgregarEjercicio.setOnClickListener {
            showAddExerciseDialog()
        }

        binding.btnFecha.setOnClickListener {
            showDatePicker()
        }

        binding.btnHora.setOnClickListener {
            showTimePicker()
        }

        binding.btnCrear.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())){
                crearTraining()
            }else{
                Toast.makeText(context, getString(R.string.toast_error_no_connection_createTraining), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
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
        val formattedTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        binding.editTextHora.setText(formattedTime)
    }

    private fun showAddExerciseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_exercise, null)
        val editTextExerciseName = dialogView.findViewById<EditText>(R.id.editTextExerciseName)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title_create_training))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_training_positive_btn)) { _, _ ->
                val exerciseName = editTextExerciseName.text.toString()
                if (exerciseName.isNotEmpty()) {
                    ejercicios.add(exerciseName)
                    updateEjerciciosTextView()
                } else {
                    Toast.makeText(context, getString(R.string.toast_error_non_exercise_added), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.dialog_training_negative_btn), null)
            .create()

        dialog.show()
    }

    private fun updateEjerciciosTextView() {
        val ejerciciosTexto = ejercicios.joinToString("\n")
        binding.textViewEjercicios.text = getString(R.string.text_exercises_added) + "\n$ejerciciosTexto"
    }


    private fun crearTraining() {
        val fecha = binding.editTextFecha.text.toString()
        val hora = binding.editTextHora.text.toString()

        if (fecha.isEmpty() || hora.isEmpty() || ejercicios.isEmpty()) {
            Toast.makeText(context, getString(R.string.toast_error_empty_values_squad), Toast.LENGTH_SHORT).show()
            return
        }

        // Convert date and time strings to a Date object
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateString = "$fecha $hora"
        val date: Date
        try {
            date = dateFormat.parse(dateString) ?: throw IllegalArgumentException("Fecha inválida")
        } catch (e: ParseException) {
            Toast.makeText(context, getString(R.string.toast_error_date_format_invalid), Toast.LENGTH_SHORT).show()
            return
        }

        // Convert the Date object to a Timestamp
        val timestamp = Timestamp(date)

        // Get the current user ID
        val currentUser = auth.currentUser
        val coachId = currentUser?.uid

        if (coachId == null) {
            Toast.makeText(context, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a Training object
        var training = Training(
            coachId = coachId,
            date = timestamp,
            exercises = ejercicios,
            completed = false // Set completed to false
        )

        db.collection("trainings")
            .add(training)
            .addOnSuccessListener { documentReference ->
                // Guardar el ID del documento
                training.id = documentReference.id
                db.collection("trainings").document(training.id!!).set(training)
                    .addOnSuccessListener {
                        Toast.makeText(context, getString(R.string.toast_training_create), Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener { e ->
                        //Toast.makeText(context, "Error al crear el entrenamiento: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.d("TrainingCreationFragment", "Error al crear el entrenamiento: ${e.message}")
                        Toast.makeText(context, getString(R.string.toast_training_create_error), Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, getString(R.string.toast_training_create_error), Toast.LENGTH_SHORT).show()
            }
    }
}



