package com.example.trabpratico.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class RegisterRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val idType: Int = 3
)

data class LoginRequest(val email: String, val password: String)
data class UserDetailsResponse(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val idType: Int
)

data class LoginResponse(val token: String, val idUser: Int)

interface ApiService {
    @POST("auth/signup")
    fun register(@Body request: RegisterRequest): Call<Void>

    @POST("auth/signin")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("user")
    fun getUsers(): Call<Void>

    @GET("user/{idUser}")
    fun getUserById(@Path("idUser") idUser: Int): Call<UserDetailsResponse>
}
