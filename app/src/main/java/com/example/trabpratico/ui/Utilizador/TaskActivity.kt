package com.example.trabpratico.ui.Utilizador

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R

class TaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            // Use taskId to fetch and display task details
        } else {
            // Handle case where taskId is not available
        }
    }
}