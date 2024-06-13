package com.example.trabpratico.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.databinding.ItemProjectBinding
import com.example.trabpratico.network.ProjectResponse

class ProjectAdapter(private val onClick: (ProjectResponse) -> Unit) : ListAdapter<ProjectResponse, ProjectAdapter.ProjectViewHolder>(ProjectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = getItem(position)
        holder.bind(project, onClick)
    }

    class ProjectViewHolder(private val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: ProjectResponse, onClick: (ProjectResponse) -> Unit) {
            binding.textViewProjectName.text = project.nameproject
            binding.textViewStartDate.text = project.startdatep
            binding.textViewEndDate.text = project.enddatep
            binding.root.setOnClickListener { onClick(project) }
        }
    }

    class ProjectDiffCallback : DiffUtil.ItemCallback<ProjectResponse>() {
        override fun areItemsTheSame(oldItem: ProjectResponse, newItem: ProjectResponse): Boolean {
            return oldItem.idproject == newItem.idproject
        }

        override fun areContentsTheSame(oldItem: ProjectResponse, newItem: ProjectResponse): Boolean {
            return oldItem == newItem
        }
    }
}
