package com.example.trabpratico.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.TaskResponse
import com.example.trabpratico.ui.Utilizador.TaskActivity

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<TaskResponse> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun submitList(taskList: List<TaskResponse>) {
        tasks = taskList
        notifyDataSetChanged()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.taskName)
        private val taskStatus: TextView = itemView.findViewById(R.id.taskStatus)

        fun bind(task: TaskResponse) {
            taskName.text = task.nametask
            taskStatus.text = when (task.idstate) {
                3 -> "Completed"
                2 -> "In Progress"
                else -> "Created"
            }

            itemView.setOnClickListener{
                val context = itemView.context
                val intent = Intent(context, TaskActivity::class.java)
                intent.putExtra("TASK_ID", task.idtask)
                context.startActivity(intent)
            }
        }
    }
}
