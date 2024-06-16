package com.example.trabpratico.ui.Gestor

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.adapter.TaskAdapter
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.TaskResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectTasksListActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var pendingTasksAdapter: TaskAdapter
    private lateinit var finishedTasksAdapter: TaskAdapter
    private var projectId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_tasks_list)

        apiService = RetrofitClient.instance
        projectId = intent.getIntExtra("PROJECT_ID", -1)

        val recyclerViewPendingTasks = findViewById<RecyclerView>(R.id.recyclerViewPendingTasks)
        recyclerViewPendingTasks.layoutManager = LinearLayoutManager(this)
        pendingTasksAdapter = TaskAdapter()
        recyclerViewPendingTasks.adapter = pendingTasksAdapter
        recyclerViewPendingTasks.visibility = View.VISIBLE

        val recyclerViewFinishedTasks = findViewById<RecyclerView>(R.id.recyclerViewFinishedTasks)
        recyclerViewFinishedTasks.layoutManager = LinearLayoutManager(this)
        finishedTasksAdapter = TaskAdapter()
        recyclerViewFinishedTasks.adapter = finishedTasksAdapter
        recyclerViewFinishedTasks.visibility = View.VISIBLE

        fetchProjectTasks()
    }

    private fun fetchProjectTasks() {
        apiService.getAllTasks().enqueue(object : Callback<List<TaskResponse>> {
            override fun onResponse(call: Call<List<TaskResponse>>, response: Response<List<TaskResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { tasks ->
                        val projectTasks = tasks.filter { it.idproject == projectId }
                        val pendingTasks = projectTasks.filter { it.idstate == 1 || it.idstate == 2 }
                        val finishedTasks = projectTasks.filter { it.idstate == 3 }

                        pendingTasksAdapter.submitList(pendingTasks)
                        finishedTasksAdapter.submitList(finishedTasks)
                    }
                } else {
                    Toast.makeText(this@ProjectTasksListActivity, "Failed to load tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TaskResponse>>, t: Throwable) {
                Toast.makeText(this@ProjectTasksListActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}