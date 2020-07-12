package me.edujtm.tuyo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.edujtm.tuyo.R
import me.edujtm.tuyo.data.PlaylistItem

/**
 * This will be the main adapter for playlist videos list. It'll be used by
 * LikedVideosFragment, PlaylistDetails, etc.
 */
class PlaylistAdapter(private val playlistItems: MutableList<PlaylistItem>)
    : ListAdapter<PlaylistItem, PlaylistAdapter.ViewHolder>(PlaylistItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val playlistItemView = inflater.inflate(R.layout.playlist_item, parent, false)
        return ViewHolder(playlistItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.titleTextView.text = item.title
    }

    fun replacePlaylistItems(items: List<PlaylistItem>) {
        playlistItems.clear()
        playlistItems.addAll(items)
        submitList(playlistItems)
    }

    inner class ViewHolder(playlistItemView: View) : RecyclerView.ViewHolder(playlistItemView) {
        val titleTextView: TextView = playlistItemView.findViewById(R.id.playlist_title_tv)
    }
}

