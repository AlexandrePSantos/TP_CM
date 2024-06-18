package com.example.trabpratico.ui.Gestor

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.adapter.UserObsAdapter
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ObsResponse
import com.example.trabpratico.network.UserDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListObsActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var userObsAdapter: UserObsAdapter
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_obs)

        apiService = RetrofitClient.instance
        taskId = intent.getIntExtra("TASK_ID", -1)

        val recyclerViewUserObservations = findViewById<RecyclerView>(R.id.recyclerViewUserObservations)
        recyclerViewUserObservations.layoutManager = LinearLayoutManager(this)
        userObsAdapter = UserObsAdapter()
        recyclerViewUserObservations.adapter = userObsAdapter

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }

        loadObservations()
    }

    private fun loadObservations() {
        apiService.getAllObs().enqueue(object : Callback<List<ObsResponse>> {
            override fun onResponse(call: Call<List<ObsResponse>>, response: Response<List<ObsResponse>>) {
                if (response.isSuccessful) {
                    val observations = response.body()?.filter { it.idtask == taskId } ?: emptyList()
                    if (observations.isNotEmpty()) {
                        loadUsers(observations)
                    } else {
                        Toast.makeText(this@ListObsActivity, "No observations found for this task", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ListObsActivity, "Failed to load observations", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ObsResponse>>, t: Throwable) {
                Toast.makeText(this@ListObsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadUsers(observations: List<ObsResponse>) {
        apiService.getAllUsers().enqueue(object : Callback<List<UserDetailsResponse>> {
            override fun onResponse(call: Call<List<UserDetailsResponse>>, response: Response<List<UserDetailsResponse>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    val userObservationsMap = users.associateWith { user ->
                        observations.filter { it.iduser == user.iduser }
                    }.filter { it.value.isNotEmpty() }

                    userObsAdapter.submitUserObservations(userObservationsMap)
                } else {
                    Toast.makeText(this@ListObsActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
                Toast.makeText(this@ListObsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}