package com.example.squadme.MainActivity.training.trainingCreation

import androidx.lifecycle.ViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


/**
 * ViewModel to handle business logic related to training creation and management.
 *
 * Uses MutableStateFlow to store and emit reactive changes in the list of exercises.
 */
class TrainingViewModel : ViewModel() {
    private val _ejercicios = MutableStateFlow<List<String>>(emptyList())
    val ejercicios: StateFlow<List<String>> get() = _ejercicios

    /**
     * Method to add a new exercise to the list of exercises.
     *
     * @param ejercicio Name of the exercise to add.
     */
    fun addEjercicio(ejercicio: String) {
        _ejercicios.value = _ejercicios.value + ejercicio
    }
}