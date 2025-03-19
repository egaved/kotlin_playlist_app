package com.example.kotlinpractice.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.kotlinpractice.utils.toTimeString

@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playlist_id"])]
)
data class Song (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "artist")
    val artist: String,

    @ColumnInfo(name = "genre")
    val genre: String,

    @ColumnInfo(name = "duration")
    val duration: Int,

    @ColumnInfo(name = "playlist_id")
    val playlistId : Long
) {
    fun getFormattedDuration(): String {
        return duration.toTimeString()
    }
}