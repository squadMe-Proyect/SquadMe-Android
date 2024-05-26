package com.example.squadme.data.Models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.util.Date

@IgnoreExtraProperties
data class Match(
    var id:String?=null,
    val coachId: String? = null,
    val date:String? = null,
    val opponent: String? = null,
    val result:String? = null,
    val squad: LineUp?=null,
    val finished: Boolean=false
): Serializable
