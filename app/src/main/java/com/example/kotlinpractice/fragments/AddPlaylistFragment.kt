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
import com.example.kotlinpractice.R
import com.example.kotlinpractice.databinding.FragmentAddPlaylistBinding
import com.example.kotlinpractice.db.AppDatabase
import com.example.kotlinpractice.db.viewmodel.PlaylistViewModel
import com.example.kotlinpractice.db.viewmodel.PlaylistViewModelFactory
import com.example.kotlinpractice.domain.Playlist
import kotlinx.coroutines.launch

class AddPlaylistFragment : Fragment() {

    private lateinit var viewModel: PlaylistViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding: FragmentAddPlaylistBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_playlist, container, false)

        val application = requireNotNull(this.activity).application
        val playlistDao = AppDatabase.getInstance(application).getPlaylistDao()
        val playlistViewModelFactory = PlaylistViewModelFactory(playlistDao, application)
        viewModel = ViewModelProvider(this, playlistViewModelFactory)
                .get(PlaylistViewModel::class.java)

        binding.addPlaylistButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val author = binding.authorEditText.text.toString().trim()

            if (name.isNotEmpty() && author.isNotEmpty()) {
                val newPlaylist = Playlist(
                    name = name,
                    author = author
                )

                lifecycleScope.launch {
                    viewModel.insertPlaylist(newPlaylist)
                    findNavController().navigateUp()
                }
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}