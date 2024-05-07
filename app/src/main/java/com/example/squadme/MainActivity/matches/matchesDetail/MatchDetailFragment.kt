package com.example.squadme.MainActivity.matches.matchesDetail

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.squadme.R
import com.example.squadme.data.Models.Match
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentMatchDetailBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchDetailFragment : Fragment() {
    private lateinit var binding: FragmentMatchDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val args: MatchDetailFragmentArgs by navArgs()
        val match: Match = args.match



        binding.matchOpponent.text =  "Oponente: $match.opponent"
        binding.matchResult.text = "Resultado: $match.result"
        binding.matchDate.text = "Fecha: $match.date"

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editBtn.setOnClickListener {
            val action = MatchDetailFragmentDirections.actionMatchDetailFragmentToMatchUpdateFragment(match)
            findNavController().navigate(action)
        }

        binding.deleteBtn.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

            var matchRef: DocumentReference? = null

            db.collection("matches")
                .whereEqualTo("date", match.date)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val playerDocument = documents.first()
                        matchRef = playerDocument.reference
                        Log.d("Documento" ,"${matchRef.toString()}")

                        // Eliminar el jugador de Firestore
                        matchRef?.delete()
                            ?.addOnSuccessListener {
                                // Éxito al eliminar el jugador
                                Log.d(ContentValues.TAG, "Match deleted successfully")
                                // Regresar al fragmento anterior después de eliminar el jugador
                                findNavController().popBackStack()
                            }
                            ?.addOnFailureListener { e ->
                                // Error al eliminar el jugador
                                Log.w(ContentValues.TAG, "Error deleting match", e)
                                // Manejar el error según sea necesario (por ejemplo, mostrar un mensaje de error)
                            }
                    } else {
                        Log.d(ContentValues.TAG, "No player document found with name: ${match.opponent}")
                    }
                }
                .addOnFailureListener { e ->
                    // Error al realizar la consulta
                    Log.w(ContentValues.TAG, "Error querying match document", e)
                    // Manejar el error según sea necesario (por ejemplo, mostrar un mensaje de error)
                }
        }


    }

}