package com.example.breathwatch.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.breathwatch.data.local.dao.AirQualityDao
import com.example.breathwatch.data.local.dao.HealthLogDao
import com.example.breathwatch.data.local.dao.WeatherDao
import com.example.breathwatch.data.local.entity.AirQualityEntity
import com.example.breathwatch.data.local.entity.HealthLogEntity
import com.example.breathwatch.data.local.entity.WeatherEntity
import com.example.breathwatch.util.Constants

@Database(
    entities = [
        AirQualityEntity::class,
        WeatherEntity::class,
        HealthLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun airQualityDataDao(): AirQualityDao
    abstract fun weatherDataDao(): WeatherDao
    abstract fun healthLogDao(): HealthLogDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                .addMigrations(
                    // Add migrations here when schema changes
                )
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    // Add type converters here if needed for complex types
}
