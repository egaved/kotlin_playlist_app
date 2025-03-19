package com.example.kotlinpractice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinpractice.R
import com.example.kotlinpractice.adapers.SongAdapter
import com.example.kotlinpractice.databinding.FragmentPlaylistContentBinding
import com.example.kotlinpractice.db.AppDatabase
import com.example.kotlinpractice.db.viewmodel.PlaylistContentViewModel
import com.example.kotlinpractice.db.viewmodel.PlaylistContentViewModelFactory
import kotlinx.coroutines.launch

class PlaylistContentFragment : Fragment() {

    private lateinit var viewModel: PlaylistContentViewModel
    private lateinit var adapter: SongAdapter
    private lateinit var binding: FragmentPlaylistContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_playlist_content, container, false
        )

        val application = requireNotNull(this.activity).application
        val playlistDao = AppDatabase.getInstance(application).getPlaylistDao()
        val songDao = AppDatabase.getInstance(application).getSongDao()

        val args: PlaylistContentFragmentArgs by navArgs()
        val playlistId = args.playlistId

        val viewModelFactory =
            PlaylistContentViewModelFactory(playlistDao, songDao, playlistId, application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(PlaylistContentViewModel::class.java)

        viewModel.getPlaylist().observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                binding.playlistTitleTextView.text = it.name
            }
        }


        // Наблюдение за отфильтрованными песнями
        viewModel.filteredSongs.observe(viewLifecycleOwner) { filteredSongs ->
            adapter.updateSongs(filteredSongs)
        }

        // Обработка нажатия на кнопку фильтрации
        binding.filterButton.setOnClickListener {
            toggleFilterVisibility()
            updateRecyclerViewConstraints()
        }

        // Обработка ввода в поля фильтрации
        binding.artistFilterEditText.addTextChangedListener {
            viewModel.setArtistFilter(it.toString())
        }

        binding.genreFilterEditText.addTextChangedListener {
            viewModel.setGenreFilter(it.toString())
        }

        binding.songList.layoutManager = LinearLayoutManager(requireContext())
        adapter = SongAdapter(emptyList()) { song ->
            lifecycleScope.launch {
                viewModel.deleteSong(song)
            }
        }
        binding.songList.adapter = adapter

        viewModel.getSongsForPlaylist().observe(viewLifecycleOwner) { songs ->
            adapter.updatePlaylists(songs)
        }

        binding.fabAddSong.setOnClickListener {
            navigateToAddSongFragment(playlistId)
        }

        binding.editButton.setOnClickListener {
            enterEditMode()
            updateRecyclerViewConstraints()
        }

        // Обработка нажатия на кнопку сохранения
        binding.saveButton.setOnClickListener {
            saveChanges()
            updateRecyclerViewConstraints()
        }

        // Обработка нажатия на кнопку отмены
        binding.cancelButton.setOnClickListener {
            exitEditMode()
            updateRecyclerViewConstraints()
        }

        return binding.root
    }

    private fun navigateToAddSongFragment(playlistId: Long) {
        val action = PlaylistContentFragmentDirections
            .actionPlaylistContentFragmentToAddSongFragment(playlistId)
        findNavController().navigate(action)
    }

    private fun enterEditMode() {
        binding.headerLayout.visibility = View.GONE
        binding.editModeLayout.visibility = View.VISIBLE

        viewModel.getPlaylist().value?.let {
            binding.editPlaylistNameEditText.setText(it.name)
        }
    }

    // Сохранение изменений
    private fun saveChanges() {
        val newName = binding.editPlaylistNameEditText.text.toString().trim()
        if (newName.isNotEmpty()) {
            lifecycleScope.launch {
                viewModel.updatePlaylistName(newName)
                exitEditMode()
            }
        } else {
            Toast.makeText(context, "Введите название", Toast.LENGTH_SHORT).show()
        }
    }

    // Выход из режима редактирования
    private fun exitEditMode() {
        binding.headerLayout.visibility = View.VISIBLE
        binding.editModeLayout.visibility = View.GONE
    }

     //Переключение видимости фильтров
    private fun toggleFilterVisibility() {
        if (binding.filterLayout.visibility == View.VISIBLE) {
            binding.filterLayout.visibility = View.GONE
        } else {
            binding.filterLayout.visibility = View.VISIBLE
        }
    }

    // Применение фильтров
//    private fun applyFilters(artistFilter: String, genreFilter: String) {
//        viewModel.getSongsForPlaylist().value?.let { songs ->
//            val filteredSongs = songs.filter { song ->
//                (artistFilter.isEmpty() || song.artist.contains(artistFilter, ignoreCase = true)) &&
//                        (genreFilter.isEmpty() || song.genre.contains(
//                            genreFilter,
//                            ignoreCase = true
//                        ))
//            }
//            adapter.updateSongs(filteredSongs)
//        }
//    }

    private fun updateRecyclerViewConstraints() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root as ConstraintLayout)

        if (binding.editModeLayout.visibility == View.VISIBLE) {
            // привязка к нижней границе editModeLayout
            constraintSet.connect(
                binding.songList.id,
                ConstraintSet.TOP,
                binding.editModeLayout.id,
                ConstraintSet.BOTTOM
            )
        } else if (binding.filterLayout.visibility == View.VISIBLE) {
            // привязка к нижней границе filterLayout
            constraintSet.connect(
                binding.songList.id,
                ConstraintSet.TOP,
                binding.filterLayout.id,
                ConstraintSet.BOTTOM
            )
        } else {
            // привязка к нижней границе headerLayout
            constraintSet.connect(
                binding.songList.id,
                ConstraintSet.TOP,
                binding.headerLayout.id,
                ConstraintSet.BOTTOM
            )
        }

        constraintSet.applyTo(binding.root as ConstraintLayout)
    }
}
