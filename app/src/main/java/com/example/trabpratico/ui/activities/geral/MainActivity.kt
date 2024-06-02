package com.example.trabpratico.ui.activities.geral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R
import com.example.trabpratico.ui.fragments.AdminFragment
import com.example.trabpratico.ui.fragments.GestorFragment
import com.example.trabpratico.ui.fragments.UtilizadorFragment

class MainActivity : AppCompatActivity() {

    private lateinit var userTypeTextView: TextView
    private lateinit var profileButton: ImageButton
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userTypeTextView = findViewById(R.id.userTypeTextView)
        profileButton = findViewById(R.id.profileButton)
        logoutButton = findViewById(R.id.logoutButton)

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            // Simular o logout limpando as preferências compartilhadas ou redirecionando para a tela de login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val idType = getUserType()

        when (idType) {
            1 -> showAdminFragment()
            2 -> showManagerFragment()
            else -> showNormalUserFragment()
        }

        userTypeTextView.text = when (idType) {
            1 -> "Admin"
            2 -> "Manager"
            else -> "Normal User"
        }
    }

    private fun getUserType(): Int {
        return intent.getIntExtra("idType", -1)
    }

    private fun showAdminFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AdminFragment())
            .commit()
    }

    private fun showManagerFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GestorFragment())
            .commit()
    }

    private fun showNormalUserFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UtilizadorFragment())
            .commit()
    }
}
