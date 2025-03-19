package com.example.kotlinpractice

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kotlinpractice.db.AppDatabase
import com.example.kotlinpractice.db.dao.PlaylistDao
import com.example.kotlinpractice.db.dao.SongDao
import com.example.kotlinpractice.domain.Playlist

import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

class DBTest {
    private lateinit var playlistDao: PlaylistDao
    private lateinit var songDao: SongDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        playlistDao = db.getPlaylistDao()
        songDao = db.getSongDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val playlist = Playlist(
            1,
            "test",
            "me"
        )
        playlistDao.insert(playlist)
        val addedPlaylist = playlistDao.getLastPlaylist()

        assertEquals(addedPlaylist?.name, "test" )
        assertEquals(addedPlaylist?.author, "me" )
    }
}