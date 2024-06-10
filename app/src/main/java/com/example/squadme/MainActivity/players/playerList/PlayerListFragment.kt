package com.example.squadme.MainActivity.players.playerList


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.squadme.MainActivity.matches.matchesList.MatchListFragmentDirections
import com.example.squadme.R
import com.example.squadme.data.Models.Player
import com.example.squadme.databinding.FragmentPlayerListBinding
import com.example.squadme.utils.FirestoreSingleton
import com.example.squadme.utils.NetworkUtils
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source

import dagger.hilt.android.AndroidEntryPoint
/*
@AndroidEntryPoint
class PlayerListFragment : Fragment() {
    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: PlayerListAdapter
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore = FirestoreSingleton.getInstance()
        firestore.firestoreSettings = firestoreSettings
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = PlayerListAdapter(requireContext()) { _, player ->
            val actionToDetail = PlayerListFragmentDirections.actionPlayerListFragmentToPlayerDetailFragment(player)
            view.findNavController().navigate(actionToDetail)
        }
        binding.playerList.adapter = adapter

        binding.btnCreationPlayer.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (isAdmin) {
                    val action = PlayerListFragmentDirections.actionPlayerListFragmentToCameraPreviewFragment()
                    view.findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), "No tienes permiso para crear un jugador.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        // Mostrar un indicador de carga mientras se obtienen los datos
        checkUserRoleAndFetchPlayers()
    }

    private fun checkUserRoleAndFetchPlayers() {
        val coachesRef = firestore.collection("coaches").document(currentUserId)
        val playersRef = firestore.collection("players").document(currentUserId)

        // Realizar ambas consultas en paralelo
        Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
            .addOnSuccessListener { results ->
                val coachDocument = results[0]
                val playerDocument = results[1]

                when {
                    coachDocument.exists() ->{
                        isAdmin = true
                        fetchPlayersByAdmin()
                    }
                    playerDocument.exists() -> {
                        isAdmin = false
                        val coachId = playerDocument.getString("coachId")
                        coachId?.let { fetchPlayersByCoachId(it) }
                    }
                    else -> {
                        Log.e("PlayerListFragment", "User is neither coach nor player.")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error checking user role: $exception")
                fetchPlayersFromCache()
            }
    }

    private fun fetchPlayersByAdmin() {
        firestore.collection("players")
            .whereEqualTo("coachId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        playerList.add(it)
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players by admin: $exception")
                fetchPlayersFromCache()
            }
    }

    private fun fetchPlayersByCoachId(coachId: String) {
        firestore.collection("players")
            .whereEqualTo("coachId", coachId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        playerList.add(it)
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players by coachId: $exception")
                // Intentar cargar los datos desde la caché
                fetchPlayersFromCache()
            }
    }

    private fun fetchPlayersFromCache() {
        firestore.collection("players")
            .get(Source.CACHE)
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        // Filtrar los jugadores por coachId
                        if (it.coachId == currentUserId) {
                            playerList.add(it)
                        }
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players from cache: $exception")
                Toast.makeText(requireContext(), getString(R.string.toast_error_load_playerList_cache), Toast.LENGTH_SHORT).show()
            }
    }
}
 */
