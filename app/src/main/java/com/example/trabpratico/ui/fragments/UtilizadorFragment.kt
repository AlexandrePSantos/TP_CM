package com.example.trabpratico.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.adapter.TaskAdapter
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.TaskResponse
import com.example.trabpratico.network.UserTaskResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UtilizadorFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var pendingTasksAdapter: TaskAdapter
    private lateinit var finishedTasksAdapter: TaskAdapter
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_utilizador, container, false)

        apiService = RetrofitClient.instance
        userId = RetrofitClient.getUserId() ?: -1

        val recyclerViewPendingTasks = view.findViewById<RecyclerView>(R.id.recyclerViewPendingTasks)
        recyclerViewPendingTasks.layoutManager = LinearLayoutManager(context)
        pendingTasksAdapter = TaskAdapter()
        recyclerViewPendingTasks.adapter = pendingTasksAdapter
        recyclerViewPendingTasks.visibility = View.VISIBLE

        val recyclerViewFinishedTasks = view.findViewById<RecyclerView>(R.id.recyclerViewFinishedTasks)
        recyclerViewFinishedTasks.layoutManager = LinearLayoutManager(context)
        finishedTasksAdapter = TaskAdapter()
        recyclerViewFinishedTasks.adapter = finishedTasksAdapter
        recyclerViewFinishedTasks.visibility = View.VISIBLE

        fetchUserTasks()

        return view
    }

    private fun fetchUserTasks() {
        apiService.getUserTasks().enqueue(object : Callback<List<UserTaskResponse>> {
            override fun onResponse(call: Call<List<UserTaskResponse>>, response: Response<List<UserTaskResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { userTasks ->
                        val userTaskIds = userTasks.filter { it.iduser == userId }.map { it.idtask }
                        fetchTasks(userTaskIds)
                    }
                } else {
                    Toast.makeText(context, "Failed to load user tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserTaskResponse>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTasks(userTaskIds: List<Int>) {
        apiService.getAllTasks().enqueue(object : Callback<List<TaskResponse>> {
            override fun onResponse(call: Call<List<TaskResponse>>, response: Response<List<TaskResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { tasks ->
                        val userTasks = tasks.filter { it.idtask in userTaskIds }
                        val pendingTasks = userTasks.filter { it.idstate == 1 || it.idstate == 2 }
                        val finishedTasks = userTasks.filter { it.idstate == 3 }

                        pendingTasksAdapter.submitList(pendingTasks)
                        finishedTasksAdapter.submitList(finishedTasks)
                    }
                } else {
                    Toast.makeText(context, "Failed to load tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TaskResponse>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}