package com.example.trabpratico.ui

import RetrofitClient
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.databinding.ActivityCreateProjectBinding
import com.example.trabpratico.network.ProjectRequest
import com.example.trabpratico.network.UserDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class CreateProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProjectBinding
    private lateinit var userList: List<UserDetailsResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        fetchUsers()
    }

    private fun setupViews() {
        binding.apply {
            editTextStartDate.setOnClickListener {
                showDatePicker(binding.editTextStartDate)
            }

            editTextEndDate.setOnClickListener {
                showDatePicker(binding.editTextEndDate)
            }

            buttonSaveProject.setOnClickListener {
                saveProject()
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this@CreateProjectActivity, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            editText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun fetchUsers() {
        RetrofitClient.instance.getAllUsers().enqueue(object : Callback<List<UserDetailsResponse>> {
            override fun onResponse(call: Call<List<UserDetailsResponse>>, response: Response<List<UserDetailsResponse>>) {
                if (response.isSuccessful) {
                    val allUsers = response.body() ?: emptyList()
                    userList = allUsers.filter { it.idtype == 2 }  // Filtra apenas os usuários com idtype == 2 (gestores)
                    val userNames = userList.map { it.name }
                    val adapter = ArrayAdapter(this@CreateProjectActivity, android.R.layout.simple_spinner_item, userNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerProjectManager.adapter = adapter
                } else {
                    Toast.makeText(this@CreateProjectActivity, "Failed to fetch users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
                Toast.makeText(this@CreateProjectActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun saveProject() {
        val nameProject = binding.editTextProjectName.text.toString()
        val startDate = convertToISO8601(binding.editTextStartDate.text.toString())
        val endDate = convertToISO8601(binding.editTextEndDate.text.toString())

        val projectManagerIndex = binding.spinnerProjectManager.selectedItemPosition

        if (projectManagerIndex == -1 || nameProject.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val projectManagerId = userList[projectManagerIndex].iduser

        val projectRequest = ProjectRequest(
            nameproject = nameProject,
            startdatep = startDate,
            enddatep = endDate,
            idstate = 1,
            iduser = projectManagerId,
            completionstatus = false,
            performancereview = null,
            obs = null
        )

        RetrofitClient.instance.createProject(projectRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateProjectActivity, "Project created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Log de resposta não bem-sucedida
                    Log.e("CreateProjectActivity", "Failed to create project: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@CreateProjectActivity, "Failed to create project", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Log de falha na requisição
                Log.e("CreateProjectActivity", "Error: ${t.message}", t)
                Toast.makeText(this@CreateProjectActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun convertToISO8601(date: String): String {
        val parts = date.split("/")
        return "${parts[2]}-${parts[1]}-${parts[0]}T00:00:00Z"
    }

}
