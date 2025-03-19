package com.example.kotlinpractice.db.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinpractice.db.dao.PlaylistDao

class PlaylistViewModelFactory(
    private val playlistDao: PlaylistDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
            return PlaylistViewModel(playlistDao, application) as  T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}