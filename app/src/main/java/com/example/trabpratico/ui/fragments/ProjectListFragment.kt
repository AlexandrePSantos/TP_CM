package com.example.trabpratico.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ProjectRequest
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.ui.adapters.ProjectAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectListFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_list, container, false)

        apiService = RetrofitClient.instance
        projectAdapter = ProjectAdapter(this::showEditProjectDialog, this::removeProject)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewProjects)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = projectAdapter

        fetchProjects()

        return view
    }

    private fun showEditProjectDialog(project: ProjectResponse) {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_add_project, null)
        val inputName = dialogView.findViewById<EditText>(R.id.editTextProjectName)
        val inputStartDate = dialogView.findViewById<EditText>(R.id.editTextStartDate)
        val inputEndDate = dialogView.findViewById<EditText>(R.id.editTextEndDate)

        inputName.setText(project.nameproject)
        inputStartDate.setText(project.startdatep)
        inputEndDate.setText(project.enddatep)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Project")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = inputName.text.toString()
                val startDate = inputStartDate.text.toString()
                val endDate = inputEndDate.text.toString()
                editProject(project, name, startDate, endDate)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }

    private fun editProject(project: ProjectResponse, name: String, startDate: String, endDate: String) {
        val projectRequest = ProjectRequest(
            name,
            startDate,
            endDate,
            project.idstate,
            project.iduser,
            project.completionstatus,
            project.performancereview,
            project.obs
        )

        apiService.updateProject(project.idproject, projectRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Project updated successfully")
                    fetchProjects()
                } else {
                    showToast("Failed to update project")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun removeProject(projectId: Int) {
        apiService.deleteProject(projectId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Project removed successfully")
                    fetchProjects()
                } else {
                    showToast("Failed to remove project")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun fetchProjects() {
        apiService.getProjects().enqueue(object : Callback<List<ProjectResponse>> {
            override fun onResponse(call: Call<List<ProjectResponse>>, response: Response<List<ProjectResponse>>) {
                if (response.isSuccessful) {
                    projectAdapter.submitList(response.body())
                } else {
                    showToast("Failed to fetch projects")
                }
            }

            override fun onFailure(call: Call<List<ProjectResponse>>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
