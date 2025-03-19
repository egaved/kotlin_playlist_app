package com.example.kotlinpractice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kotlinpractice.R
import com.example.kotlinpractice.databinding.FragmentAddSongBinding
import com.example.kotlinpractice.db.AppDatabase
import com.example.kotlinpractice.db.viewmodel.SongViewModel
import com.example.kotlinpractice.db.viewmodel.SongViewModelFactory
import com.example.kotlinpractice.domain.Song
import kotlinx.coroutines.launch

class AddSongFragment : Fragment() {

    private lateinit var viewModel: SongViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentAddSongBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_song, container, false)

        val application = requireNotNull(this.activity).application
        val songDao = AppDatabase.getInstance(application).getSongDao()
        val songViewModelFactory = SongViewModelFactory(songDao, application)
        viewModel = ViewModelProvider(this, songViewModelFactory)
            .get(SongViewModel::class.java)

        val args: PlaylistContentFragmentArgs by navArgs()
        val playlistId = args.playlistId

        binding.addSongButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val artist = binding.artistEditText.text.toString().trim()
            val genre = binding.genreEditText.text.toString().trim()
            val minutes = binding.minutesEditText.text.toString().toIntOrNull() ?: 0
            val seconds = binding.secondsEditText.text.toString().toIntOrNull() ?: 0
            val durationInSeconds = minutes * 60 + seconds

            if (title.trim().isEmpty()) {
                Toast.makeText(context, "Введите название трека", Toast.LENGTH_SHORT).show()
            } else if (artist.trim().isEmpty()) {
                Toast.makeText(context, "Введите имя исполнителя", Toast.LENGTH_SHORT).show()
            } else if (minutes <= 0 && seconds <= 0) {
                Toast.makeText(context, "Продолжительность трека должна быть больше 0", Toast.LENGTH_SHORT).show()
            } else if (seconds < 0 || seconds >= 60) {
                Toast.makeText(context, "Секунды должны быть в диапазоне от 0 до 59", Toast.LENGTH_SHORT).show()
            } else {
                val newSong = Song(
                    title = title.trim(),
                    artist = artist.trim(),
                    genre = genre.trim(),
                    playlistId = playlistId,
                    duration = durationInSeconds
                )

                lifecycleScope.launch {
                    viewModel.insertSong(newSong)
                    findNavController().navigateUp()
                }
            }
        }

        return binding.root
    }

}