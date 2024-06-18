package com.example.trabpratico.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ProjectResponse

class ProjectAdapter(private val onClick: (ProjectResponse) -> Unit) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private var projectList: List<ProjectResponse> = listOf()

    fun submitList(projects: List<ProjectResponse>) {
        projectList = projects
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projectList[position]
        holder.bind(project)
        holder.itemView.setOnClickListener { onClick(project) }
    }

    override fun getItemCount(): Int = projectList.size

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val projectName: TextView = itemView.findViewById(R.id.textViewProjectName)

        fun bind(project: ProjectResponse) {
            projectName.text = project.nameproject
        }
    }
}
