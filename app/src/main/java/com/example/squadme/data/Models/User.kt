package com.example.squadme.data.Models

data class User(
    val id: String,
    val name: String,
    val email:String,
    val nationality: String,
    val photo: String,
    val rol: Role,
    val password: String,
    val player: Player?,
    val mister: Mister?
    )
