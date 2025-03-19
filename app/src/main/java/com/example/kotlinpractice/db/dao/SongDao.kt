package com.example.kotlinpractice.db.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kotlinpractice.domain.Song

@Dao
interface SongDao {
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM songs WHERE playlist_id = :playlistId")
    fun getSongsForPlaylist(playlistId: Long): LiveData<List<Song>>
}