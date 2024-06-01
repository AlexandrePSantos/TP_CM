package com.example.trabpratico.ui.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R
import com.example.trabpratico.ui.fragments.AdminFragment
import com.example.trabpratico.ui.fragments.GestorFragment
import com.example.trabpratico.ui.fragments.UtilizadorFragment

class MainActivity : AppCompatActivity() {

    private lateinit var userTypeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userTypeTextView = findViewById(R.id.userTypeTextView)

        // Aqui você obtém o tipo de usuário (idType) que foi retornado após o login
        val idType = getUserType()

        // Dependendo do tipo de usuário, define o fragmento correspondente
        when (idType) {
            1 -> showAdminFragment()
            2 -> showManagerFragment()
            else -> showNormalUserFragment()
        }

        // Configurar o texto do TextView com o tipo de usuário em negrito
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
        // Aqui você pode carregar e exibir o fragmento do Admin
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AdminFragment())
            .commit()
    }

    private fun showManagerFragment() {
        // Aqui você pode carregar e exibir o fragmento do Gestor
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GestorFragment())
            .commit()
    }

    private fun showNormalUserFragment() {
        // Aqui você pode carregar e exibir o fragmento do Utilizador Normal
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UtilizadorFragment())
            .commit()
    }
}
