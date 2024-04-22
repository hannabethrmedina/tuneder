package edu.utap.tuneder3.ui.likedSongs

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import edu.utap.tuneder3.ui.home.HomeViewModel
import edu.utap.tuneder3.SongRowAdapter
import com.spotify.android.appremote.api.SpotifyAppRemote
import edu.utap.tuneder3.databinding.FragmentLikedsongsBinding
import edu.utap.tuneder3.R

class LikedSongsFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentLikedsongsBinding? = null
    private val binding get() = _binding!!
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val CLIENT_ID = "183fa8c353be4ea4965ed1b5fb65dce9"
    private val CLIENT_ID_HANNA = "8eee78021080498b89e2c8620760e493"
    private val CLIENT_ID_NOEL = "227b1d2303a54211900eed9753bcad65"
    private val REDIRECT_URI = "edu.utap.tuneder3://callback"

    var isPlaying = true
    var currentSong = ""

    override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                //monitorPlayerState()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })

    }


    fun playSong(songId: String) {
        spotifyAppRemote?.let {
            val songUri = "spotify:track:$songId"
            it.playerApi.play(songUri)
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            it.playerApi.pause()
            SpotifyAppRemote.disconnect(it)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedsongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SongRowAdapter(viewModel) {
            playSong(it.id)
            isPlaying = true
            binding.playerPlayPauseButton.setImageResource(R.drawable.ic_pause_black_24dp)

        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

//        val sharedPreferences = requireActivity().getSharedPreferences("SPOTIFY", 0)
//        val token = sharedPreferences.getString("token", null)
//        val userId = sharedPreferences.getString("userid", null)

        binding.playerPlayPauseButton.setOnClickListener {
            spotifyAppRemote?.let {
                if (isPlaying) {
                    it.playerApi.pause()
                    isPlaying = false
                    binding.playerPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                }
                else {
                    it.playerApi.resume()
                    isPlaying = true
                    binding.playerPlayPauseButton.setImageResource(R.drawable.ic_pause_black_24dp)
                }

            }
        }

        // Observe the LiveData from the ViewModel
        viewModel.observeLikedSongsList().observe(viewLifecycleOwner) { songs ->
            // Submit the list to the adapter
            adapter.submitList(songs)
        }

        // Structure for setOnSeekBarChangeListener found here: https://www.geeksforgeeks.org/seekbar-in-kotlin/
        binding.playerSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // The seek bar's progress has changed.
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // The user has touched the seek bar. Make sure the seek bar (and time)
                // stops progressing
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // The user is no longer touching the seek bar. Make sure the seek bar (and time
                // resume their progress
                //userModifyingSeekBar.set(false)
                val positionInSeekBar = seek.progress
                //viewModel.player.seekTo(positionInSeekBar)
            }

            }
        )

    }

    override fun onResume() {
        super.onResume()
        startUpdatingSeekBar()
    }

    private lateinit var handler: Handler
    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            spotifyAppRemote?.playerApi?.playerState?.setResultCallback {
                val currentSong = it.track
                val maxTime = currentSong.duration.toInt()
                val currentPosition = it.playbackPosition.toInt()

                updateSeekBar(currentPosition, maxTime)
            }
            handler.postDelayed(this, 500) // update every second
        }
    }

    private fun startUpdatingSeekBar() {
        handler = Handler(Looper.getMainLooper())
        handler.post(updateSeekBarRunnable)
    }

    fun updateSeekBar(currentPosition: Int, maxTime: Int) {
        binding.playerSeekBar.progress = currentPosition
        binding.playerSeekBar.max = maxTime

        val timePassed = convertTime(currentPosition)
        val timeRemaining = convertTime(maxTime - currentPosition)
        binding.playerTimePassedText.text = timePassed
        binding.playerTimeRemainingText.text = timeRemaining
    }


    fun monitorPlayerState() {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            Log.d("Spotify", "Event Callback Triggered")
            val currentSong = it.track
            val maxTime = currentSong.duration.toInt()
            val currentPosition = it.playbackPosition.toInt()

            binding.playerSeekBar.progress = currentPosition
            binding.playerSeekBar.max = maxTime

            val timePassed = convertTime(currentPosition)
            val timeRemaining = convertTime(maxTime - currentPosition)
            binding.playerTimePassedText.text = timePassed
            binding.playerTimeRemainingText.text = timeRemaining

        }
    }

    // This method converts time in milliseconds to minutes-second formatted string
    // with two digit minutes and two digit sections, e.g., 01:30
    private fun convertTime(milliseconds: Int): String {
        //XXX Write me
        val millisecondsToSeconds = milliseconds / 1000
        val minutes = millisecondsToSeconds / 60
        val seconds = millisecondsToSeconds % 60

        val minutesFormatted = String.format("%02d", minutes)
        val secondsFormatted = String.format("%02d", seconds)

        val convertedTime = "$minutesFormatted:$secondsFormatted"
        return convertedTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
