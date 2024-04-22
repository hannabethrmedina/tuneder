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
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import edu.utap.tuneder3.R
import edu.utap.tuneder3.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: SongCardAdapter
    private lateinit var cardStackView: CardStackView
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        val sharedPreferences = requireActivity().getSharedPreferences("SPOTIFY", 0)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            viewModel.setCurrentUser(token)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SongCardAdapter()
        cardStackView = view.findViewById(R.id.card_stack_view)
        val navController = findNavController()


        binding.userProfile.setOnClickListener {
            navController.navigate(R.id.action_discover_to_userprofile)
        }

        val sharedPreferences = requireActivity().getSharedPreferences("SPOTIFY", 0)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userid", null)
        if (token != null && userId != null) {
            viewModel.initializeLikedSongList(token, userId)
            viewModel.initializeFriendList(token, userId)
            viewModel.fetchNewSong(token)
        }

        if (token != null && userId != null) {
            setupCardStackView(token, userId)
        }
    }

    private fun setupCardStackView(token: String, userId: String) {
        val layoutManager = CardStackLayoutManager(context, object : CardStackListener {
            override fun onCardSwiped(direction: Direction) {
                if (direction == Direction.Right) {
                    viewModel.addSongToLikedSongList(token, userId)
                }
                viewModel.fetchNewSong(token)
            }
            override fun onCardDragging(direction: Direction, ratio: Float) {
            }
            override fun onCardRewound() {
            }
            override fun onCardCanceled() {
            }
            override fun onCardAppeared(view: View, position: Int) {
            }
            override fun onCardDisappeared(view: View, position: Int) {
            }
        })

        cardStackView.layoutManager = layoutManager
        adapter = SongCardAdapter()
        cardStackView.adapter = adapter

        viewModel.observeCurrentSong().observe(viewLifecycleOwner) {
            adapter.setSongs(listOf(it))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        adapter.stopPlayback()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopPlayback()
    }
}