package edu.utap.tuneder3

import android.view.LayoutInflater
import android.view.ViewGroup
import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.tuneder3.databinding.RowSongBinding
import edu.utap.tuneder3.glide.GlideHelper
import edu.utap.tuneder3.ui.home.HomeViewModel

class SongRowAdapter(
    private val viewModel: HomeViewModel,
    private val clickListener: (Song) -> Unit) :
    ListAdapter<Song, SongRowAdapter.ViewHolder>(SongDiff()) {


    var previousPosition = -1
    private var currentSelectedPosition = viewModel.currentlyPlayingPosition
    private var previouslySelectedPosition = -1

    class SongDiff : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id  // Assumes each song has a unique ID
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem  // This checks all fields, you can customize as needed
        }
    }

    class ViewHolder(val binding: RowSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = getItem(position)
        holder.binding.rowTitle.text = song.songTitle
        holder.binding.rowArtist.text = song.artistName
        GlideHelper.glideFetch(song.albumImageUrl, song.albumImageUrl, holder.binding.rowPic) // Adjusted for GlideHelper

        if (position == currentSelectedPosition) {
            holder.binding.rowTitle.setTextColor(Color.parseColor("#1DB954"))
            holder.binding.rowArtist.setTextColor(Color.parseColor("#1DB954"))
        }
        else {
            holder.binding.rowTitle.setTextColor(Color.WHITE)
            holder.binding.rowArtist.setTextColor(Color.WHITE)
        }

        holder.binding.root.setOnClickListener {
//            holder.binding.rowTitle.setTextColor(Color.parseColor("#1DB954"))
//            holder.binding.rowArtist.setTextColor(Color.parseColor("#1DB954"))

            previousPosition = currentSelectedPosition
            currentSelectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(currentSelectedPosition)
            clickListener(song)
        }
    }
}
