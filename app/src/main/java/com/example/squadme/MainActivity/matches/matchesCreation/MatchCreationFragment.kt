package com.example.squadme.MainActivity.matches.matchesCreation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.squadme.databinding.FragmentMatchCreationBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MatchCreationFragment : Fragment() {
    private lateinit var binding: FragmentMatchCreationBinding
    private lateinit var auth: FirebaseAuth
    private var firestore = FirestoreSingleton.getInstance()
    private lateinit var currentUserId: String
    private lateinit var lineUpList: MutableList<LineUp>
    private var selectedLineUp: LineUp? = null
    private lateinit var lineUpAdapter: LineUpAdapterDropdown
    private val calendar: Calendar = Calendar.getInstance()

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate
     *                  the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after `onCreateView` has returned, but before any saved state has been restored in the view.
     *
     * @param view The View returned by `onCreateView`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
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
            if (NetworkUtils.isNetworkAvailable(requireContext())){
                createMatch()
            }else{
                Toast.makeText(context, getString(R.string.toast_error_no_connection_createMatch), Toast.LENGTH_SHORT).show()
            }
        }

        binding.Cancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Displays a date picker dialog.
     */
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

    /**
     * Displays a time picker dialog.
     */
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

    /**
     * Updates the date EditText with the selected date.
     */
    private fun updateDateEditText() {
        val formattedDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        binding.editTextFecha.setText(formattedDate)
    }

    /**
     * Updates the time EditText with the selected time.
     */
    private fun updateTimeEditText() {
        val formattedTime = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        binding.editTextTHora.setText(formattedTime)
    }

    /**
     * Sets up the LineUp adapter for the dropdown menu.
     */
    private fun setupLineUpAdapter() {
        lineUpAdapter = LineUpAdapterDropdown(lineUpList) { lineUp ->
            selectedLineUp = lineUp
            updateLineUpView()
        }
    }

    /**
     * Updates the view to show the selected LineUp's name.
     */
    private fun updateLineUpView() {
        binding.spinnerTextView.text = selectedLineUp?.name ?: getString(R.string.select_lineUp_macth)
    }

    /**
     * Displays a dialog for selecting a LineUp from the list.
     */
    private fun showLineUpSelectionDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_lineup_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.dialogLineUpRecyclerView)

        recyclerView.adapter = lineUpAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_match_lineUp))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.dialog_match_lineUp_positive_btn)) { dialog, _ ->
                dialog.dismiss()
                updateLineUpView()
            }
            .setNegativeButton(getString(R.string.dialog_match_lineUp_negative_btn)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    /**
     * Creates a new match and saves it to Firestore.
     */
    private fun createMatch() {
        val matchName = binding.opponentInput.text.toString()
        val selectedLineUp = this.selectedLineUp

        if (matchName.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.toast_empty_oponent_value), Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedLineUp == null) {
            Toast.makeText(requireContext(), getString(R.string.toast_empty_lineUp_value), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), getString(R.string.toast_match_create), Toast.LENGTH_SHORT).show()
                        val action = MatchCreationFragmentDirections.actionMatchCreationFragmentToMatchListFragment()
                        findNavController().navigate(action)
                    }
            }
            .addOnFailureListener { e ->
                //Toast.makeText(requireContext(), "Error al crear el partido: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("MatchCreationFragment", "Error al crear el partido: ${e.message}")
                Toast.makeText(requireContext(), getString(R.string.toast_match_create_error), Toast.LENGTH_SHORT).show()
            }
    }
}

