package com.example.kotlinpractice.db.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinpractice.db.dao.PlaylistDao
import com.example.kotlinpractice.db.dao.SongDao
import com.example.kotlinpractice.domain.Playlist

class PlaylistContentViewModelFactory(
    private val playlistDao: PlaylistDao,
    private val songDao: SongDao,
    private val playlistId: Long,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistContentViewModel::class.java)) {
            return PlaylistContentViewModel(playlistDao, songDao, playlistId, application) as  T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}