package com.example.trabpratico.room.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class user(
    @PrimaryKey val iduser: Int,
    val email: String,
    val photo: String?,
    val password: String,
    val idtype: Int,
    val username: String?,
    val name: String?,
    val last_login: Long?
)