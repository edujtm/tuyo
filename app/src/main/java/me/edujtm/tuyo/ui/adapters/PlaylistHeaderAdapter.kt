package me.edujtm.tuyo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.edujtm.tuyo.databinding.PlaylistHeaderItemBinding
import me.edujtm.tuyo.domain.domainmodel.PlaylistHeader

class PlaylistHeaderAdapter(
    val onItemClickListener: OnItemClick<PlaylistHeader>? = null
) : RecyclerView.Adapter<PlaylistHeaderAdapter.ViewHolder>() {

    private val _headers = mutableListOf<PlaylistHeader>()
    val headers: List<PlaylistHeader>
        get() = _headers

    override fun getItemCount(): Int = _headers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PlaylistHeaderItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = headers[position]
        holder.bind(item)
    }

    fun submitList(items: List<PlaylistHeader>) {
        _headers.clear()
        _headers.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder(
        val binding: PlaylistHeaderItemBinding,
        val onItemClick : OnItemClick<PlaylistHeader>? = null
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var playlistHeader: PlaylistHeader? = null

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            playlistHeader?.let { header ->
                onItemClick?.invoke(header)
            }
        }

        fun bind(item: PlaylistHeader) {
            playlistHeader = item

            binding.playlistTitle.text = item.title
        }
    }
}