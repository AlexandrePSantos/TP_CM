package com.example.trabpratico.room.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class usertype(
    @PrimaryKey val idtype: Int,
    val type: String
)