package com.example.trabpratico.ui.Utilizador

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.adapter.ObsAdapter
import com.example.trabpratico.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var textTaskName: TextView
    private lateinit var textStartDate: TextView
    private lateinit var textEndDate: TextView
    private lateinit var textState: TextView
    private lateinit var editTimeSpent: EditText
    private lateinit var editLocal: EditText
    private lateinit var seekBarCompletionRate: SeekBar
    private lateinit var textCompletionRate: TextView
    private lateinit var recyclerViewObservations: RecyclerView
    private lateinit var buttonAddObservation: Button
    private lateinit var buttonConfirm: Button
    private lateinit var obsAdapter: ObsAdapter

    private var taskId: Int = -1
    private var task: TaskResponse? = null
    private var projectId: Int = -1
    private var idState: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        apiService = RetrofitClient.instance

        textTaskName = findViewById(R.id.textTaskName)
        textStartDate = findViewById(R.id.textStartDate)
        textEndDate = findViewById(R.id.textEndDate)
        textState = findViewById(R.id.textState)
        editTimeSpent = findViewById(R.id.editTimeSpent)
        editLocal = findViewById(R.id.editLocal)
        seekBarCompletionRate = findViewById(R.id.seekBarCompletionRate)
        textCompletionRate = findViewById(R.id.textCompletionRate)
        recyclerViewObservations = findViewById(R.id.recyclerViewObservations)
        buttonAddObservation = findViewById(R.id.buttonAddObservation)
        buttonConfirm = findViewById(R.id.buttonConfirm)

        taskId = intent.getIntExtra("TASK_ID", -1)

        seekBarCompletionRate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textCompletionRate.text = "$progress%"
                idState = when {
                    progress == 0 -> 1
                    progress in 1..99 -> 2
                    progress == 100 -> 3
                    else -> idState
                }
                loadState(idState) // Atualiza o estado com base no progresso
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        recyclerViewObservations.layoutManager = LinearLayoutManager(this)
        obsAdapter = ObsAdapter()
        recyclerViewObservations.adapter = obsAdapter

        buttonAddObservation.setOnClickListener {
            showAddObservationDialog()
        }

        buttonConfirm.setOnClickListener {
            updateTaskDetails()
        }

        loadTaskDetails()
    }

    private fun loadTaskDetails() {
        apiService.getTaskById(taskId).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful) {
                    task = response.body()
                    task?.let {
                        textTaskName.text = it.nametask
                        textStartDate.text = it.startdatet
                        textEndDate.text = it.enddatet
                        editTimeSpent.setText(it.timespend?.toString())
                        editLocal.setText(it.local)
                        seekBarCompletionRate.progress = it.completionrate?.toInt() ?: 0
                        textCompletionRate.text = "${it.completionrate?.toInt() ?: 0}%"
                        idState = it.idstate
                        loadProjectName(it.idproject)
                        loadObservations(it.idtask)
                        loadState(it.idstate) // Adiciona chamada para carregar o estado
                        checkState(it.idstate)
                    }
                } else {
                    Toast.makeText(this@TaskActivity, "Failed to load task details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadProjectName(projectId: Int) {
        apiService.getProjectById(projectId).enqueue(object : Callback<ProjectResponse> {
            override fun onResponse(call: Call<ProjectResponse>, response: Response<ProjectResponse>) {
                if (response.isSuccessful) {
                    val project = response.body()
                    findViewById<TextView>(R.id.textProjectName).text = project?.nameproject ?: "Unknown Project"
                } else {
                    Toast.makeText(this@TaskActivity, "Failed to load project name", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProjectResponse>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadObservations(taskId: Int) {
        apiService.getAllObs().enqueue(object : Callback<List<ObsResponse>> {
            override fun onResponse(call: Call<List<ObsResponse>>, response: Response<List<ObsResponse>>) {
                if (response.isSuccessful) {
                    Log.d("Observations", "Observations loaded no response: ${response.body()}")
                    val observations = response.body()?.filter { it.idtask == taskId }
                    if (observations != null) {
                        obsAdapter.submitList(observations)
                        Log.d("IDTask", "Task ID: $taskId")
                        Log.d("Observations", "Observations loaded: $observations")
                    }
                } else {
                    Toast.makeText(this@TaskActivity, "Failed to load observations", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ObsResponse>>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadState(idstate: Int) {
        apiService.getStateById(idstate).enqueue(object : Callback<StateResponse> {
            override fun onResponse(call: Call<StateResponse>, response: Response<StateResponse>) {
                if (response.isSuccessful) {
                    val state = response.body()
                    textState.text = state?.state ?: "Unknown State"
                } else {
                    Toast.makeText(this@TaskActivity, "Failed to load state", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StateResponse>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkState(idstate: Int) {
        if (idstate == 3) {
            // Disable editing
            editTimeSpent.isEnabled = false
            editLocal.isEnabled = false
            seekBarCompletionRate.isEnabled = false
            buttonConfirm.visibility = View.GONE
            buttonAddObservation.visibility = View.GONE
        }
    }

    private fun updateTaskDetails() {
        val task = task ?: return

        val updatedTask = TaskRequest(
            nametask = task.nametask,
            startdatet = task.startdatet,
            enddatet = task.enddatet,
            idproject = task.idproject,
            idstate = idState, // Atualiza o estado com base no progresso do slider
            timespend = editTimeSpent.text.toString().toIntOrNull(),
            local = editLocal.text.toString(),
            completionrate = seekBarCompletionRate.progress.toDouble()
        )

        apiService.updateTask(task.idtask, updatedTask).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@TaskActivity, "Task updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@TaskActivity, "Failed to update task", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddObservationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_observation, null)
        val inputObservation = dialogView.findViewById<EditText>(R.id.editTextObservation)

        AlertDialog.Builder(this)
            .setTitle("Add Observation")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val observationContent = inputObservation.text.toString()
                addObservation(observationContent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addObservation(content: String) {
        val task = task ?: return

        val newObservation = ObsRequest(
            idtask = task.idtask,
            content = content
        )

        apiService.createObs(newObservation).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadObservations(task.idtask)
                    Toast.makeText(this@TaskActivity, "Observation added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@TaskActivity, "Failed to add observation", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
