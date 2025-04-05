package com.example.projandroid2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projandroid2.model.Favorito

@Database(entities = [Favorito::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritoDao(): FavoritoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ubs_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
