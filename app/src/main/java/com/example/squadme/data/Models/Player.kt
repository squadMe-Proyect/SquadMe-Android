package com.example.squadme.data.Models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Player(
    var id:String? = null,
    val coachId: String? = null,
    val picture: String? = null,
    val email: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val teamName: String? = null,
    val nation: String? = null,
    val numbers: Int? = null,
    val position: String? = null,
    val goal: Int? = null,
    val assists: Int? = null,
    val yellowCards: Int? = null,
    val redCards: Int? = null
) : Serializable
