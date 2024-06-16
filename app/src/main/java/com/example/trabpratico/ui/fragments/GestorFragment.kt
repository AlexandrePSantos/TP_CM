package com.example.trabpratico.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.ui.Gestor.ProjectActivity
import com.example.trabpratico.ui.ProjectAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GestorFragment : Fragment() {
    private lateinit var apiService: ApiService
    private lateinit var pendingProjectAdapter: ProjectAdapter
    private lateinit var finishedProjectAdapter: ProjectAdapter
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gestor, container, false)

        apiService = RetrofitClient.instance
        userId = RetrofitClient.getUserId() ?: -1

        val recyclerViewPendingProject = view.findViewById<RecyclerView>(R.id.recyclerViewPendingProject)
        recyclerViewPendingProject.layoutManager = LinearLayoutManager(context)
        pendingProjectAdapter = ProjectAdapter { project -> openProjectDetails(project) }
        recyclerViewPendingProject.adapter = pendingProjectAdapter

        val recyclerViewFinishedProject = view.findViewById<RecyclerView>(R.id.recyclerViewFinishedProject)
        recyclerViewFinishedProject.layoutManager = LinearLayoutManager(context)
        finishedProjectAdapter = ProjectAdapter { project -> openProjectDetails(project) }
        recyclerViewFinishedProject.adapter = finishedProjectAdapter

        fetchUserProjects()

        return view
    }

    private fun fetchUserProjects() {
        apiService.getAllProjects().enqueue(object : Callback<List<ProjectResponse>> {
            override fun onResponse(call: Call<List<ProjectResponse>>, response: Response<List<ProjectResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { projects ->
                        val userProjects = projects.filter { it.iduser == userId }
                        val pendingProjects = userProjects.filter { it.idstate == 1 || it.idstate == 2 }
                        val finishedProjects = userProjects.filter { it.idstate == 3 }

                        pendingProjectAdapter.submitList(pendingProjects)
                        finishedProjectAdapter.submitList(finishedProjects)
                    }
                } else {
                    Toast.makeText(context, "Failed to load user projects", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ProjectResponse>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openProjectDetails(project: ProjectResponse) {
        val intent = Intent(context, ProjectActivity::class.java).apply {
            putExtra("PROJECT_ID", project.idproject)
        }
        startActivity(intent)
    }
}
