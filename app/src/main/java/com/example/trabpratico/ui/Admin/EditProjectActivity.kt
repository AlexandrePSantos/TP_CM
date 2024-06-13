package com.example.trabpratico.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R
import com.example.trabpratico.ui.fragments.EditProjectFragment

class EditProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_project)

        val projectId = intent.getIntExtra("PROJECT_ID", -1)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProjectFragment.newInstance(projectId))
                .commitNow()
        }
    }
}
