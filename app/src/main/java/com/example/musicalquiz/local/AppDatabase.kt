package com.example.musicalquiz.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Playlist::class, TrackEntity::class, PlaylistTrackCrossRef::class],
    version = 2, // ✅ Updated version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_database"
                )
                    .fallbackToDestructiveMigration() // ✅ This allows Room to auto-reset on schema change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
