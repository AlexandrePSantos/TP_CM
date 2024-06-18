package com.example.trabpratico.ui.fragments

import RetrofitClient
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trabpratico.databinding.FragmentEditProjectBinding
import com.example.trabpratico.network.ProjectRequest
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.network.UserDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProjectFragment : Fragment() {

    companion object {
        private const val ARG_PROJECT_ID = "PROJECT_ID"

        fun newInstance(projectId: Int) = EditProjectFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PROJECT_ID, projectId)
            }
        }
    }

    private var projectId: Int = -1
    private lateinit var binding: FragmentEditProjectBinding
    private val calendar = Calendar.getInstance()
    private lateinit var userList: List<UserDetailsResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectId = it.getInt(ARG_PROJECT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchProjectDetails(projectId)
        fetchUsers()

        binding.editTextStartDate.setOnClickListener {
            showDatePickerDialog { date -> binding.editTextStartDate.setText(date) }
        }

        binding.editTextEndDate.setOnClickListener {
            showDatePickerDialog { date -> binding.editTextEndDate.setText(date) }
        }

        binding.buttonUpdateProject.setOnClickListener {
            val name = binding.editTextProjectName.text.toString()
            val startDate = binding.editTextStartDate.text.toString()
            val endDate = binding.editTextEndDate.text.toString()
            val projectManagerIndex = binding.spinnerProjectManager.selectedItemPosition

            if (projectManagerIndex == -1 || name.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val projectManagerId = userList[projectManagerIndex].iduser
            updateProject(ProjectResponse(projectId, name, startDate, endDate, 1, projectManagerId, false, null, null))
        }

        binding.buttonDeleteProject.setOnClickListener {
            deleteProject(projectId)
        }

        binding.buttonBack.setOnClickListener {
            // Use the FragmentManager to pop the back stack
            parentFragmentManager.popBackStack()
        }
    }

    private fun fetchProjectDetails(projectId: Int) {
        RetrofitClient.instance.getProjectById(projectId).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { project ->
                        binding.editTextProjectName.setText(project.nameproject)
                        binding.editTextStartDate.setText(project.startdatep?.let { formatDateForDisplay(it) })
                        binding.editTextEndDate.setText(project.enddatep?.let { formatDateForDisplay(it) })
                    }
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUsers() {
        RetrofitClient.instance.getAllUsers().enqueue(object : Callback<List<UserDetailsResponse>> {
            override fun onResponse(call: Call<List<UserDetailsResponse>>, response: Response<List<UserDetailsResponse>>) {
                if (response.isSuccessful) {
                    val allUsers = response.body() ?: emptyList()
                    userList = allUsers.filter { it.idtype == 2 }  // Filtra apenas os usuários com idtype == 2 (gestores)
                    val userNames = userList.map { it.name }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerProjectManager.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProject(project: ProjectResponse) {
        val projectRequest = ProjectRequest(
            nameproject = project.nameproject,
            startdatep = project.startdatep?.let { convertToISO8601(it) },
            enddatep = project.enddatep?.let { convertToISO8601(it) },
            idstate = project.idstate,
            iduser = project.iduser,
            completionstatus = project.completionstatus,
            performancereview = project.performancereview,
            obs = project.obs
        )

        RetrofitClient.instance.updateProject(project.idproject, projectRequest)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Project updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update project", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteProject(projectId: Int) {
        RetrofitClient.instance.deleteProject(projectId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Project deleted successfully", Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager?.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete project", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            onDateSet(dateFormat.format(calendar.time))
        }

        DatePickerDialog(
            requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun convertToISO8601(date: String): String {
        val parts = date.split("-")
        return "${parts[0]}-${parts[1]}-${parts[2]}T00:00:00.000Z"
    }

    private fun formatDateForDisplay(date: String): String {
        val parts = date.split("T")[0].split("-")
        return "${parts[2]}/${parts[1]}/${parts[0]}"
    }


}
