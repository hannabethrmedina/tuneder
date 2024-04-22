package edu.utap.tuneder3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.tuneder3.databinding.RowFriendBinding
import edu.utap.tuneder3.glide.GlideHelper

class FriendRowAdapter(private val clickListener: (User) -> Unit) :
    ListAdapter<User, FriendRowAdapter.ViewHolder>(UserDiff()) {

    private var friendsToLikedSongsMap = emptyMap<String, String>()

    class UserDiff : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id  // Assumes each song has a unique ID
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem  // This checks all fields, you can customize as needed
        }
    }

    class ViewHolder(val binding: RowFriendBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = getItem(position)
        holder.binding.rowName.text = friend.display_name
        val imageUrl = friend.imageUrl
        if (imageUrl != null) {
            GlideHelper.glideFetch(
                imageUrl,
                imageUrl,
                holder.binding.rowPic
            )
        }

        val id = friend.id
        val likedSongsCount = friendsToLikedSongsMap[id]
        holder.binding.rowLikedSongsNumber.text = likedSongsCount

        holder.binding.rowGenerate.setOnClickListener {
            clickListener(friend)
        }
    }

    fun updateLikedSongsCount(newMap: Map<String, String>) {
        friendsToLikedSongsMap = newMap
        notifyDataSetChanged()
    }
}
