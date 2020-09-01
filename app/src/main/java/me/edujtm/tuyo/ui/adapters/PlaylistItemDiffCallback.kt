package me.edujtm.tuyo.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem

class PlaylistItemDiffCallback: DiffUtil.ItemCallback<PlaylistItem>() {
    override fun areItemsTheSame(
        oldItem: PlaylistItem, newItem: PlaylistItem
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return (oldItem.title == newItem.title &&
                oldItem.thumbnailUrl == newItem.thumbnailUrl)
    }
}