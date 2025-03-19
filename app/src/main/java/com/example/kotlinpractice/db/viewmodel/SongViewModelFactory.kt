package com.example.kotlinpractice.db.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinpractice.db.dao.SongDao

class SongViewModelFactory(
    private val songDao: SongDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
            return SongViewModel(songDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}