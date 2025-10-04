package com.example.playlistmaker.features.media.presentation.ui

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

class PlaylistsAdapter : ListAdapter<Playlist, PlaylistsAdapter.PlaylistViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaylistViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val coverImage: ImageView = view.findViewById(R.id.ivPlaylistCover)
        private val titleText: TextView = view.findViewById(R.id.tvPlaylistName)
        private val trackCountText: TextView = view.findViewById(R.id.tvTrackCount)

        fun bind(playlist: Playlist) {
            val context = itemView.context

            val coverPath = playlist.coverPath
            if (!coverPath.isNullOrBlank()) {
                Glide.with(coverImage)
                    .load(coverPath)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(coverImage)
            } else {
                coverImage.setImageResource(R.drawable.placeholder)
            }

            titleText.text = playlist.name
            trackCountText.text = context.resources.getQuantityString(
                R.plurals.tracks,
                playlist.trackCount,
                playlist.trackCount
            )
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem == newItem
    }
}
