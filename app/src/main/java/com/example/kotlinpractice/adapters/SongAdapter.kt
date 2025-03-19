package com.example.kotlinpractice.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinpractice.databinding.ItemSongBinding
import com.example.kotlinpractice.domain.Song

class SongAdapter (
    private var songsForPlaylist: List<Song>,
    private val onDeleteClickListener: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int = songsForPlaylist.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songsForPlaylist[position]
        holder.bind(song)
        holder.binding.deleteButton.setOnClickListener {
            onDeleteClickListener(song)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylists(newSongs: List<Song>) {
        songsForPlaylist = newSongs
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSongs(newSongs: List<Song>) {
        songsForPlaylist = newSongs
        notifyDataSetChanged()
    }

    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(song: Song) {
                binding.songTitleTextView.text = song.title
                binding.songArtistTextView.text = song.artist
                binding.songGenreTextView.text = song.genre
                binding.songDurationTextView.text = song.getFormattedDuration()
            }
    }
}