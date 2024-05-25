package com.example.squadme.MainActivity.training.trainingUpdate

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.squadme.R
import com.example.squadme.data.Models.Training
import com.example.squadme.databinding.FragmentTrainingUpdateBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TrainingUpdateFragment : Fragment() {
    private lateinit var binding: FragmentTrainingUpdateBinding
    private val db = FirebaseFirestore.getInstance()
    private val calendar: Calendar = Calendar.getInstance()
    private val exercises = mutableListOf<String>()
    private lateinit var exerciseAdapter: TrainingExerciseUpdateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: TrainingUpdateFragmentArgs by navArgs()
        val training: Training = args.training

        // Populate existing data
        val timestamp = training.date
        if (timestamp != null) {
            val date = timestamp.toDate()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val formattedDate = dateFormat.format(date)
            val formattedTime = timeFormat.format(date)

            binding.editTextFecha.setText(formattedDate)
            binding.editTextHora.setText(formattedTime)
            exercises.addAll(training.exercises)
            binding.switchCompleted.isChecked = training.completed
        }

        // Initialize RecyclerView
        exerciseAdapter = TrainingExerciseUpdateAdapter(exercises) { exercise ->
            exercises.remove(exercise)
            exerciseAdapter.notifyDataSetChanged()
        }
        binding.recyclerViewEjercicios.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseAdapter
        }

        binding.btnFecha.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.editTextFecha.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }

        binding.btnHora.setOnClickListener {
            val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.editTextHora.setText(timeFormat.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }

        binding.btnAgregarEjercicio.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Agregar Ejercicio")
            val input = EditText(requireContext())
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("Agregar") { _, _ ->
                val ejercicio = input.text.toString()
                if (ejercicio.isNotBlank()) {
                    exercises.add(ejercicio)
                    exerciseAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Ejercicio no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            builder.show()
        }

        binding.btnGuardar.setOnClickListener {
            guardarCambios(training.id)
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun guardarCambios(trainingId: String?) {
        val fecha = binding.editTextFecha.text.toString()
        val hora = binding.editTextHora.text.toString()
        val completado = binding.switchCompleted.isChecked

        if (fecha.isEmpty() || hora.isEmpty() || exercises.isEmpty()) {
            Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert date and time strings to a Timestamp object
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateString = "$fecha $hora"
        val date = dateFormat.parse(dateString)
        val timestamp = Timestamp(date)

        // Check if any changes were made
        val args: TrainingUpdateFragmentArgs by navArgs()
        val training: Training = args.training
        if (timestamp == training.date &&
            exercises == training.exercises &&
            completado == training.completed
        ) {
            Toast.makeText(context, "No se hicieron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // Update the Training object
        val trainingUpdate = mapOf(
            "date" to timestamp,
            "exercises" to exercises,
            "completed" to completado
        )

        if (trainingId != null) {
            db.collection("trainings").document(trainingId)
                .update(trainingUpdate)
                .addOnSuccessListener {
                    Toast.makeText(context, "Entrenamiento actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    val action = TrainingUpdateFragmentDirections.actionTrainingUpdateFragmentToTrainingListFragment()
                    findNavController().navigate(action)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al actualizar el entrenamiento: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "ID de entrenamiento no válido", Toast.LENGTH_SHORT).show()
        }
    }
}
