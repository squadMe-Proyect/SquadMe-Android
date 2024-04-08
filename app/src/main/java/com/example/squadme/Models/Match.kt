package com.example.squadme.Models

import java.util.Date

data class Match(
    val id: String,
    val date:Date,
    val rival: String,
    val score:String
)
