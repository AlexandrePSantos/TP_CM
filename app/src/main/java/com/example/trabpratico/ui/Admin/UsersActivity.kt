package com.example.trabpratico.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trabpratico.R
import com.example.trabpratico.databinding.ActivityUsersBinding
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.UserDetailsResponse
import com.example.trabpratico.ui.Admin.EditUserActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var apiService: ApiService
    private val userAdapter = UserAdapter(UserAdapter.UserClickListener { user ->
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("USER_ID", user.iduser)
        startActivity(intent)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitClient.instance

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@UsersActivity)
            adapter = userAdapter
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }

        fetchUsers()
    }

    private fun fetchUsers() {
        apiService.getAllUsers().enqueue(object : Callback<List<UserDetailsResponse>> {
            override fun onResponse(
                call: Call<List<UserDetailsResponse>>,
                response: Response<List<UserDetailsResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        userAdapter.submitList(it)
                    }
                    // Log the raw API response
                    Log.d("UsersActivity", "API response: ${response.body()}")

                } else {
                    Toast.makeText(this@UsersActivity, "Failed to fetch users", Toast.LENGTH_SHORT).show()
                    Log.e("UsersActivity", "Failed to fetch users: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
                Toast.makeText(this@UsersActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
