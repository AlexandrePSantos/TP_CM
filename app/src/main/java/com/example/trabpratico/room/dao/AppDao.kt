package com.example.trabpratico.room.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.trabpratico.room.entities.project
import com.example.trabpratico.room.entities.synclog
import com.example.trabpratico.room.entities.task
import com.example.trabpratico.room.entities.user

@Dao
interface AppDao {
    // User
    @Query("SELECT * FROM user")
    fun getAllUsers(): List<user>

    @Query("SELECT * FROM user WHERE iduser = :id")
    fun getUserById(id: Int): user

    @Insert
    fun insertUser(user: user)

    @Update
    fun updateUser(user: user)

    @Delete
    fun deleteUser(user: user)

    // Project
    @Query("SELECT * FROM project")
    fun getAllProjects(): List<project>

    @Query("SELECT * FROM project WHERE idproject = :id")
    fun getProjectById(id: Int): project

    @Insert
    fun insertProject(project: project)

    @Update
    fun updateProject(project: project)

    // Task
    @Query("SELECT * FROM task")
    fun getAllTasks(): List<task>

    @Query("SELECT * FROM task WHERE idtask = :id")
    fun getTaskById(id: Int): task

    @Insert
    fun insertTask(task: task)

    @Update
    fun updateTask(task: task)

    // SyncLog
    @Query("SELECT * FROM synclog")
    fun getAllSyncLogs(): List<synclog>

    @Insert
    fun insertSyncLog(syncLog: synclog)
}