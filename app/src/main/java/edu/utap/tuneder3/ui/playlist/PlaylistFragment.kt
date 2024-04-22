package edu.utap.tuneder3.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.tuneder3.ui.home.HomeViewModel
import edu.utap.tuneder3.SongRowAdapter
import edu.utap.tuneder3.databinding.FragmentPlaylistBinding

class PlaylistFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SongRowAdapter(viewModel) {

        }
        binding.PlaylistRecyclerView.adapter = adapter
        binding.PlaylistRecyclerView.layoutManager = LinearLayoutManager(context)

        val sharedPreferences = requireActivity().getSharedPreferences("SPOTIFY", 0)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userid", null)

        // Observe the LiveData from the ViewModel
        viewModel.observeGeneratedPlaylist().observe(viewLifecycleOwner) { songs ->
            // Submit the list to the adapter
            adapter.submitList(songs)
        }

        binding.title.text = viewModel.generatedPlaylistPageTitle

        binding.generateButton.setOnClickListener {
            viewModel.generatedPlaylistPageTitle = "Date with a Playlist"
            token?.let {
                viewModel.fetchSongsForPlaylist(it)
            }
        }

        binding.createPlaylistButton.setOnClickListener {
            if (token != null && userId != null) {
                val dialog = PlaylistPromptFragment { playlistName ->
                    viewModel.createPlaylistInSpotify(token, userId, playlistName)
                }
                dialog.show(parentFragmentManager, "PlaylistDialogFragment")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