/*
@AndroidEntryPoint
class PlayerListFragment : Fragment() {
    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: PlayerListAdapter
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore = FirestoreSingleton.getInstance()
        firestore.firestoreSettings = firestoreSettings
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = PlayerListAdapter(requireContext()) { _, player ->
            val actionToDetail = PlayerListFragmentDirections.actionPlayerListFragmentToPlayerDetailFragment(player)
            view.findNavController().navigate(actionToDetail)
        }
        binding.playerList.adapter = adapter

        binding.btnCreationPlayer.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (isAdmin) {
                    val action = PlayerListFragmentDirections.actionPlayerListFragmentToCameraPreviewFragment()
                    view.findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), "No tienes permiso para crear un jugador.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        // Mostrar un indicador de carga mientras se obtienen los datos
        checkUserRoleAndFetchPlayers()
    }

    private fun checkUserRoleAndFetchPlayers() {
        val coachesRef = firestore.collection("coaches").document(currentUserId)
        val playersRef = firestore.collection("players").document(currentUserId)

        try {
            Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
                .addOnSuccessListener { results ->
                    val coachDocument = results[0]
                    val playerDocument = results[1]

                    when {
                        coachDocument.exists() -> {
                            isAdmin = true
                            fetchPlayersByAdmin()
                        }
                        playerDocument.exists() -> {
                            isAdmin = false
                            val coachId = playerDocument.getString("coachId")
                            coachId?.let { fetchPlayersByCoachId(it) }
                        }
                        else -> {
                            Log.e("PlayerListFragment", "User is neither coach nor player.")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("PlayerListFragment", "Error checking user role: $exception")
                    fetchPlayersFromCache()
                }
        } catch (e: Exception) {
            Log.e("PlayerListFragment", "Error: $e")
            fetchPlayersFromCache()
        }
    }

    private fun fetchPlayersByAdmin() {
        firestore.collection("players")
            .whereEqualTo("coachId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        playerList.add(it)
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players by admin: $exception")
                fetchPlayersFromCache()
            }
    }

    private fun fetchPlayersByCoachId(coachId: String) {
        firestore.collection("players")
            .whereEqualTo("coachId", coachId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        playerList.add(it)
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players by coachId: $exception")
                // Intentar cargar los datos desde la caché
                fetchPlayersFromCache()
            }
    }

    private fun fetchPlayersFromCache() {
        firestore.collection("players")
            .get(Source.CACHE)
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        // Filtrar los jugadores por coachId
                        if (it.coachId == currentUserId) {
                            playerList.add(it)
                        }
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players from cache: $exception")
                Toast.makeText(requireContext(), getString(R.string.toast_error_load_playerList_cache), Toast.LENGTH_SHORT).show()
            }
    }
}


 */

/*
@AndroidEntryPoint
class PlayerListFragment : Fragment() {
    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: PlayerListAdapter
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore = FirestoreSingleton.getInstance()
        firestore.firestoreSettings = firestoreSettings
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = PlayerListAdapter(requireContext()) { _, player ->
            val actionToDetail = PlayerListFragmentDirections.actionPlayerListFragmentToPlayerDetailFragment(player)
            view.findNavController().navigate(actionToDetail)
        }
        binding.playerList.adapter = adapter

        binding.btnCreationPlayer.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (isAdmin) {
                    val action = PlayerListFragmentDirections.actionPlayerListFragmentToCameraPreviewFragment()
                    view.findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), "No tienes permiso para crear un jugador.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        // Mostrar un indicador de carga mientras se obtienen los datos
        checkUserRoleAndFetchPlayers()
    }

    private fun checkUserRoleAndFetchPlayers() {
        val coachesRef = firestore.collection("coaches").document(currentUserId)
        val playersRef = firestore.collection("players").document(currentUserId)

        // Realizar ambas consultas en paralelo
        Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
            .addOnSuccessListener { results ->
                val coachDocument = results[0]
                val playerDocument = results[1]

                when {
                    coachDocument.exists() ->{
                        isAdmin = true
                        fetchPlayersByAdmin()
                    }
                    playerDocument.exists() -> {
                        isAdmin = false
                        val coachId = playerDocument.getString("coachId")
                        coachId?.let { fetchPlayersByCoachId(it) }
                    }
                    else -> {
                        Log.e("PlayerListFragment", "User is neither coach nor player.")
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseFirestoreException && exception.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                    // La conexión está desactivada, intentar cargar los datos desde la caché local
                    fetchPlayersFromCache()
                } else {
                    Log.e("PlayerListFragment", "Error checking user role: $exception")
                }
            }
    }

    private fun fetchPlayersByAdmin() {
        firestore.collection("players")
            .whereEqualTo("coachId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        playerList.add(it)
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players by admin: $exception")
                fetchPlayersFromCache()
            }
    }

    private fun fetchPlayersByCoachId(coachId: String) {
        firestore.collection("players")
            .whereEqualTo("coachId", coachId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        playerList.add(it)
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players by coachId: $exception")
                // Intentar cargar los datos desde la caché
                fetchPlayersFromCache()
            }
    }

    private fun fetchPlayersFromCache() {
        firestore.collection("players")
            .get(Source.CACHE)
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        // Filtrar los jugadores por coachId
                        if (it.coachId == currentUserId) {
                            playerList.add(it)
                        }
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players from cache: $exception")
                Toast.makeText(requireContext(), getString(R.string.toast_error_load_playerList_cache), Toast.LENGTH_SHORT).show()
            }
    }
}
 */


