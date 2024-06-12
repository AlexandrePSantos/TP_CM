package com.example.trabpratico.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ProjectResponse

class ProjectAdapter(
    private val onEditClick: (ProjectResponse) -> Unit,
    private val onRemoveClick: (Int) -> Unit
) : ListAdapter<ProjectResponse, ProjectAdapter.ProjectViewHolder>(ProjectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = getItem(position)
        holder.bind(project, onEditClick, onRemoveClick)
    }

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewProjectName)
        private val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        private val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)

        fun bind(project: ProjectResponse, onEditClick: (ProjectResponse) -> Unit, onRemoveClick: (Int) -> Unit) {
            textViewName.text = project.nameproject
            buttonEdit.setOnClickListener { onEditClick(project) }
            buttonRemove.setOnClickListener { onRemoveClick(project.idproject) }
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
