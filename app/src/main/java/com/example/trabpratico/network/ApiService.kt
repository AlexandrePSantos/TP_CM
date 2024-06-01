package com.example.trabpratico.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RegisterRequest(val name: String, val username: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)

interface ApiService {
    @POST("auth/signup")
    fun register(@Body request: RegisterRequest): Call<Void>

    @POST("auth/signin")
    fun login(@Body request: LoginRequest): Call<Void>

    @GET("user")
    fun getUsers(): Call<Void>
}