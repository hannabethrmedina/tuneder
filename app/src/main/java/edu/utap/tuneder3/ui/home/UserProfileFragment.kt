package edu.utap.tuneder3.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import edu.utap.tuneder3.R
import edu.utap.tuneder3.databinding.FragmentUserprofileBinding
import edu.utap.tuneder3.glide.GlideHelper

class UserProfileFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: SongCardAdapter
    private var _binding: FragmentUserprofileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserprofileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as? AppCompatActivity)?.supportActionBar?.hide()



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeCurrentUser().observe(viewLifecycleOwner) {
            binding.displayNameText.text = it.display_name
            binding.userNameText.text = it.id
            val imageUrl = it.imageUrl
            if (imageUrl != null) {
                GlideHelper.glideFetch(
                    imageUrl,
                    imageUrl,
                    binding.profilePic
                ) // Adjusted for GlideHelper
            }
        }

        viewModel.observeLikedSongsList().observe(viewLifecycleOwner) {
            binding.likedSongsText.text = it.size.toString()
        }

        val navController = findNavController()
        binding.backButton.setOnClickListener {
            navController.navigate(R.id.action_userprofile_to_discover)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}