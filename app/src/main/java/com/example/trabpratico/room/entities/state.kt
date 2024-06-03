package com.example.trabpratico.room.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class state(
    @PrimaryKey val idstate: Int,
    val state: String
)