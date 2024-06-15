package com.example.trabpratico.network

import retrofit2.Call
import retrofit2.http.*

// Request classes
data class RegisterRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val idtype: Int = 3
)

data class RegisterRequestAdmin(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val idtype: Int
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserUpdate(
    val name: String,
    val username: String,
    val email: String,
    val password: String? = null,
    var idtype: Int
)

data class UserTypeRequest(
    val type: String
)

data class ProjectRequest(
    val nameproject: String,
    val startdatep: String?,
    val enddatep: String?,
    val idstate: Int,
    val iduser: Int,
    val completionstatus: Boolean,
    val performancereview: String?,
    val obs: String?
)

data class TaskRequest(
    val nametask: String,
    val startdatet: String?,
    val enddatet: String?,
    val idproject: Int,
    val idstate: Int,
    val timespend: Int?,
    val local: String?,
    val completionrate: Double?,
)

data class StateRequest(
    val state: String
)

data class UserTaskRequest(
    val iduser: Int,
    val idtask: Int
)

data class ObsRequest(
    val idtask: Int,
    val content: String
)

// Response classes
data class LoginResponse(
    val token: String,
    val iduser: Int
)

data class UserDetailsResponse(
    val iduser: Int,
    var name: String,
    var username: String,
    var email: String,
    var idtype: Int,
    var password: String
)

data class UserTypeResponse(
    val idtype: Int,
    val type: String
)

data class ProjectResponse(
    val idproject: Int,
    val nameproject: String,
    val startdatep: String?,
    val enddatep: String?,
    val idstate: Int,
    val iduser: Int,
    val completionstatus: Boolean,
    val performancereview: String?,
    val obs: String?
)

data class TaskResponse(
    val idtask: Int,
    val nametask: String,
    val startdatet: String?,
    val enddatet: String?,
    val idproject: Int,
    val idstate: Int,
    val timespend: Int?,
    val local: String?,
    val completionrate: Double?,
)

data class StateResponse(
    val idstate: Int,
    val state: String
)

data class UserTaskResponse(
    val iduser: Int,
    val idtask: Int
)

data class ObsResponse(
    val idtask: Int,
    val content: String
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

    @GET("user/{iduser}")
    fun getUserById(@Path("iduser") iduser: Int): Call<UserDetailsResponse>

    @POST("user/create")
    fun createUser(@Body user: RegisterRequestAdmin): Call<Void>

    @PUT("user/update/{iduser}")
    fun updateUser(@Path("iduser") userId: Int, @Body userUpdate: UserUpdate): Call<Void>

    @DELETE("user/delete/{iduser}")
    fun deleteUser(@Path("iduser") userId: Int): Call<Void>

    // UserType
    @GET("userType")
    fun getAllUserTypes(): Call<List<UserTypeResponse>>

    @GET("userType/{idtype}")
    fun getUserTypeById(@Path("idtype") idtype: Int): Call<UserTypeResponse>

    @POST("userType/create")
    fun createUserType(@Body userType: UserTypeRequest): Call<Void>

    @PUT("userType/update/{idtype}")
    fun updateUserType(@Path("idtype") idtype: Int, @Body userType: UserTypeRequest): Call<Void>

    @DELETE("userType/delete/{idtype}")
    fun deleteUserType(@Path("idtype") idtype: Int): Call<Void>

    // Project
    @GET("project")
    fun getAllProjects(): Call<List<ProjectResponse>>

    @GET("project/{idproject}")
    fun getProjectById(@Path("idproject") idproject: Int): Call<ProjectResponse>

    @POST("project/create")
    fun createProject(@Body project: ProjectRequest): Call<Void>

    @PUT("project/update/{idproject}")
    fun updateProject(@Path("idproject") idproject: Int, @Body project: ProjectRequest): Call<Void>

    @DELETE("project/delete/{idproject}")
    fun deleteProject(@Path("idproject") idproject: Int): Call<Void>

    // Task
    @GET("task")
    fun getAllTasks(): Call<List<TaskResponse>>

    @GET("task/{idtask}")
    fun getTaskById(@Path("idtask") idtask: Int): Call<TaskResponse>

    @POST("task/create")
    fun createTask(@Body task: TaskRequest): Call<Void>

    @PUT("task/update/{idtask}")
    fun updateTask(@Path("idtask") idtask: Int, @Body task: TaskRequest): Call<Void>

    @DELETE("task/delete/{idtask}")
    fun deleteTask(@Path("idtask") idtask: Int): Call<Void>

    // State
    @GET("state")
    fun getAllStates(): Call<List<StateResponse>>

    @GET("state/{idstate}")
    fun getStateById(@Path("idstate") idstate: Int): Call<StateResponse>

    @POST("state/create")
    fun createState(@Body state: StateRequest): Call<Void>

    @PUT("state/update/{idstate}")
    fun updateState(@Path("idstate") idstate: Int, @Body state: StateRequest): Call<Void>

    @DELETE("state/delete/{idstate}")
    fun deleteState(@Path("idstate") idstate: Int): Call<Void>

    // UserTask
    @POST("usertask/create")
    fun createUserTask(@Body userTaskRequest: UserTaskRequest): Call<Void>
    @GET("usertask")
    fun getUserTasks(): Call<List<UserTaskResponse>>

    // Obs
    @GET("obs")
    fun getAllObs(): Call<List<ObsResponse>>
    @GET("obs/{idobs}")
    fun getObsById(@Path("idobs") idobs: Int): Call<ObsResponse>
    @POST("obs/create")
    fun createObs(@Body obs: ObsRequest): Call<Void>
}
