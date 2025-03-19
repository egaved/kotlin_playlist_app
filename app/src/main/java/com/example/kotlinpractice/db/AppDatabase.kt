package com.example.kotlinpractice.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinpractice.db.dao.PlaylistDao
import com.example.kotlinpractice.db.dao.SongDao
import com.example.kotlinpractice.domain.Playlist
import com.example.kotlinpractice.domain.Song

@Database(entities = [Playlist::class, Song::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPlaylistDao(): PlaylistDao
    abstract fun getSongDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "playlist_maker_db")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                println("Database initialized")
                return instance
            }
        }

    }
}