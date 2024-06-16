package com.example.trabpratico.ui.Gestor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R

class ProjectActivity : AppCompatActivity() {

    private var projectId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        projectId = intent.getIntExtra("PROJECT_ID", -1)
        // Load and display project details based on projectId
    }
}
