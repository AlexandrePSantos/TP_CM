package com.example.trabpratico.ui.Gestor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ProjectRequest
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.network.TaskRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var textProjectName: TextView
    private lateinit var textStartDate: TextView
    private lateinit var textEndDate: TextView
    private lateinit var textState: TextView
    private lateinit var buttonMarkCompleted: Button
    private lateinit var buttonNavigate: Button

    private var projectId: Int = -1
    private var project: ProjectResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        apiService = RetrofitClient.instance

        textProjectName = findViewById(R.id.textProjectName)
        textStartDate = findViewById(R.id.textStartDate)
        textEndDate = findViewById(R.id.textEndDate)
        textState = findViewById(R.id.textState)
        buttonMarkCompleted = findViewById(R.id.buttonMarkCompleted)
        buttonNavigate = findViewById(R.id.buttonNavigate)

        projectId = intent.getIntExtra("PROJECT_ID", -1)

        buttonMarkCompleted.setOnClickListener {
            markProjectAsCompleted()
        }

        buttonNavigate.setOnClickListener {
            navigateToNewPage()
        }

        loadProjectDetails()
    }

    private fun loadProjectDetails() {
        apiService.getProjectById(projectId).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful) {
                    project = response.body()
                    project?.let {
                        textProjectName.text = it.nameproject
                        textStartDate.text = it.startdatep?.let { it1 -> formatDateForDisplay(it1) }
                        textEndDate.text = it.enddatep?.let { it1 -> formatDateForDisplay(it1) }
                        textState.text = getStateText(it.idstate)
                    }
                    if (project?.completionstatus == true) {
                        buttonMarkCompleted.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@ProjectActivity, "Failed to load project details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) {
                Toast.makeText(this@ProjectActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getStateText(idstate: Int): String {
        return when (idstate) {
            1 -> "Created"
            2 -> "In Development"
            3 -> "Completed"
            else -> "Unknown State"
        }
    }

    private fun markProjectAsCompleted() {
        project?.let {
            val updatedProject = ProjectRequest(
                nameproject = it.nameproject,
                startdatep = it.startdatep,
                enddatep = it.enddatep,
                idstate = 3,
                iduser = it.iduser,
                completionstatus = true,
                performancereview = null,
                obs = null
            )

            apiService.updateProject(it.idproject, updatedProject).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        textState.text = getStateText(3)
                        buttonMarkCompleted.visibility = View.GONE
                        Toast.makeText(this@ProjectActivity, "Project marked as completed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProjectActivity, "Failed to update project", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ProjectActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun formatDateForDisplay(date: String): String {
        val parts = date.split("T")[0].split("-")
        return "${parts[2]}/${parts[1]}/${parts[0]}"
    }

    private fun navigateToNewPage() {
        val intent = Intent(this, ProjectTasksListActivity::class.java)
        intent.putExtra("PROJECT_ID", projectId)
        startActivity(intent)
    }
}