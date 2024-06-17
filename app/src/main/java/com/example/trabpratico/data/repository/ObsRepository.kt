package com.example.trabpratico.data.repository

import androidx.lifecycle.LiveData
import com.example.trabpratico.data.dao.ObservationDao
import com.example.trabpratico.data.entities.Observation

class ObsRepository(private val obsDao: ObservationDao) {
    val readAllObservations: LiveData<List<Observation>> = obsDao.getAllObservations()

    suspend fun addObservation(observation: Observation) {
        obsDao.insert(observation)
    }

    suspend fun updateObservation(observation: Observation) {
        obsDao.update(observation)
    }

    suspend fun deleteObservation(observation: Observation) {
        obsDao.delete(observation)
    }

    suspend fun getUnsyncedObservations(): List<Observation> {
        return obsDao.getUnsyncedObservations()
    }
}
