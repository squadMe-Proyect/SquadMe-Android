package com.example.squadme.MainActivity.matches.matchesCreation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.squadme.R
import com.example.squadme.data.Models.LineUp
import com.example.squadme.data.Models.Match
import com.example.squadme.databinding.FragmentMatchCreationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

/*
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
                    result = "0-0",
                    lineUp = null,
                    finished = false
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
                match.id = documentReference.id
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

 */
/*

@AndroidEntryPoint
class MatchCreationFragment : Fragment() {
    private lateinit var binding: FragmentMatchCreationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var lineUpList: MutableList<LineUp>
    private var selectedLineUp: LineUp? = null
    private lateinit var lineUpAdapter: LineUpAdapterDropdown
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        lineUpList = mutableListOf()
        selectedLineUp = null

        val lineUpRef = firestore.collection("squads")

        lineUpRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(TAG, "Error fetching lineUps: $exception")
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


        binding.Crear.setOnClickListener {
            createMatch()
        }

        binding.Cancelar.setOnClickListener {
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
        val formattedTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        binding.editTextTHora.setText(formattedTime)
    }

    private fun setupLineUpAdapter() {
        lineUpAdapter = LineUpAdapterDropdown(lineUpList) { lineUp ->
            selectedLineUp = lineUp
            updateLineUpView()
        }
    }

    private fun updateLineUpView() {
        binding.spinnerTextView.text = selectedLineUp?.name ?: "Selecciona una alineación"
    }

    private fun showLineUpSelectionDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_lineup_selection, null)
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

    private fun createMatch() {
        val matchName = binding.opponentInput.text.toString()
        val selectedLineUp = this.selectedLineUp

        if (matchName.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Por favor, ingrese un nombre para el partido.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (selectedLineUp == null) {
            Toast.makeText(
                requireContext(),
                "Por favor, seleccione una alineación.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val match = hashMapOf(
            "opponent" to matchName,
            "lineUp" to selectedLineUp,
            "coachId" to currentUserId
        )

        firestore.collection("matches")
            .add(match)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Partido creado exitosamente.",
                    Toast.LENGTH_SHORT
                ).show()
                val action =
                    MatchCreationFragmentDirections.actionMatchCreationFragmentToMatchListFragment()
                findNavController().navigate(action)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error al crear el partido: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

 */

@AndroidEntryPoint
class MatchCreationFragment : Fragment() {
    private lateinit var binding: FragmentMatchCreationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var lineUpList: MutableList<LineUp>
    private var selectedLineUp: LineUp? = null
    private lateinit var lineUpAdapter: LineUpAdapterDropdown
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        lineUpList = mutableListOf()
        selectedLineUp = null

        val lineUpRef = firestore.collection("squads")

        lineUpRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(TAG, "Error fetching lineUps: $exception")
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

        binding.Crear.setOnClickListener {
            createMatch()
        }

        binding.Cancelar.setOnClickListener {
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
        val formattedTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        binding.editTextTHora.setText(formattedTime)
    }

    private fun setupLineUpAdapter() {
        lineUpAdapter = LineUpAdapterDropdown(lineUpList) { lineUp ->
            selectedLineUp = lineUp
            updateLineUpView()
        }
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

    private fun createMatch() {
        val matchName = binding.opponentInput.text.toString()
        val selectedLineUp = this.selectedLineUp

        if (matchName.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre para el partido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedLineUp == null) {
            Toast.makeText(requireContext(), "Por favor, seleccione una alineación.", Toast.LENGTH_SHORT).show()
            return
        }

        val formattedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        val formattedTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        val dateTime = "$formattedDate - $formattedTime"

        val match = hashMapOf(
            "opponent" to matchName,
            "date" to dateTime,
            "finished" to false,
            "result" to "0-0",
            "coachId" to currentUserId,
            "squad" to mapOf(
                "id" to selectedLineUp.id,
                "name" to selectedLineUp.name,
                "lineUp" to selectedLineUp.lineUp,
                "players" to selectedLineUp.players.map { player ->
                    mapOf(
                        "name" to player.name,
                        "surname" to player.surname,
                        "position" to player.position
                    )
                }
            )
        )


        firestore.collection("matches")
            .add(match)
            .addOnSuccessListener {
                val matchId = it.id
                firestore.collection("matches").document(matchId)
                    .update("id", matchId)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Partido creado exitosamente.", Toast.LENGTH_SHORT).show()
                        val action = MatchCreationFragmentDirections.actionMatchCreationFragmentToMatchListFragment()
                        findNavController().navigate(action)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al crear el partido: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

