package com.example.kotlinpractice.db.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotlinpractice.db.dao.PlaylistDao
import com.example.kotlinpractice.db.dao.SongDao
import com.example.kotlinpractice.domain.Playlist
import com.example.kotlinpractice.domain.Song
import com.example.kotlinpractice.utils.toTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistContentViewModel(
    private val playlistDao: PlaylistDao,
    private val songDao: SongDao,
    private val playlistId: Long,
    application: Application
) : AndroidViewModel(application) {

    private val _songsForPlaylist = songDao.getSongsForPlaylist(playlistId)
    private val _artistFilter = MutableLiveData<String>()
    private val _genreFilter = MutableLiveData<String>()

    val filteredSongs = MediatorLiveData<List<Song>>().apply {
        addSource(_songsForPlaylist) { songs ->
            filterAndSetData(songs, _artistFilter.value, _genreFilter.value)
        }
        addSource(_artistFilter) { artist ->
            filterAndSetData(_songsForPlaylist.value, artist, _genreFilter.value)
        }
        addSource(_genreFilter) { genre ->
            filterAndSetData(_songsForPlaylist.value, _artistFilter.value, genre)
        }
    }

    suspend fun updatePlaylistName(newName: String) = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.IO) {
            playlistDao.updateName(playlistId, newName)
        }
    }

    fun getTotalDuration(songs: List<Song>): String {
        var totalDurationInSeconds = 0
        for (song in songs) {
            totalDurationInSeconds += song.duration
        }
        return totalDurationInSeconds.toTimeString()
    }

    fun getPlaylist(): LiveData<Playlist?> {
        return playlistDao.get(playlistId)
    }

    fun getSongsForPlaylist(): LiveData<List<Song>> {
        return songDao.getSongsForPlaylist(playlistId)
    }

    suspend fun deleteSong(song: Song) {
        withContext(Dispatchers.IO) {
            songDao.delete(song)
        }
    }

    fun setArtistFilter(artist: String) {
        _artistFilter.value = artist
    }

    fun setGenreFilter(genre: String) {
        _genreFilter.value = genre
    }

    private fun filterAndSetData(
        songs: List<Song>?,
        artistFilter: String?,
        genreFilter: String?
    ) {
        val filtered = songs?.filter { song ->
            (artistFilter.isNullOrEmpty() || song.artist.contains(artistFilter, ignoreCase = true)) &&
                    (genreFilter.isNullOrEmpty() || song.genre.contains(genreFilter, ignoreCase = true))
        }
        filteredSongs.value = filtered
    }
}