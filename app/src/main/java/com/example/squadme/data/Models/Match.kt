package com.example.squadme.data.Models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.util.Date

@IgnoreExtraProperties
data class Match(
    val coachId: String? = null,
    val date:String? = null,
    val opponent: String? = null,
    val result:String? = null
): Serializable
