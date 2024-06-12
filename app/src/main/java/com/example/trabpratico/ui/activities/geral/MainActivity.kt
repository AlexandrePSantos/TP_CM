package com.example.trabpratico.ui.activities.geral

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.trabpratico.R
import com.example.trabpratico.ui.fragments.AdminFragment
import com.example.trabpratico.ui.fragments.GestorFragment
import com.example.trabpratico.ui.fragments.UtilizadorFragment

class MainActivity : AppCompatActivity() {

    private lateinit var userTypeTextView: TextView
    private lateinit var profileButton: ImageButton
    private lateinit var logoutButton: Button
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userTypeTextView = findViewById(R.id.userTypeTextView)
        profileButton = findViewById(R.id.profileButton)
        logoutButton = findViewById(R.id.logoutButton)

        userName = intent.getStringExtra("userName") ?: "User"

        userTypeTextView.text = "Welcome, $userName!"

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val idtype = intent.getIntExtra("idtype", -1)
        if (idtype != -1) {
            when (idtype) {
                1 -> {
                    loadFragment(AdminFragment())
                    userTypeTextView.text = "Admin"
                }
                2 -> {
                    loadFragment(GestorFragment())
                    userTypeTextView.text = "Gestor"
                }
                3 -> {
                    loadFragment(UtilizadorFragment())
                    userTypeTextView.text = "Utilizador"
                }
                else -> {
                    userTypeTextView.text = "Unknown User Type"
                }
            }
        } else {
            userTypeTextView.text = "User Type Not Found"
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
