package com.example.squadme.Models

import java.util.Date

data class Training(
    val id:String,
    val date:Date,
    val exercises:List<String>,
    val completed: Boolean
)
