package com.example.squadme.data.Models

import com.google.firebase.Timestamp
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Training(
    var id: String? = null,
    val coachId:String?=null,
    val date: Timestamp?=null,
    val exercises:List<String> = listOf(),
    val completed: Boolean = false
):Serializable
