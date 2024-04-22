package edu.utap.tuneder3

import android.media.MediaPlayer
import android.content.Context
import android.media.AudioAttributes

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun playPreview(url: String) {
        mediaPlayer?.stop()
        releaseMediaPlayer()  // Ensure any existing player is released

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepareAsync() // Prepare the player asynchronously
            setOnPreparedListener {
                start() // Start playback once the media source is ready
            }
            setOnErrorListener { _, _, _ ->
                // Handle errors and clean up
                releaseMediaPlayer()
                false
            }
        }
    }

    fun stopPlayback() {
        mediaPlayer?.stop()
        releaseMediaPlayer()
        mediaPlayer = null
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
