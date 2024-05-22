package com.example.squadme.data.Models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.util.Date

@IgnoreExtraProperties
data class Training(
    val coachId:String?=null,
    val date:Date?=null,
    val exercises:List<String> = listOf(),
    val completed: Boolean = false
):Serializable
