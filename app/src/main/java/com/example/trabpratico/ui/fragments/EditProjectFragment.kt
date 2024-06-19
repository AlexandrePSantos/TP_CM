package com.example.trabpratico.ui.fragments

import RetrofitClient
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trabpratico.databinding.FragmentEditProjectBinding
import com.example.trabpratico.network.ProjectRequest
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.network.UserDetailsResponse
import com.example.trabpratico.ui.EditProjectActivity
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
    private lateinit var userList: List<UserDetailsResponse>
    private val calendar = Calendar.getInstance()

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

        setupViews()
        fetchUsers()
        fetchProjectDetails(projectId)
    }

    private fun setupViews() {
        binding.apply {
            editTextStartDate.setOnClickListener {
                showDatePicker(binding.editTextStartDate)
            }

            editTextEndDate.setOnClickListener {
                showDatePicker(binding.editTextEndDate)
            }

            buttonUpdateProject.setOnClickListener {
                updateProject()
            }

            buttonDeleteProject.setOnClickListener {
                deleteProject()
            }
        }

        binding.buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(Calendar.YEAR, selectedYear)
            calendar.set(Calendar.MONTH, selectedMonth)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editText.setText(dateFormat.format(calendar.time))
        }

        DatePickerDialog(
            requireContext(), dateSetListener,
            year, month, day
        ).show()
    }


    private fun fetchProjectDetails(projectId: Int) {
        RetrofitClient.instance.getProjectById(projectId).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { project ->
                        binding.apply {
                            editTextProjectName.setText(project.nameproject ?: "")
                            editTextStartDate.setText(formatDateForDisplay(project.startdatep ?: ""))
                            editTextEndDate.setText(formatDateForDisplay(project.enddatep ?: ""))
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch project details", Toast.LENGTH_SHORT).show()
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
                    userList = allUsers.filter { it.idtype == 2 }
                    val userNames = userList.map { it.username }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerProjectManager.adapter = adapter

                    val currentProjectManagerId = response.body()?.find { it.iduser == projectId }?.iduser
                    val position = userNames.indexOfFirst { it == currentProjectManagerId?.toString() }

                    binding.spinnerProjectManager.setSelection(position)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProject() {
        val name = binding.editTextProjectName.text.toString()
        val startDate = convertToISO8601(binding.editTextStartDate.text.toString())
        val endDate = convertToISO8601(binding.editTextEndDate.text.toString())
        val projectManagerId = userList[binding.spinnerProjectManager.selectedItemPosition].iduser

        val projectRequest = ProjectRequest(
            nameproject = name,
            startdatep = startDate,
            enddatep = endDate,
            idstate = 1,
            iduser = projectManagerId,
            completionstatus = false,
            performancereview = null,
            obs = null
        )

        RetrofitClient.instance.updateProject(projectId, projectRequest)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Project updated successfully", Toast.LENGTH_SHORT).show()
                        navigateToEditProjectActivity()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update project", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteProject() {
        RetrofitClient.instance.deleteProject(projectId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Project deleted successfully", Toast.LENGTH_SHORT).show()
                    navigateToEditProjectActivity() // Navega para EditProjectActivity após deletar
                } else {
                    Toast.makeText(requireContext(), "Failed to delete project", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatDateForDisplay(date: String): String {
        val parts = date.split("T")[0].split("-")
        return "${parts[2]}/${parts[1]}/${parts[0]}"
    }

    private fun convertToISO8601(date: String): String {
        val parts = date.split("/")
        return "${parts[2]}-${parts[1]}-${parts[0]}T00:00:00Z"
    }

    private fun navigateToEditProjectActivity() {
        val intent = Intent(requireContext(), EditProjectActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
