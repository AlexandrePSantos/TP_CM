package com.example.trabpratico.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trabpratico.R
import com.example.trabpratico.databinding.FragmentProjectListBinding
import com.example.trabpratico.network.ProjectResponse

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectListFragment : Fragment() {

    private var _binding: FragmentProjectListBinding? = null
    private val binding get() = _binding!!
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectAdapter = ProjectAdapter { project -> onProjectClicked(project) }
        binding.recyclerViewProjects.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProjects.adapter = projectAdapter

        binding.buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fetchProjects()
    }

    private fun fetchProjects() {
        RetrofitClient.instance.getAllProjects().enqueue(object : Callback<List<ProjectResponse>> {
            override fun onResponse(call: Call<List<ProjectResponse>>, response: Response<List<ProjectResponse>>) {
                if (response.isSuccessful) {
                    val projects = response.body() ?: listOf()
                    projectAdapter.submitList(projects)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch projects", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ProjectResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onProjectClicked(project: ProjectResponse) {
        val intent = Intent(requireContext(), EditProjectActivity::class.java).apply {
            putExtra("PROJECT_ID", project.idproject)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
