package com.example.trabpratico.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ProjectResponse

class ProjectAdapter : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private var projects: List<ProjectResponse> = emptyList()
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(project: ProjectResponse)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(project: ProjectResponse): Boolean
    }

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewStartDate: TextView = itemView.findViewById(R.id.textViewStartDate)
        private val textViewEndDate: TextView = itemView.findViewById(R.id.textViewEndDate)

        fun bind(project: ProjectResponse) {
            textViewName.text = project.nameproject
            textViewStartDate.text = project.startdatep
            textViewEndDate.text = project.enddatep

            itemView.setOnClickListener {
                onItemClickListener?.onItemClick(project)
            }

            itemView.setOnLongClickListener {
                onItemLongClickListener?.onItemLongClick(project) ?: false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    fun submitList(projects: List<ProjectResponse>) {
        this.projects = projects
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }
}
