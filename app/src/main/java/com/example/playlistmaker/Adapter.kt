package com.example.playlistmaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Adapter(private var tracks: List<Track>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tracks[position])
    }


    override fun getItemCount() = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}