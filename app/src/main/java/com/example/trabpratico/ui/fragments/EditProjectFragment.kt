package com.example.trabpratico.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trabpratico.databinding.FragmentEditProjectBinding
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.network.ProjectRequest
import com.example.trabpratico.network.UserDetailsResponse
import com.example.trabpratico.ui.UsersActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class EditProjectFragment : Fragment() {

    private lateinit var binding: FragmentEditProjectBinding
    private lateinit var apiService: ApiService
    private var projectId: Int = -1
    private lateinit var editTextStartDate: EditText
    private lateinit var editTextEndDate: EditText
    private lateinit var spinnerProjectManager: Spinner
    private var userList: List<UserDetailsResponse> = listOf()

    companion object {
        private const val ARG_PROJECT_ID = "project_id"

        fun newInstance(projectId: Int) = EditProjectFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PROJECT_ID, projectId)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditProjectBinding.inflate(inflater, container, false)
        apiService = RetrofitClient.instance
        projectId = arguments?.getInt(ARG_PROJECT_ID) ?: -1

        editTextStartDate = binding.editTextStartDate
        editTextEndDate = binding.editTextEndDate
        spinnerProjectManager = binding.spinnerProjectManager

        editTextStartDate.setOnClickListener {
            showDatePicker(editTextStartDate)
        }

        editTextEndDate.setOnClickListener {
            showDatePicker(editTextEndDate)
        }

        binding.buttonUpdateProject.setOnClickListener {
            updateProjectDetails()
        }

        binding.buttonDeleteProject.setOnClickListener {
            deleteProject(projectId)
        }

        loadProjectDetails(projectId)
        fetchUsers()

        return binding.root
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            editText.setText(selectedDate)
        }

        DatePickerDialog(requireContext(), dateSetListener, year, month, day).show()
    }

    private fun fetchUsers() {
        RetrofitClient.instance.getAllUsers().enqueue(object : Callback<List<UserDetailsResponse>> {
            override fun onResponse(call: Call<List<UserDetailsResponse>>, response: Response<List<UserDetailsResponse>>) {
                if (response.isSuccessful) {
                    userList = response.body() ?: listOf()
                    val userNames = userList.map { it.name }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerProjectManager.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadProjectDetails(projectId: Int) {
        apiService.getProjectById(projectId).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { project ->
                        binding.project = project
                        editTextStartDate.setText(project.startdatep)
                        editTextEndDate.setText(project.enddatep)
                    }
                } else {
                    Toast.makeText(context, "Failed to load project details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProjectDetails() {
        val project = binding.project ?: return
        val nameProject = binding.editTextProjectName.text.toString()
        val startDate = binding.editTextStartDate.text.toString()
        val endDate = binding.editTextEndDate.text.toString()
        val projectManagerIndex = spinnerProjectManager.selectedItemPosition

        if (projectManagerIndex == -1 || nameProject.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val projectManagerId = userList[projectManagerIndex].iduser

        val projectUpdateRequest = ProjectRequest(
            nameproject = nameProject,
            startdatep = startDate,
            enddatep = endDate,
            idstate = project.idstate,
            iduser = projectManagerId,
            completionstatus = project.completionstatus,
            performancereview = project.performancereview,
            obs = project.obs
        )

        apiService.updateProject(project.idproject, projectUpdateRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Project updated successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, "Failed to update project", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteProject(projectId: Int) {
        apiService.deleteProject(projectId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Project deleted successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, "Failed to delete project", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
