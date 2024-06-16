package com.example.trabpratico.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.trabpratico.data.db.ObsDatabase
import com.example.trabpratico.data.entities.Observation
import com.example.trabpratico.data.repository.ObsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ObsViewModel(application: Application) : AndroidViewModel(application) {
    val readAllObservations: LiveData<List<Observation>>
    private val repository: ObsRepository

    init {
        val obsDao = ObsDatabase.getDatabase(application).obsDao()
        repository = ObsRepository(obsDao)
        readAllObservations = repository.readAllObservations
    }

    fun addObservation(observation: Observation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addObservation(observation)
        }
    }

    fun updateObservation(observation: Observation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateObservation(observation)
        }
    }

    fun deleteObservation(observation: Observation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteObservation(observation)
        }
    }
}