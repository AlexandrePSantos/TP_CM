package com.example.trabpratico.ui

import com.example.trabpratico.R
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.network.LoginRequest
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

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
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        // Armazene o corpo da resposta em uma variável
                        val responseBodyString = response.body()?.toString()

                        if (response.isSuccessful) {
                            // Se a resposta for bem-sucedida, mostrar o popup de sucesso
                            showSuccessPopup()

                            // Chamar a função getUsers() após o login bem-sucedido
                            RetrofitClient.instance.getUsers().enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        val responseBody = response.body()?.toString()
                                        Log.d("API Response", "Response body: $responseBody")
                                    } else {
                                        Log.e("API Error", "Failed to login: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    // Se houver uma falha na chamada, trate-a aqui
                                }
                            })
                        } else {
                            // Se houver um erro na resposta, mostrar o popup de erro
                            showErrorPopup()
                            // Registre o código de erro para depuração
                            Log.e("API Error", "Failed to login: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Se houver uma falha na chamada, mostre o popup de erro
                        showErrorPopup()
                        // Registre a exceção para depuração
                        Log.e("API Error", "Login call failed", t)
                    }
                })

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
}