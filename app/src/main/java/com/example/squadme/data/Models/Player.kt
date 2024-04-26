package com.example.squadme.data.Models

data class Player(
    val coachId:String,
    val picture: String,
    val email:String,
    val name:String,
    val surname:String,
    val teamName:String,
    val nation:String,
    val numbers: Int,
    val position: String,
    val goal:Int,
    val assists: Int,
    val yellowCards: Int,
    val redCards:Int
)