@AndroidEntryPoint
class PlayerListFragment : Fragment() {
    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserId: String
    private lateinit var adapter: PlayerListAdapter
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirestoreSingleton.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""

        adapter = PlayerListAdapter(requireContext()) { _, player ->
            val actionToDetail = PlayerListFragmentDirections.actionPlayerListFragmentToPlayerDetailFragment(player)
            view.findNavController().navigate(actionToDetail)
        }
        binding.playerList.adapter = adapter

        binding.btnCreationPlayer.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                if (isAdmin) {
                    val action = PlayerListFragmentDirections.actionPlayerListFragmentToCameraPreviewFragment()
                    view.findNavController().navigate(action)
                } else {
                    Toast.makeText(requireContext(), "No tienes permiso para crear un jugador.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        checkUserRoleAndFetchPlayers()
    }

    private fun checkUserRoleAndFetchPlayers() {
        val coachesRef = firestore.collection("coaches").document(currentUserId)
        val playersRef = firestore.collection("players").document(currentUserId)

        try {
            Tasks.whenAllSuccess<DocumentSnapshot>(coachesRef.get(), playersRef.get())
                .addOnSuccessListener { results ->
                    val coachDocument = results[0]
                    val playerDocument = results[1]

                    when {
                        coachDocument.exists() -> {
                            isAdmin = true
                            fetchPlayersByAdmin()
                        }
                        playerDocument.exists() -> {
                            isAdmin = false
                            val coachId = playerDocument.getString("coachId")
                            coachId?.let { fetchPlayersFromCache("player") }
                        }
                        else -> {
                            Log.e("PlayerListFragment", "Unknown user type")
                            fetchPlayersFromCache("unknown")
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("PlayerListFragment", "Error checking user role: $exception")
                    fetchPlayersFromCache("unknown")
                }
        } catch (e: Exception) {
            Log.e("PlayerListFragment", "Error: $e")
            fetchPlayersFromCache("unknown")
        }
    }

    private fun fetchPlayersByAdmin() {
        firestore.collection("players")
            .whereEqualTo("coachId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val playerList = mutableListOf<Player>()
                for (document in querySnapshot.documents) {
                    val player = document.toObject(Player::class.java)
                    player?.let {
                        playerList.add(it)
                    }
                }
                adapter.submitList(playerList)
            }
            .addOnFailureListener { exception ->
                Log.e("PlayerListFragment", "Error fetching players by admin: $exception")
                fetchPlayersFromCache("coach")
            }
    }

    private fun fetchPlayersFromCache(userType: String) {
        if (isAdmin) {
            firestore.collection("players")
                .get(Source.CACHE)
                .addOnSuccessListener { querySnapshot ->
                    val playerList = mutableListOf<Player>()
                    for (document in querySnapshot.documents) {
                        val player = document.toObject(Player::class.java)
                        player?.let {
                            if (it.coachId == currentUserId) {
                                playerList.add(it)
                            }
                        }
                    }
                    adapter.submitList(playerList)
                }
                .addOnFailureListener { exception ->
                    Log.e("PlayerListFragment", "Error fetching players from cache: $exception")
                }
        } else {
            val prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val coachId = prefs.getString("coachId", "")

            coachId?.let { id ->
                firestore.collection("players")
                    .whereEqualTo("coachId", id)
                    .get(Source.CACHE)
                    .addOnSuccessListener { querySnapshot ->
                        val playerList = mutableListOf<Player>()
                        for (document in querySnapshot.documents) {
                            val player = document.toObject(Player::class.java)
                            player?.let {
                                playerList.add(it)
                            }
                        }
                        adapter.submitList(playerList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("PlayerListFragment", "Error fetching players from cache: $exception")
                    }
            }
        }
    }
}






