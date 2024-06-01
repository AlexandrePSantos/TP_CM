package com.example.trabpratico.ui

import com.example.trabpratico.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.network.LoginRequest
import com.example.trabpratico.network.RetrofitClient
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
//            val email = etEmail.text.toString()
//            val password = etPassword.text.toString()
            val email = "test@example.com"
            val password = "password123"
            // Validação de campos e chamada de API para login
            val request = LoginRequest(email, password)

            // Imprima o corpo da solicitação para depuração
            Log.d("Request Body", "Request body: $request")

            // Envie a solicitação de login para o servidor usando Retrofit
            RetrofitClient.instance.login(request)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // If the response is successful, show the success popup
                            showSuccessPopup()
                        } else {
                            // If there's an error in the response, show the error popup
                            showErrorPopup()
                            // Log the error code for debugging
                            Log.e("API Error", "Failed to login: ${response.code()}")
                        }

                        // Parse the response body as JSON and log it
                        val responseBody = response.body()?.toString()
                        if (responseBody != null && responseBody.isNotEmpty()) {
                            Log.d("API Response", "Response body: $responseBody")
                        } else {
                            Log.d("API Response", "Empty response body")
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
//        builder.setPositiveButton("OK") { _, _ ->
//            navigateToIntroActivity()
//        }
        val dialog = builder.create()
        dialog.show()
    }

//    private fun navigateToIntroActivity() {
//        val intent = Intent(this@LoginActivity, IntroActivity::class.java)
//        startActivity(intent)
//        finish()
//    }

    private fun showErrorPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Login Failed")
        builder.setMessage("There was an error during login.")
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }
}