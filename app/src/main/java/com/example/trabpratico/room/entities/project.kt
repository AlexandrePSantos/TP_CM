package com.example.trabpratico.room.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class project(
    @PrimaryKey val idproject: Int,
    val nameproject: String,
    val startdatep: Long?,
    val enddatep: Long?,
    val idstate: Int,
    val iduser: Int,
    val completionstatus: Boolean,
    val performancereview: String?,
    val obs: String?
)