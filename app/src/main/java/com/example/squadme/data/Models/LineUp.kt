package com.example.squadme.data.Models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
@IgnoreExtraProperties
data class LineUp(
    var id: String?=null,
    val coachId: String?=null,
    val name: String?=null,
    val lineUp: String?=null,
    val players: List<Player> = listOf()
): Serializable
