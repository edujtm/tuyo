package me.edujtm.tuyo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.databinding.PlaylistItemBinding

typealias OnItemClick<T> = (T) -> Unit

/**
 * This will be the main adapter for playlist videos list. It'll be used by
 * LikedVideosFragment, PlaylistDetails, etc.
 */
class PlaylistAdapter(
    val context: Context,
    val onItemClickListener: OnItemClick<PlaylistItem>? = null
) : ListAdapter<PlaylistItem, PlaylistAdapter.ViewHolder>(PlaylistItemDiffCallback()) {

    private val _items = mutableListOf<PlaylistItem>()
    val items : List<PlaylistItem>
        get() = _items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.playlistItem = item
            holder.bind(item)
        }
    }

    fun insertAll(playlistItems: List<PlaylistItem>) {
        _items.clear()
        notifyDataSetChanged()
        _items.addAll(playlistItems)
        submitList(_items)
    }

    // TODO: refactor inner class
    inner class ViewHolder(
        val binding: PlaylistItemBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var playlistItem: PlaylistItem? = null

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            playlistItem?.let { item ->
                onItemClickListener?.invoke(item)
            }
        }

        fun bind(item: PlaylistItem) {
            binding.playlistTitleTv.text = item.title

            Glide.with(context)
                .load(item.thumbnail)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.videoThumbnailIv)
        }
    }
}

