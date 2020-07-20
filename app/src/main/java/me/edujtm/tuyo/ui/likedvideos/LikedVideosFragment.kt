package me.edujtm.tuyo.ui.likedvideos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.common.activityInjector
import me.edujtm.tuyo.common.activityViewModel
import me.edujtm.tuyo.common.viewModel
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.ui.adapters.PlaylistAdapter

// TODO: refactor this class so that it represents all playlists (not only liked videos)
class LikedVideosFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModel {
            activityInjector.mainViewModel
    }
    private val likedVideosViewModel: LikedVideosViewModel by viewModel {
            activityInjector.likedVideosViewModel
    }

    private lateinit var textView: TextView
    private lateinit var playlistView: RecyclerView
    private lateinit var playlistItems: MutableList<PlaylistItem>
    private val playlistAdapter = PlaylistAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_liked_videos, container, false)
        textView = root.findViewById(R.id.text_dashboard)
        playlistItems = mutableListOf()
        playlistView = root.findViewById(R.id.liked_videos_rv)
        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL)
        with(playlistView) {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = playlistAdapter
            addItemDecoration(decoration)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        likedVideosViewModel.likedVideos.observe(viewLifecycleOwner, Observer { request ->
            when (request) {
                is RequestState.Loading -> textView.text = "Loading request"
                is RequestState.Success -> {
                    textView.text = "Your Liked Videos"
                    (playlistView.adapter as PlaylistAdapter).replacePlaylistItems(request.data)
                }
                is RequestState.Failure -> handleYoutubeError(request.e)
            }
        })
         */

        lifecycleScope.launch {
            likedVideosViewModel.getLikedVideos().collectLatest {
                playlistAdapter.submitData(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            likedVideosViewModel.getLikedVideos()
        }
    }

    private fun handleYoutubeError(error: Throwable) {
        when (error) {
            is GooglePlayServicesAvailabilityIOException -> mainViewModel.checkGoogleApiServices()
            is UserRecoverableAuthIOException -> startActivityForResult(error.intent, REQUEST_AUTHORIZATION)
            else -> textView.text = "The following error occurred ${error.message}"
        }
    }

    companion object {
        const val REQUEST_AUTHORIZATION = 1001
    }

}