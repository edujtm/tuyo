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

typealias OnItemClick<T> = (T) -> Unit

/**
 * This will be the main adapter for playlist videos list. It'll be used by
 * PlaylistItemsFragment etc.
 */
class PlaylistAdapter(
    val context: Context,
    val onItemClickListener: OnItemClick<PlaylistItem>? = null
) : ListAdapter<PlaylistItem, PlaylistAdapter.ViewHolder>(PlaylistItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, context, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.playlistItem = item
            holder.bind(item)
        }
    }

    class ViewHolder(
        val binding: PlaylistItemBinding,
        val context: Context,
        val onItemClickListener: OnItemClick<PlaylistItem>? = null
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
                .load(item.thumbnailUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.videoThumbnailIv)
        }
    }
}

