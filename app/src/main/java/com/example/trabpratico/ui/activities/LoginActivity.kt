package com.example.trabpratico.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R
import com.example.trabpratico.network.LoginRequest
import com.example.trabpratico.network.LoginResponse
import com.example.trabpratico.network.UserDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var userId: Int? = null // Adicione uma variável para armazenar o ID do usuário

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val request = LoginRequest(email, password)

            RetrofitClient.instance.login(request)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            val token = loginResponse?.token

                            if (!token.isNullOrEmpty()) {
                                Log.d("LoginActivity", "Token extracted: $token")

                                // Armazene o token no RetrofitClient
                                RetrofitClient.setAuthToken(token)

                                // Armazene o ID do usuário
                                userId = loginResponse.idUser

                                // Se o login for bem-sucedido, obtenha o tipo de usuário usando o token e o idUser
                                getUserType()
                            } else {
                                Log.e("LoginActivity", "Token is empty or null")
                                showErrorPopup()
                            }
                        } else {
                            // Se houver um erro na resposta, mostrar o popup de erro
                            showErrorPopup()
                            // Registre o código de erro para depuração
                            Log.e("API Error", "Failed to login: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // Se houver uma falha na chamada, mostrar o popup de erro
                        showErrorPopup()
                        // Registre a exceção para depuração
                        Log.e("API Error", "Login call failed", t)
                    }
                })
        }
    }

    private fun getUserType() {
        // Obtenha o tipo de usuário após o login bem-sucedido
        userId?.let {
            RetrofitClient.instance.getUserById(it).enqueue(object : Callback<UserDetailsResponse> {
                override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                    if (response.isSuccessful) {
                        val idType = response.body()?.idType
                        if (idType != null) {
                            // Se a resposta for bem-sucedida e o idType for obtido com sucesso,
                            // mostrar o popup de sucesso e redirecionar para a MainActivity
                            showSuccessPopup()
                            Log.d("User Type", "User type: $idType")
                            redirectToMain(idType)
                        } else {
                            // Se não for possível obter o idType, mostrar o popup de erro
                            showErrorPopup()
                        }
                    } else {
                        // Se houver um erro na resposta, mostrar o popup de erro
                        showErrorPopup()
                        // Registre o código de erro para depuração
                        Log.e("API Error", "Failed to get user details: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                    // Se houver uma falha na chamada, mostrar o popup de erro
                    showErrorPopup()
                    // Registre a exceção para depuração
                    Log.e("API Error", "Failed to get user details", t)
                }
            })
        } ?: run {
            Log.e("LoginActivity", "User ID is null")
            showErrorPopup()
        }
    }

    private fun showSuccessPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Login Successful")
        builder.setMessage("You have successfully logged in.")
        val dialog = builder.create()
        dialog.show()
    }

    private fun showErrorPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Login Failed")
        builder.setMessage("There was an error during login.")
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun redirectToMain(idType: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("idType", idType)
        startActivity(intent)
        finish()
    }
}
