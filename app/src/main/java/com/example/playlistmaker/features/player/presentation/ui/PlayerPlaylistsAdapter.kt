package com.example.playlistmaker.features.player.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.domain.model.Playlist

class PlayerPlaylistsAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlayerPlaylistsAdapter.PlaylistViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_selection, parent, false)
        return PlaylistViewHolder(view, onPlaylistClick)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaylistViewHolder(
        itemView: View,
        private val onPlaylistClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val coverImage: ImageView = itemView.findViewById(R.id.playlistCover)
        private val titleText: TextView = itemView.findViewById(R.id.playlistTitle)
        private val countText: TextView = itemView.findViewById(R.id.playlistTrackCount)

        fun bind(playlist: Playlist) {
            val context = itemView.context

            if (!playlist.coverPath.isNullOrBlank()) {
                Glide.with(coverImage)
                    .load(playlist.coverPath)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(coverImage)
            } else {
                coverImage.setImageResource(R.drawable.placeholder)
            }

            titleText.text = playlist.name
            countText.text = context.resources.getQuantityString(
                R.plurals.playlist_track_count,
                playlist.trackCount,
                playlist.trackCount
            )

            itemView.setOnClickListener {
                onPlaylistClick.invoke(playlist)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem == newItem
    }
}
