package com.example.trabpratico.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.trabpratico.R
import com.example.trabpratico.network.RegisterRequest
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // Validação de campos e chamada de API para registrar o usuário
            val request = RegisterRequest(name, username, email, password)
            RetrofitClient.instance.register(request)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // Se o registro for bem-sucedido, mostrar o popup de sucesso
                            showSuccessPopup()
                            // Redirecionar para a LoginActivity após o registro bem-sucedido
                            redirectToLogin()
                        } else {
                            // Se houver um erro na resposta, mostrar o popup de erro
                            showErrorPopup()
                            // Registre o código de erro para depuração
                            Log.e("API Error", "Failed to register: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Se houver uma falha na chamada, mostrar o popup de erro
                        showErrorPopup()
                        // Registre a exceção para depuração
                        Log.e("API Error", "Registration call failed", t)
                    }
                })
        }
    }

    private fun showSuccessPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registration Successful")
        builder.setMessage("You have successfully registered.")
        val dialog = builder.create()
        dialog.show()
    }

    private fun showErrorPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registration Failed")
        builder.setMessage("There was an error during registration.")
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
