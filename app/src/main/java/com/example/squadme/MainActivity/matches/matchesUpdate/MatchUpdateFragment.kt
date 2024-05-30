package com.example.squadme.MainActivity.matches.matchesUpdate

import android.app.AlertDialog
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.MainActivity.matches.matchesCreation.LineUpAdapterDropdown
import com.example.squadme.MainActivity.matches.matchesDetail.MatchDetailFragmentArgs
import com.example.squadme.MainActivity.matches.matchesDetail.MatchDetailFragmentDirections
import com.example.squadme.R
import com.example.squadme.data.Models.LineUp
import com.example.squadme.data.Models.Match
import com.example.squadme.databinding.FragmentMatchListBinding
import com.example.squadme.databinding.FragmentMatchUpdateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MatchUpdateFragment : Fragment() {
    private lateinit var binding: FragmentMatchUpdateBinding
    private val calendar: Calendar = Calendar.getInstance()
    private val db = Firebase.firestore
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var lineUpAdapter: LineUpUpdateAdapterDropdown
    private lateinit var lineUpList: MutableList<LineUp>
    private var selectedLineUp: LineUp? = null

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
        binding.opponentInput.setText(match.opponent ?: "")
        binding.editTextFecha.setText(fecha)
        binding.editTextTHora.setText(hora)
        binding.resultInput.setText(match.result ?: "")
        binding.switchCompleted.isChecked = match.finished

        loadLineUps(match.squad?.id)

        binding.Cancelar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnFecha.setOnClickListener {
            showDatePicker()
        }

        binding.btnHora.setOnClickListener {
            showTimePicker()
        }

        binding.spinnerTextView.setOnClickListener {
            showLineUpSelectionDialog()
        }

        binding.Actualizar.setOnClickListener {
            val date = "${binding.editTextFecha.text} - ${binding.editTextTHora.text}"
            val opponent = binding.opponentInput.text.toString()
            val result = binding.resultInput.text.toString()
            val finished = binding.switchCompleted.isChecked

            val updatedMatch = Match(
                id = match.id,
                date = date,
                opponent = opponent,
                result = result,
                finished = finished,
                squad = selectedLineUp
            )
            updateMatch(updatedMatch)
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

    private fun loadLineUps(selectedLineUpId: String?) {
        lineUpList = mutableListOf()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""
        val lineUpRef = firestore.collection("squads")

        lineUpRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(ContentValues.TAG, "Error fetching lineUps: $exception")
                return@addSnapshotListener
            }

            lineUpList.clear()
            snapshot?.documents?.forEach { document ->
                val lineUp = document.toObject(LineUp::class.java)
                lineUp?.let {
                    it.id = document.id
                    if (it.coachId == currentUserId) {
                        lineUpList.add(it)
                    }
                }
            }

            setupLineUpAdapter()
            updateLineUpView()

        }
    }

    private fun setupLineUpAdapter() {
        lineUpAdapter = LineUpUpdateAdapterDropdown(lineUpList, selectedLineUp) { lineUp ->
            selectedLineUp = lineUp
            updateLineUpView()
        }
        lineUpAdapter.notifyDataSetChanged()
    }

    private fun updateLineUpView() {
        binding.spinnerTextView.text = selectedLineUp?.name ?: "Selecciona una alineación"
    }

    private fun showLineUpSelectionDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_lineup_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.dialogLineUpRecyclerView)

        recyclerView.adapter = lineUpAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar Alineación")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                updateLineUpView()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun updateMatch(match: Match) {
        db.collection("matches")
            .document(match.id!!)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val docId = document.id

                    val updatedValues = hashMapOf(
                        "opponent" to match.opponent,
                        "date" to match.date,
                        "result" to match.result,
                        "finished" to match.finished,
                        "squad" to mapOf(
                            "id" to selectedLineUp?.id,
                            "name" to selectedLineUp?.name,
                            "lineUp" to selectedLineUp?.lineUp,
                            "players" to selectedLineUp?.players?.map { player ->
                                mapOf(
                                    "name" to player.name,
                                    "surname" to player.surname,
                                    "position" to player.position
                                )
                            }
                        )
                    )

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
