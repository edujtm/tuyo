package me.edujtm.tuyo.ui.playlistitems

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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
import java.io.IOException

@ExperimentalCoroutinesApi
class PlaylistItemsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModel {
            activityInjector.mainViewModel
    }
    private val playlistItemsViewModel: PlaylistItemsViewModel by viewModel {
            activityInjector.playlistItemsViewModel
    }

    private val args: PlaylistItemsFragmentArgs by navArgs()

    private var playlistView: RecyclerView? = null
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_playlist_items, container, false)
        playlistView = root.findViewById(R.id.liked_videos_rv)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL)
        val hostActivity = requireActivity()
        playlistAdapter = PlaylistAdapter(hostActivity)
        with(playlistView!!) {
            layoutManager = LinearLayoutManager(hostActivity)
            adapter = playlistAdapter
            addItemDecoration(decoration)
        }

        getPlaylistItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playlistView = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            getPlaylistItems()
        }
    }

    private fun getPlaylistItems() {
        lifecycleScope.launch {
            try {
                val playlistId = args.playlistId
                if (playlistId != null) {
                    playlistItemsViewModel.getPlaylist(playlistId).collectLatest {
                        playlistAdapter.submitData(it)
                    }
                } else {
                    playlistItemsViewModel.getPrimaryPlaylist(args.primaryPlaylist).collectLatest {
                        playlistAdapter.submitData(it)
                    }
                }
            } catch (e: IOException) {
                handleYoutubeError(e)
            }
        }
    }

    // TODO: move this string to strings.xml
    private fun handleYoutubeError(error: Throwable) {
        when (error) {
            is GooglePlayServicesAvailabilityIOException -> mainViewModel.checkGoogleApiServices()
            is UserRecoverableAuthIOException -> startActivityForResult(error.intent, REQUEST_AUTHORIZATION)
            else -> Toast.makeText(requireActivity(), "The following error occurred ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val REQUEST_AUTHORIZATION = 1001
    }

}