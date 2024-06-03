package com.example.trabpratico.room.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class task(
    @PrimaryKey val idtask: Int,
    val nametask: String,
    val startdatet: Long?,
    val enddatet: Long?,
    val idproject: Int,
    val idstate: Int,
    val photo: String?,
    val timespend: Long?,
    val local: String?,
    val taxes: Double?,
    val completionrate: Double?,
    val photos: String?,
    val observations: String?
)