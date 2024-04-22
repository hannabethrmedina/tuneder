package edu.utap.tuneder3.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.tuneder3.FriendRowAdapter
import edu.utap.tuneder3.R
import edu.utap.tuneder3.ui.home.HomeViewModel
import edu.utap.tuneder3.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireActivity().getSharedPreferences("SPOTIFY", 0)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userid", null)

        val navController = findNavController()
        val adapter = FriendRowAdapter {
            if (token != null) {
                val firstName = it.display_name.split(" ").firstOrNull() ?: ""
                viewModel.generatedPlaylistPageTitle = "Date with $firstName!"
                viewModel.fetchSongsForPlaylistWithFriend(token, it.id)
                navController.navigate(R.id.action_friends_to_Generate)
            }
        }
        binding.PlaylistRecyclerView.adapter = adapter
        binding.PlaylistRecyclerView.layoutManager = LinearLayoutManager(context)

        // Observe the LiveData from the ViewModel
        viewModel.observeFriendsList().observe(viewLifecycleOwner) { friends ->
            // Submit the list to the adapter
            adapter.submitList(friends)
        }

        binding.addFriendButton.setOnClickListener {

            val friendId = binding.addFriendId.text.toString()

            if (friendId.isNotEmpty()) {
                token?.let {
                    if (userId != null) {
                        viewModel.addUserToFriendList(it, userId, friendId)
                    }
                }
            }
        }

        viewModel.observeFriendsToLikedSongs().observe(viewLifecycleOwner) {
            adapter.updateLikedSongsCount(it)
        }

    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = requireActivity().getSharedPreferences("SPOTIFY", 0)
        val userId = sharedPreferences.getString("userid", null)
        if (userId != null) {
            viewModel.startObservingFriendsLikedSongs(userId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
