package com.example.trabpratico.ui.Gestor

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class ListObsActivity: AppCompatActivity() {
    var idTask: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idTask = intent.getIntExtra("TASK_ID", 0)
        Log.d("ListObsActivity", "Task ID: $idTask")
    }
}