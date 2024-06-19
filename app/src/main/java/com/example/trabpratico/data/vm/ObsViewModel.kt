package com.example.trabpratico.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.trabpratico.data.db.ObsDatabase
import com.example.trabpratico.data.entities.Observation
import com.example.trabpratico.data.repository.ObsRepository
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ObsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun syncObservations(apiService: ApiService, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val unsyncedObservations = repository.getUnsyncedObservations()
            if (unsyncedObservations.isNotEmpty()) {
                var success = true
                for (observation in unsyncedObservations) {
                    val obsRequest = ObsRequest(observation.idtask, observation.iduser, observation.content)
                    val response = apiService.createObs(obsRequest).execute()
                    if (response.isSuccessful) {
                        repository.deleteObservation(observation)
                    } else {
                        success = false
                    }
                }
                withContext(Dispatchers.Main) {
                    callback(success)
                }
            } else {
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }
}
