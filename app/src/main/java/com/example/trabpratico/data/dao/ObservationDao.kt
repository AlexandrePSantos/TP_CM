package com.example.trabpratico.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.trabpratico.data.entities.Observation

@Dao
interface ObservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(observation: Observation)

    @Update
    suspend fun update(observation: Observation)

    @Delete
    suspend fun delete(observation: Observation)

    @Query("SELECT * FROM obs")
    fun getAllObservations(): LiveData<List<Observation>>

    @Query("SELECT * FROM obs WHERE id = :id")
    suspend fun getObservationById(id: Int): Observation?
}