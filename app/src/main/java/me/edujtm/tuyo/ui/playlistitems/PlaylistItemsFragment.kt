package me.edujtm.tuyo.ui.likedvideos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.common.activityInjector
import me.edujtm.tuyo.common.activityViewModel
import me.edujtm.tuyo.common.viewModel
import me.edujtm.tuyo.ui.adapters.PlaylistAdapter

// TODO: refactor this class so that it represents all playlists (not only liked videos)
@ExperimentalCoroutinesApi
class PlaylistItemsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModel {
            activityInjector.mainViewModel
    }
    private val likedVideosViewModel: PlaylistItemsViewModel by viewModel {
            activityInjector.playlistItemsViewModel
    }

    private lateinit var textView: TextView
    private lateinit var playlistView: RecyclerView
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_playlist_items, container, false)
        textView = root.findViewById(R.id.text_dashboard)
        playlistView = root.findViewById(R.id.liked_videos_rv)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL)
        val hostActivity = requireActivity()
        playlistAdapter = PlaylistAdapter(hostActivity)
        with(playlistView) {
            layoutManager = LinearLayoutManager(hostActivity)
            adapter = playlistAdapter
            addItemDecoration(decoration)
        }

        getLikedVideos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            getLikedVideos()
        }
    }

    private fun getLikedVideos() {
        lifecycleScope.launch {
            try {
                likedVideosViewModel.getLikedVideos().collectLatest {
                    Log.d("UI_LISTENER", "Received new data")
                    playlistAdapter.submitData(it)
                }
            } catch (e: Exception) {
                handleYoutubeError(e)
            }
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