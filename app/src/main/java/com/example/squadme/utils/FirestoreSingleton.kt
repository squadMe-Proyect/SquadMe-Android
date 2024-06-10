package com.example.squadme.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings

object FirestoreSingleton {
        private var instance: FirebaseFirestore? = null

        fun getInstance(): FirebaseFirestore {
            if (instance == null) {
                instance = FirebaseFirestore.getInstance().apply {
                    firestoreSettings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                }
            }
            return instance!!
        }
}