package com.example.trabpratico.ui.activities.geral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        // Limpar dados do usuário (SharedPreferences, etc.)
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        // Redirecionar para IntroActivity
        val intent = Intent(this, IntroActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
