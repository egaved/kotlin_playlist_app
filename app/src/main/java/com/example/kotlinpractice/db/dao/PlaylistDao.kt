package com.example.kotlinpractice.db.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kotlinpractice.domain.Playlist

@Dao
interface PlaylistDao {
    @Insert
    fun insert(playlist: Playlist)

    @Update
    fun update(playlist: Playlist)

    @Delete
    fun delete(playlist: Playlist)

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): LiveData<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun get(id: Long): LiveData<Playlist?>

    @Query("SELECT * FROM playlists ORDER BY id DESC LIMIT 1")
    fun getLastPlaylist(): Playlist?

    @Query("UPDATE playlists SET name = :newName WHERE id = :id")
    fun updateName(id: Long, newName: String)
}