package com.example.trabpratico.room.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class synclog(
    @PrimaryKey val idlog: Int,
    val iduser: Int,
    val timestamp: Long,
    val action: String,
    val status: String
)