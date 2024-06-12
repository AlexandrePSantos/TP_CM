package com.example.trabpratico.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trabpratico.R
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ProjectRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminFragment : Fragment() {

    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        apiService = RetrofitClient.instance

        val buttonAddProject = view.findViewById<Button>(R.id.buttonAddProject)
        buttonAddProject.setOnClickListener {
            showAddProjectDialog()
        }

        val buttonListProjects = view.findViewById<Button>(R.id.buttonListProjects)
        buttonListProjects.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProjectListFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun showAddProjectDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_add_project, null)
        val inputName = dialogView.findViewById<EditText>(R.id.editTextProjectName)
        val inputStartDate = dialogView.findViewById<EditText>(R.id.editTextStartDate)
        val inputEndDate = dialogView.findViewById<EditText>(R.id.editTextEndDate)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Project")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = inputName.text.toString()
                val startDate = inputStartDate.text.toString()
                val endDate = inputEndDate.text.toString()
                addProject(name, startDate, endDate)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }

    private fun addProject(name: String, startDate: String, endDate: String) {
        val projectRequest = ProjectRequest(
            name,
            startDate,
            endDate,
            idstate = 1,
            iduser = RetrofitClient.getUserId() ?: -1,
            completionstatus = false,
            performancereview = null,
            obs = null
        )

        apiService.createProject(projectRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Project added successfully")
                } else {
                    showToast("Failed to add project")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
