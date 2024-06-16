package com.example.trabpratico.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "obs")
class Observation(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val idtask: Int,
    val iduser: Int,
    @ColumnInfo(name = "content") val content: String
)