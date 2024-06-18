package com.example.trabpratico.ui.Gestor

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.adapter.TaskAdapter
import com.example.trabpratico.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProjectTasksListActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var pendingTasksAdapter: TaskAdapter
    private lateinit var finishedTasksAdapter: TaskAdapter
    private var projectId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_tasks_list)

        apiService = RetrofitClient.instance
        projectId = intent.getIntExtra("PROJECT_ID", -1)

        val recyclerViewPendingTasks = findViewById<RecyclerView>(R.id.recyclerViewPendingTasks)
        recyclerViewPendingTasks.layoutManager = LinearLayoutManager(this)
        pendingTasksAdapter = TaskAdapter()
        recyclerViewPendingTasks.adapter = pendingTasksAdapter
        recyclerViewPendingTasks.visibility = View.VISIBLE

        val recyclerViewFinishedTasks = findViewById<RecyclerView>(R.id.recyclerViewFinishedTasks)
        recyclerViewFinishedTasks.layoutManager = LinearLayoutManager(this)
        finishedTasksAdapter = TaskAdapter()
        recyclerViewFinishedTasks.adapter = finishedTasksAdapter
        recyclerViewFinishedTasks.visibility = View.VISIBLE

        val buttonAddTask = findViewById<Button>(R.id.buttonAddTask)
        buttonAddTask.setOnClickListener {
            showCreateTaskDialog()
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }

        fetchProjectTasks()
    }

    private fun fetchProjectTasks() {
        apiService.getAllTasks().enqueue(object : Callback<List<TaskResponse>> {
            override fun onResponse(call: Call<List<TaskResponse>>, response: Response<List<TaskResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { tasks ->
                        val projectTasks = tasks.filter { it.idproject == projectId }
                        val pendingTasks = projectTasks.filter { it.idstate == 1 || it.idstate == 2 }
                        val finishedTasks = projectTasks.filter { it.idstate == 3 }

                        pendingTasksAdapter.submitList(pendingTasks)
                        finishedTasksAdapter.submitList(finishedTasks)
                    }
                } else {
                    Toast.makeText(this@ProjectTasksListActivity, "Failed to load tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TaskResponse>>, t: Throwable) {
                Toast.makeText(this@ProjectTasksListActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showCreateTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_task, null)
        val editTaskName = dialogView.findViewById<EditText>(R.id.editTaskName)
        val textViewStartDate = dialogView.findViewById<TextView>(R.id.textViewStartDate)
        val textViewEndDate = dialogView.findViewById<TextView>(R.id.textViewEndDate)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Create Task")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        textViewStartDate.setOnClickListener {
            showDatePickerDialog { date ->
                textViewStartDate.text = date
            }
        }

        textViewEndDate.setOnClickListener {
            showDatePickerDialog { date ->
                textViewEndDate.text = date
            }
        }

        dialogView.findViewById<Button>(R.id.buttonCreateTask).setOnClickListener {
            val taskName = editTaskName.text.toString()
            val startDate = textViewStartDate.text.toString()
            val endDate = textViewEndDate.text.toString()

            if (taskName.isEmpty()) {
                editTaskName.error = "Task name is required"
                return@setOnClickListener
            }

            if (startDate.isEmpty()) {
                textViewStartDate.error = "Start date is required"
                return@setOnClickListener
            }

            if (endDate.isEmpty()) {
                textViewEndDate.error = "End date is required"
                return@setOnClickListener
            }

            createTask(taskName, startDate, endDate, dialog)
        }

        dialogView.findViewById<Button>(R.id.buttonCancelTask).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            onDateSet("${selectedYear}-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}")
        }, year, month, day).show()
    }

    private fun createTask(nameTask: String, startDate: String, endDate: String, dialog: AlertDialog) {
        val newTask = TaskRequest(
            nametask = nameTask,
            startdatet = "${startDate}T00:00:00Z",
            enddatet = "${endDate}T00:00:00Z",
            idproject = projectId,
            idstate = 1,
            timespend = 0,
            local = "",
            completionrate = 0.0
        )

        apiService.createTask(newTask).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProjectTasksListActivity, "Task created successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    fetchProjectTasks()
                } else {
                    Toast.makeText(this@ProjectTasksListActivity, "Failed to create task", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProjectTasksListActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
