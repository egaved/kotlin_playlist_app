package com.example.kotlinpractice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinpractice.R
import com.example.kotlinpractice.adapters.PlaylistAdapter
import com.example.kotlinpractice.databinding.FragmentPlaylistListBinding
import com.example.kotlinpractice.db.AppDatabase
import com.example.kotlinpractice.db.viewmodel.PlaylistViewModel
import com.example.kotlinpractice.db.viewmodel.PlaylistViewModelFactory

import kotlinx.coroutines.launch


class PlaylistListFragment : Fragment() {

    private lateinit var viewModel: PlaylistViewModel
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentPlaylistListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_playlist_list, container, false)

        val application = requireNotNull(this.activity).application
        val playlistDao = AppDatabase.getInstance(application).getPlaylistDao()
        val playlistViewModelFactory = PlaylistViewModelFactory(playlistDao, application)
        viewModel = ViewModelProvider(this, playlistViewModelFactory)
            .get(PlaylistViewModel::class.java)

        // Наблюдение за отфильтрованными плейлистами
        viewModel.filteredPlaylists.observe(viewLifecycleOwner) { playlists ->
            adapter.updatePlaylists(playlists)
        }

        binding.playlistList.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlaylistAdapter(emptyList(),
            { playlist ->
                navigateToPlaylistContent(playlist.id)
            },
            { playlist ->
                lifecycleScope.launch {
                    viewModel.deletePlaylist(playlist)
                }
            }
        )
        binding.playlistList.adapter = adapter

        binding.searchEditText.addTextChangedListener { text ->
            viewModel.setSearchQuery(text.toString())
        }

        binding.fabAddPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playlistListFragment_to_addPlaylistFragment)
        }

        return binding.root
    }

    private fun navigateToPlaylistContent(playlistId: Long) {
        val action = PlaylistListFragmentDirections
            .actionPlaylistListFragmentToPlaylistContentFragment(playlistId)
        findNavController().navigate(action)
    }



}