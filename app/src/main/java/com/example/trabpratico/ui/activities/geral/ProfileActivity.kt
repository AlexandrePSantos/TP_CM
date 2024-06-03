package com.example.trabpratico.ui.activities.geral

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R
import com.example.trabpratico.network.UserDetailsResponse
import com.example.trabpratico.network.UserUpdate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmButton: Button
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usernameEditText = findViewById(R.id.usernameEditText)
        nameEditText = findViewById(R.id.nameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmButton = findViewById(R.id.confirmButton)
        logoutButton = findViewById(R.id.logoutButton)

        confirmButton.setOnClickListener {
            updateProfile()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        val userId = RetrofitClient.getUserId()

        if (userId != null) {
            val apiService = RetrofitClient.instance
            apiService.getUserById(userId).enqueue(object : Callback<UserDetailsResponse> {
                override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                    if (response.isSuccessful) {
                        val userDetailsResponse = response.body()
                        if (userDetailsResponse != null) {
                            // Preencher os campos EditText com os dados do usuário
                            usernameEditText.setText(userDetailsResponse.username)
                            nameEditText.setText(userDetailsResponse.name)
                            // Preencher o campo de senha com um valor placeholder
                            passwordEditText.hint = "••••••••"
                            passwordEditText.tag = userDetailsResponse.password
                        } else {
                            showErrorPopup("Erro ao obter detalhes do usuário. Por favor, tente novamente.")
                        }
                    } else {
                        showErrorPopup("Erro ao obter detalhes do usuário. Por favor, tente novamente.")
                    }
                }

                override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                    showErrorPopup("Erro ao obter detalhes do usuário. Verifique sua conexão com a internet e tente novamente.")
                }
            })
        } else {
            showErrorPopup("Erro ao obter ID do usuário. Por favor, tente novamente.")
        }
    }

    private fun updateProfile() {
        val username = usernameEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val password = if (passwordEditText.text.toString() == "••••••••") passwordEditText.tag.toString() else passwordEditText.text.toString().trim()

        if (username.isEmpty() || name.isEmpty() || password.isEmpty()) {
            showErrorPopup("Todos os campos são obrigatórios.")
            return
        }

        val userId = RetrofitClient.getUserId()

        if (userId != null) {
            val apiService = RetrofitClient.instance
            apiService.getUserById(userId).enqueue(object : Callback<UserDetailsResponse> {
                override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                    if (response.isSuccessful) {
                        val userDetailsResponse = response.body()
                        if (userDetailsResponse != null) {
                            val email = userDetailsResponse.email
                            val idtype = userDetailsResponse.idtype

                            val userUpdate = UserUpdate(
                                username = username,
                                name = name,
                                password = password,
                                email = email,
                                idtype = idtype
                            )

                            apiService.updateUser(userId, userUpdate).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        showSuccessPopup()
                                    } else {
                                        showErrorPopup("Falha na atualização do perfil. Por favor, tente novamente.")
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    showErrorPopup("Erro ao atualizar perfil. Verifique sua conexão com a internet e tente novamente.")
                                }
                            })
                        } else {
                            showErrorPopup("Erro ao obter detalhes do usuário. Por favor, tente novamente.")
                        }
                    } else {
                        showErrorPopup("Erro ao obter detalhes do usuário. Por favor, tente novamente.")
                    }
                }

                override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                    showErrorPopup("Erro ao obter detalhes do usuário. Verifique sua conexão com a internet e tente novamente.")
                }
            })
        } else {
            showErrorPopup("Erro ao obter ID do usuário. Por favor, tente novamente.")
        }
    }


    private fun showSuccessPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sucesso")
        builder.setMessage("Perfil atualizado com sucesso.")
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun showErrorPopup(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erro")
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
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
