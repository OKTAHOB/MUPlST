package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivArtwork: ImageView = itemView.findViewById(R.id.artwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.trackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.artistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(track: Track) {
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvTrackTime.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .transform(RoundedCorners(2))
            )
            .into(ivArtwork)
    }
}