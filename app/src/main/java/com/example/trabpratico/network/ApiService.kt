package com.example.trabpratico.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT

data class RegisterRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val idType: Int = 3
)
data class LoginRequest(
    val email: String,
    val password: String
)
data class UserDetailsResponse(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val idType: Int,
    val password: String
)

data class LoginResponse(
    val token: String,
    val idUser: Int
)

data class UserUpdate(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val idType: Int
)

interface ApiService {
    @POST("auth/signup")
    fun register(@Body request: RegisterRequest): Call<Void>

    @POST("auth/signin")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("user")
    fun getAllUsers(): Call<UserDetailsResponse>

    @GET("user/{idUser}")
    fun getUserById(@Path("idUser") idUser: Int): Call<UserDetailsResponse>

    @PUT("user/update/{idUser}")
    fun updateUser(@Path("idUser") userId: Int, @Body userUpdate: UserUpdate): Call<Void>

}
