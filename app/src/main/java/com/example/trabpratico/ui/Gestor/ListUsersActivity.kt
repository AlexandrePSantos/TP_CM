package com.example.trabpratico.ui.Gestor

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.R
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.PerformanceRequest
import com.example.trabpratico.network.UserDetailsResponse
import com.example.trabpratico.network.UserTaskResponse
import com.example.trabpratico.ui.UserAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListUsersActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var userAdapter: UserAdapter
    private var idTask: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users)

        idTask = intent.getIntExtra("TASK_ID", 0)
        Log.d("ListUserActivity", "Task ID: $idTask")

        apiService = RetrofitClient.instance

        val recyclerViewUsers = findViewById<RecyclerView>(R.id.recyclerViewUsers)
        recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(UserAdapter.UserClickListener { user ->
            showPerformanceReviewDialog(user)
        })
        recyclerViewUsers.adapter = userAdapter

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() 
        }

        fetchUsers()
    }

    private fun fetchUsers() {
        apiService.getUserTasks().enqueue(object : Callback<List<UserTaskResponse>> {
            override fun onResponse(call: Call<List<UserTaskResponse>>, response: Response<List<UserTaskResponse>>) {
                if (response.isSuccessful) {
                    val userTaskResponses = response.body()?.filter { it.idtask == idTask }
                    val userIds = userTaskResponses?.map { it.iduser } ?: emptyList()

                    apiService.getAllUsers().enqueue(object : Callback<List<UserDetailsResponse>> {
                        override fun onResponse(call: Call<List<UserDetailsResponse>>, response: Response<List<UserDetailsResponse>>) {
                            if (response.isSuccessful) {
                                val users = response.body()?.filter { it.iduser in userIds }
                                userAdapter.submitList(users ?: emptyList())
                            } else {
                                Toast.makeText(this@ListUsersActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<UserDetailsResponse>>, t: Throwable) {
                            Toast.makeText(this@ListUsersActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this@ListUsersActivity, "Failed to load user tasks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserTaskResponse>>, t: Throwable) {
                Toast.makeText(this@ListUsersActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showPerformanceReviewDialog(user: UserDetailsResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_performance_review, null)
        val inputReview = dialogView.findViewById<EditText>(R.id.editTextReview)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val review = inputReview.text.toString()
                val stars = ratingBar.rating.toInt()

                val performanceRequest = PerformanceRequest(
                    iduser = user.iduser,
                    idtask = idTask,
                    stars = stars,
                    review = review
                )

                apiService.createPerformance(performanceRequest).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ListUsersActivity, "Review submitted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ListUsersActivity, "Failed to submit review", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@ListUsersActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}