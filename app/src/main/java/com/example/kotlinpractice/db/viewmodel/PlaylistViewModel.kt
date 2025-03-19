package com.example.kotlinpractice.db.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinpractice.db.dao.PlaylistDao
import com.example.kotlinpractice.db.dao.SongDao
import com.example.kotlinpractice.domain.Playlist
import com.example.kotlinpractice.domain.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistViewModel(
    private val playlistDao: PlaylistDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val _allPlaylists = playlistDao.getAllPlaylists()
    private val _searchQuery = MutableLiveData<String>()

    suspend fun insertPlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            playlistDao.insert(playlist)
        }
    }

    fun getAllPlaylists(): LiveData<List<Playlist>> {
        return playlistDao.getAllPlaylists()
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO){
            playlistDao.delete(playlist)
        }
    }

    val filteredPlaylists = MediatorLiveData<List<Playlist>>().apply {
        addSource(_allPlaylists) { playlists ->
            filterAndSetData(playlists, _searchQuery.value)
        }
        addSource(_searchQuery) { query ->
            filterAndSetData(_allPlaylists.value, query)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun filterAndSetData(
        playlists: List<Playlist>?,
        searchQuery: String?
    ) {
        val filtered = playlists?.filter { playlist ->
            searchQuery.isNullOrEmpty() || playlist.name.contains(searchQuery, ignoreCase = true)
        }
        filteredPlaylists.value = filtered
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}