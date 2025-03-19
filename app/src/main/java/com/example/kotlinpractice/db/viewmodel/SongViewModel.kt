package com.example.kotlinpractice.db.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kotlinpractice.db.dao.SongDao
import com.example.kotlinpractice.domain.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class SongViewModel (
    private val songDao: SongDao,
    application: Application
) : AndroidViewModel(application) {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    fun getSongsForPlaylist(playlistId: Long): LiveData<List<Song>> {
        return songDao.getSongsForPlaylist(playlistId)
    }

    suspend fun insertSong(song: Song) {
        withContext(Dispatchers.IO) {
            songDao.insert(song)
        }
    }

    suspend fun deleteSong(song: Song) {
        withContext(Dispatchers.IO) {
            songDao.delete(song)
        }
    }

}