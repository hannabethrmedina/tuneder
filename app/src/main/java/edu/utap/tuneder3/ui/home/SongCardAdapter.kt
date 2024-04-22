package edu.utap.tuneder3.ui.home

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.utap.tuneder3.AudioPlayer
import edu.utap.tuneder3.R
import edu.utap.tuneder3.Song
import edu.utap.tuneder3.glide.GlideHelper

class SongCardAdapter (
    private var songs: List<Song> = emptyList()
    ) : RecyclerView.Adapter<SongCardAdapter.ViewHolder>() {

        private var audioPlayer: AudioPlayer = AudioPlayer()
        private var mediaPlayer: MediaPlayer? = null

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val songTitle: TextView = view.findViewById(R.id.songTitle)
            val songArtist: TextView = view.findViewById(R.id.artistName)
            val songImage: ImageView = view.findViewById(R.id.albumImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_song, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val song = songs[position]
            holder.songTitle.text = song.songTitle
            holder.songArtist.text = song.artistName

            val imageUrl = song.albumImageUrl
            GlideHelper.glideFetch(imageUrl, imageUrl, holder.songImage)

            if (song.previewUrl.isNotEmpty())
                playSongPreview(song.previewUrl)
        }

        override fun getItemCount(): Int = songs.size

        fun setSongs(songs: List<Song>) {
            this.songs = songs
            notifyDataSetChanged()
        }

        override fun onViewRecycled(holder: ViewHolder) {
            super.onViewRecycled(holder)
            stopPlayback()
        }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        stopPlayback()
    }

        fun playSongPreview(url: String) {
            releaseMediaPlayer()

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