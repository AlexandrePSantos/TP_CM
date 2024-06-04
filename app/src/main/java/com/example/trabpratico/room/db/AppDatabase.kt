package com.example.trabpratico.room.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trabpratico.room.dao.AppDao
import com.example.trabpratico.room.entities.project
import com.example.trabpratico.room.entities.state
import com.example.trabpratico.room.entities.synclog
import com.example.trabpratico.room.entities.task
import com.example.trabpratico.room.entities.user
import com.example.trabpratico.room.entities.usertype

@Database(entities = [usertype::class, user::class, state::class, project::class, task::class, synclog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    companion object {
        const val DATABASE_NAME = "ProjectManagerDB"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}