package com.example.trabpratico.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT

// Request classes
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
data class UserUpdate(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val idType: Int
)
data class UserTypeRequest(
    val type: String
)
data class ProjectRequest(
    val nameProject: String,
    val startDateP: String?,
    val endDateP: String?,
    val idState: Int,
    val idUser: Int,
    val completionStatus: Boolean,
    val performanceReview: String?,
    val obs: String?
)
data class TaskRequest(
    val nameTask: String,
    val startDateT: String?,
    val endDateT: String?,
    val idProject: Int,
    val idState: Int,
    val photo: String?,
    val timeSpend: String?,
    val local: String?,
    val taxes: Double?,
    val completionRate: Double?,
    val photos: String?,
    val observations: String?
)
data class StateRequest(
    val state: String
)

// Response classes
data class LoginResponse(
    val token: String,
    val idUser: Int
)
data class UserDetailsResponse(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val idType: Int,
    val password: String
)
data class UserTypeResponse(
    val idType: Int,
    val type: String
)
data class ProjectResponse(
    val idProject: Int,
    val nameProject: String,
    val startDateP: String?,
    val endDateP: String?,
    val idState: Int,
    val idUser: Int,
    val completionStatus: Boolean,
    val performanceReview: String?,
    val obs: String?
)
data class TaskResponse(
    val idTask: Int,
    val nameTask: String,
    val startDateT: String?,
    val endDateT: String?,
    val idProject: Int,
    val idState: Int,
    val photo: String?,
    val timeSpend: String?,
    val local: String?,
    val taxes: Double?,
    val completionRate: Double?,
    val photos: String?,
    val observations: String?
)
data class StateResponse(
    val idState: Int,
    val state: String
)

interface ApiService {

    // Auth
    @POST("auth/signup")
    fun register(@Body request: RegisterRequest): Call<Void>
    @POST("auth/signin")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // User
    @GET("user")
    fun getAllUsers(): Call<List<UserDetailsResponse>>
    @GET("user/{idUser}")
    fun getUserById(@Path("idUser") idUser: Int): Call<UserDetailsResponse>
    @POST("user/create")
    fun createUser(@Body user: RegisterRequest): Call<Void>
    @PUT("user/update/{idUser}")
    fun updateUser(@Path("idUser") userId: Int, @Body userUpdate: UserUpdate): Call<Void>
    @DELETE("user/delete/{idUser}")
    fun deleteUser(@Path("idUser") userId: Int): Call<Void>

    // UserType
    @GET("userType")
    fun getAllUserTypes(): Call<List<UserTypeResponse>>
    @GET("userType/{idType}")
    fun getUserTypeById(@Path("idType") idType: Int): Call<UserTypeResponse>
    @POST("userType/create")
    fun createUserType(@Body userType: UserTypeRequest): Call<Void>
    @PUT("userType/update/{idType}")
    fun updateUserType(@Path("idType") idType: Int, @Body userType: UserTypeRequest): Call<Void>
    @DELETE("userType/delete/{idType}")
    fun deleteUserType(@Path("idType") idType: Int): Call<Void>

    // Project
    @GET("project")
    fun getAllProjects(): Call<List<ProjectResponse>>
    @GET("project/{idProject}")
    fun getProjectById(@Path("idProject") idProject: Int): Call<ProjectResponse>
    @POST("project/create")
    fun createProject(@Body project: ProjectRequest): Call<Void>
    @PUT("project/update/{idProject}")
    fun updateProject(@Path("idProject") idProject: Int, @Body project: ProjectRequest): Call<Void>
    @DELETE("project/delete/{idProject}")
    fun deleteProject(@Path("idProject") idProject: Int): Call<Void>

    // Task
    @GET("task")
    fun getAllTasks(): Call<List<TaskResponse>>
    @GET("task/{idTask}")
    fun getTaskById(@Path("idTask") idTask: Int): Call<TaskResponse>
    @POST("task/create")
    fun createTask(@Body task: TaskRequest): Call<Void>
    @PUT("task/update/{idTask}")
    fun updateTask(@Path("idTask") idTask: Int, @Body task: TaskRequest): Call<Void>
    @DELETE("task/delete/{idTask}")
    fun deleteTask(@Path("idTask") idTask: Int): Call<Void>

    // State
    @GET("state")
    fun getAllStates(): Call<List<StateResponse>>
    @GET("state/{idState}")
    fun getStateById(@Path("idState") idState: Int): Call<StateResponse>
    @POST("state/create")
    fun createState(@Body state: StateRequest): Call<Void>
    @PUT("state/update/{idState}")
    fun updateState(@Path("idState") idState: Int, @Body state: StateRequest): Call<Void>
    @DELETE("state/delete/{idState}")
    fun deleteState(@Path("idState") idState: Int): Call<Void>
}
