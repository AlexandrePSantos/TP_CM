package com.example.trabpratico.ui.Admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R
import com.example.trabpratico.ui.fragments.EditUserFragment

class EditUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        val userId = intent.getIntExtra("USER_ID", -1)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditUserFragment.newInstance(userId))
                .commitNow()
        }
    }
}