package com.example.trabpratico.ui.activities.geral

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.trabpratico.R
import com.example.trabpratico.network.UserDetailsResponse
import com.example.trabpratico.network.UserUpdate
import com.example.trabpratico.room.db.AppDatabase
import com.example.trabpratico.room.entities.user
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class ProfileActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmButton: Button
    private lateinit var logoutButton: Button
    private lateinit var changePhotoButton: Button
    private lateinit var syncButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        usernameEditText = findViewById(R.id.usernameEditText)
        nameEditText = findViewById(R.id.nameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmButton = findViewById(R.id.confirmButton)
        logoutButton = findViewById(R.id.logoutButton)
        changePhotoButton = findViewById(R.id.changePhotoButton)
        syncButton = findViewById(R.id.syncButton)

        confirmButton.setOnClickListener {
            updateProfile()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        syncButton.setOnClickListener {
            syncData()
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

                            if (isOnline(applicationContext)) {
                            apiService.updateUser(userId, userUpdate).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        showSuccessPopup()

                                        // Store the updated user in the local database
                                        val db = Room.databaseBuilder(
                                            applicationContext,
                                            AppDatabase::class.java, "ProjectManagerDB"
                                        ).build()
                                        val updatedUser = user(
                                            iduser = userId,
                                            username = username,
                                            name = name,
                                            password = password,
                                            email = email,
                                            idtype = idtype,
                                            last_login = null,
                                            photo = null
                                        )
                                        CoroutineScope(Dispatchers.IO).launch {
                                            db.appDao().insertUser(updatedUser)
                                        }
                                    } else {
                                        showErrorPopup("Falha na atualização do perfil. Por favor, tente novamente.")
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    showErrorPopup("Erro ao atualizar perfil. Verifique sua conexão com a internet e tente novamente.")
                                }
                            })} else {
                                showErrorPopup("Erro ao atualizar perfil. Verifique sua conexão com a internet e tente novamente.")
                            }
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

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    private fun syncData() {
        // Get a reference to the Room database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "ProjectManagerDB"
        ).build()

        // Use a coroutine to perform database operations off the main thread
        CoroutineScope(Dispatchers.IO).launch {
            // Get the user from the local database
            val userId = RetrofitClient.getUserId()
            val user = userId?.let { db.appDao().getUserById(it) }

            // Make an API call to update the user on the server
            val apiService = RetrofitClient.instance
            val userUpdate = user?.let {
                user.username?.let { it1 ->
                    user.name?.let { it2 ->
                        UserUpdate(
                            username = it1,
                            name = it2,
                            password = user.password,
                            email = user.email,
                            idtype = it.idtype
                        )
                    }
                }
            }
            if (userId != null) {
                if (userUpdate != null) {
                    apiService.updateUser(userId, userUpdate).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                // If the API call is successful, remove the user from the local database
                                CoroutineScope(Dispatchers.IO).launch {
                                    db.appDao().deleteUser(user)
                                }
                            } else {
                                // Handle error
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            // Handle error
                        }
                    })
                }
            }
            // If the API call is successful, remove the entity from the local database
            // TODO: Check the result of the API call and remove the entity from the local database if successful
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
