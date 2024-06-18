package com.example.trabpratico.ui

import RetrofitClient
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trabpratico.R
import com.example.trabpratico.databinding.ActivityEditProjectBinding
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.ui.fragments.EditProjectFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProjectBinding
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.recyclerViewProjects
        recyclerView.layoutManager = LinearLayoutManager(this)
        projectAdapter = ProjectAdapter { project ->
            val fragment = EditProjectFragment.newInstance(project.idproject)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = projectAdapter

        fetchProjects()

        binding.buttonBack.setOnClickListener {
            finish() // This will close the current activity and return to the previous one
        }
    }

    private fun fetchProjects() {
        RetrofitClient.instance.getAllProjects().enqueue(object : Callback<List<ProjectResponse>> {
            override fun onResponse(
                call: Call<List<ProjectResponse>>,
                response: Response<List<ProjectResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { projects ->
                        Log.d("EditProjectActivity", "Projects loaded: ${projects.size}")
                        projectAdapter.submitList(projects)
                    } ?: run {
                        Log.e("EditProjectActivity", "No projects found in response body")
                    }
                } else {
                    Log.e("EditProjectActivity", "Failed to fetch projects: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ProjectResponse>>, t: Throwable) {
                Log.e("EditProjectActivity", "Error fetching projects: ${t.message}", t)
            }
        })
    }


}
