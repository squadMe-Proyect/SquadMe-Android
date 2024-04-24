package com.example.squadme.data.Models

data class Player(
    val id:String,
    val coachId:String,
    val picture: String,
    val email:String,
    val name:String,
    val surname:String,
    val teamName:String,
    val nation:String,
    val numbers: Int,
    val position: Position,
    val goal:Int,
    val assists: Int,
    val yellowCards: Int,
    val redCards:Int
)
