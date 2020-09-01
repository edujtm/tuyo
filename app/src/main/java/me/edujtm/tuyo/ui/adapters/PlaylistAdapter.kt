package me.edujtm.tuyo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem
import me.edujtm.tuyo.databinding.PlaylistItemBinding
import timber.log.Timber
import kotlin.properties.Delegates

typealias OnItemClick<T> = (T) -> Unit

/**
 * This will be the main adapter for playlist videos list. It'll be used by
 * PlaylistItemsFragment etc.
 */
class PlaylistAdapter(
    val context: Context,
    var onItemClickListener: OnItemClick<PlaylistItem>? = null,
    var onItemLongClickListener: OnItemClick<PlaylistItem>? = null
) : ListAdapter<PlaylistItem, PlaylistAdapter.ViewHolder>(PlaylistItemDiffCallback()) {

    private val _selectedItems = mutableSetOf<Int>()
    val selectedItems : Set<Int> = _selectedItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.d("binding item at position $position")
        val item = getItem(position)
        val selected = position in selectedItems
        item?.let {
            holder.playlistItem = it
            holder.bind(it)
            holder.setSelected(selected)
        }
    }

    fun setItemChecked(position: Int, checked: Boolean) {
        Timber.d("Item selected: $position")
        // Only re-renders if there's a change in the set
        if (checked) {
            if (_selectedItems.add(position)) {
                notifyItemChanged(position)
            }
        } else {
            if (_selectedItems.remove(position)) {
                notifyItemChanged(position)
            }
        }
    }

    inner class ViewHolder(
        val binding: PlaylistItemBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        var playlistItem: PlaylistItem? = null

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            playlistItem?.let { item ->
                onItemClickListener?.invoke(item)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            playlistItem?.let { item ->
                onItemLongClickListener?.invoke(item)
                return true
            }
            return false
        }

        fun bind(item: PlaylistItem) {
            binding.playlistTitleTv.text = item.title

            Glide.with(context)
                .load(item.thumbnailUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.videoThumbnailIv)
        }

        fun setSelected(selected: Boolean) {
            Timber.d("Setting overlay: ${if (selected) "visible" else "invisible"}")
            if (selected) {
                binding.selectedOverlay.visibility = View.VISIBLE
            } else {
                binding.selectedOverlay.visibility = View.GONE
            }
        }
    }
}

