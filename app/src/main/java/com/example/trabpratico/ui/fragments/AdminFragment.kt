package com.example.trabpratico.ui.fragments


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ProjectRequest
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.network.RegisterRequest
import com.example.trabpratico.network.RegisterRequestAdmin
import com.example.trabpratico.ui.UsersActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        apiService = RetrofitClient.instance

        val buttonAddProject = view.findViewById<Button>(R.id.buttonAddProject1)
        buttonAddProject.setOnClickListener {
            showAddProjectDialog()
        }
        val buttonAddUser = view.findViewById<Button>(R.id.buttonAddUser)
        buttonAddUser.setOnClickListener {
            showAddUserDialog()
        }
        val buttonListUsers = view.findViewById<Button>(R.id.buttonListUsers)
        buttonListUsers.setOnClickListener {
            val intent = Intent(activity, UsersActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    // --PROJECTS--
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

    private fun showEditProjectDialog(project: ProjectResponse) {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_edit_project, null)
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
                    fetchProjects()
                } else {
                    showToast("Failed to add project")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun editProject(
        project: ProjectResponse,
        name: String,
        startDate: String,
        endDate: String
    ) {
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

        apiService.updateProject(project.idproject, projectRequest)
            .enqueue(object : Callback<Void> {
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

    private fun removeProject(project: ProjectResponse) {
        apiService.deleteProject(project.idproject).enqueue(object : Callback<Void> {
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
        apiService.getAllProjects().enqueue(object : Callback<List<ProjectResponse>> {
            override fun onResponse(
                call: Call<List<ProjectResponse>>,
                response: Response<List<ProjectResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        projectAdapter.submitList(it)
                    }
                } else {
                    showToast("Failed to load projects")
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

    // --USERS--
    private fun showAddUserDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_add_user, null)
        val inputEmail = dialogView.findViewById<EditText>(R.id.editTextEmail)
        val inputPassword = dialogView.findViewById<EditText>(R.id.editTextPassword).apply {
            setText("123456") // Set default password
        }
        val spinnerIdType = dialogView.findViewById<Spinner>(R.id.spinnerIdType)
        val inputUsername = dialogView.findViewById<EditText>(R.id.editTextUsername)
        val inputName = dialogView.findViewById<EditText>(R.id.editTextName)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add User")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()
                val idType =
                    spinnerIdType.selectedItemPosition + 2 // Add 2 because your idType starts from 2
                val username = inputUsername.text.toString()
                val name = inputName.text.toString()
                addUser(name, username, email, password, idType)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }
    private fun addUser(name: String, username: String, email: String, password: String, idtype: Int) {
        val userRequest = RegisterRequestAdmin(name, username, email, password, idtype)

        apiService.createUser(userRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("User added successfully")
                } else {
                    showToast("Failed to add user")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }


}
