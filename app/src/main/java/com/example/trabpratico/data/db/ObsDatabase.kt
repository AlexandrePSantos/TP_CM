package com.example.trabpratico.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.trabpratico.data.dao.ObservationDao
import com.example.trabpratico.data.entities.Observation

@Database(entities = [Observation :: class], version = 1, exportSchema = false)
abstract class ObsDatabase : RoomDatabase(){
    abstract  fun  obsDao(): ObservationDao

    companion object {
        @Volatile
        private var INSTANCE: ObsDatabase? = null

        fun getDatabase(context: Context): ObsDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ObsDatabase::class.java,
                    "obs_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
