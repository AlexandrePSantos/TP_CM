package com.example.trabpratico.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trabpratico.databinding.FragmentEditUserBinding
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.UserDetailsResponse
import com.example.trabpratico.network.UserUpdate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditUserFragment : Fragment() {

    private lateinit var binding: FragmentEditUserBinding
    private lateinit var apiService: ApiService
    private var userId: Int = -1

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Int) = EditUserFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_USER_ID, userId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditUserBinding.inflate(inflater, container, false)
        apiService = RetrofitClient.instance
        userId = arguments?.getInt(ARG_USER_ID) ?: -1

        loadUserDetails(userId)

        binding.buttonUpdateUser.setOnClickListener {
            updateUserDetails()
        }

        binding.buttonDeleteUser.setOnClickListener {
            deleteUser(userId)
        }

        return binding.root
    }

    private fun loadUserDetails(userId: Int) {
        apiService.getUserById(userId).enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        binding.user = user
                    }
                } else {
                    Toast.makeText(context, "Failed to load user details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserDetails() {
        val user = binding.user ?: return
        val userUpdate = UserUpdate(
            name = user.name,
            username = user.username,
            email = user.email,
            password = user.password,
            idtype = user.idtype
        )
        apiService.updateUser(userId, userUpdate).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteUser(userId: Int) {
        apiService.deleteUser(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                    activity?.finish()
                } else {
                    Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}