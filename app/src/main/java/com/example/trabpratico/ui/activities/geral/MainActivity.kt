package com.example.trabpratico.ui.activities.geral

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        // Obtenha o tipo de usuário do Intent
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
                    // Tipo de usuário desconhecido
                    userTypeTextView.text = "Unknown User Type"
                }
            }
        } else {
            // Caso idtype não tenha sido passado corretamente
            userTypeTextView.text = "User Type Not Found"
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_admin -> {
                // Exibir o fragmento de administração
                showAdminFragment()
                true
            }
            R.id.menu_gestor -> {
                // Exibir o fragmento do gestor
                showManagerFragment()
                true
            }
            R.id.menu_utilizador -> {
                // Exibir o fragmento do utilizador
                showNormalUserFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAdminFragment() {
        replaceFragment(AdminFragment())
    }

    private fun showManagerFragment() {
        replaceFragment(GestorFragment())
    }

    private fun showNormalUserFragment() {
        replaceFragment(UtilizadorFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
