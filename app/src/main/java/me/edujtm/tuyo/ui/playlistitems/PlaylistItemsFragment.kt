package me.edujtm.tuyo.ui.playlistitems

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.youtube.model.PlaylistItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.common.activityInjector
import me.edujtm.tuyo.common.activityViewModel
import me.edujtm.tuyo.common.startImplicit
import me.edujtm.tuyo.common.viewModel
import me.edujtm.tuyo.databinding.FragmentPlaylistItemsBinding
import me.edujtm.tuyo.ui.adapters.FlowPaginator
import me.edujtm.tuyo.ui.adapters.PlaylistAdapter
import java.io.IOException

@ExperimentalCoroutinesApi
@FlowPreview
class PlaylistItemsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModel {
            activityInjector.mainViewModel
    }
    private val playlistItemsViewModel: PlaylistItemsViewModel by viewModel {
            activityInjector.playlistItemsViewModel
    }

    private val args: PlaylistItemsFragmentArgs by navArgs()
    private var playlistAdapter: PlaylistAdapter? = null
    private var ui: FragmentPlaylistItemsBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binds = FragmentPlaylistItemsBinding.inflate(inflater, container, false)
        ui = binds
        return binds.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL)
        val hostActivity = requireActivity()

        playlistAdapter = PlaylistAdapter(
            hostActivity,
            onItemClickListener = { playlistItem ->
                watchYoutube(hostActivity, playlistItem.videoId)
            })
        playlistAdapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val listManager = LinearLayoutManager(hostActivity)
        val paginator = FlowPaginator(listManager)
        with(ui!!.playlistRecyclerView) {
            layoutManager = listManager
            adapter = playlistAdapter
            addItemDecoration(decoration)
            addOnScrollListener(paginator)
        }

        getPlaylistItems()
        listenForMoreItemRequests(paginator)
    }

    override fun onDestroyView() {
        ui = null
        playlistAdapter = null
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            getPlaylistItems()
        }
    }

    private fun listenForMoreItemRequests(paginator: FlowPaginator) {
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("UI_PLAYLIST_ITEMS", "Listening for pagination")
            paginator.events.collect {
                Log.d("FLOW_PAGINATOR", "Received page: ${it.first}")
                val pageToken = playlistAdapter?.items?.lastOrNull()?.nextPageKey
                Log.d("FLOW_PAGINATOR", "Trying to get token: $pageToken")
                pageToken?.let { token ->
                    if (args.playlistId != null) {
                        playlistItemsViewModel.requestPlaylistItems(args.playlistId!!, token)
                    } else {
                        playlistItemsViewModel.requestPlaylistItems(args.primaryPlaylist, token)
                    }
                }
            }
        }
    }

    private fun getPlaylistItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("UI_PLAYLIST_ITEMS", "Listening for playlist items")
            try {
                if (args.playlistId != null) {
                    // Null assertion: Kotlin can't smart cast complex expressions
                    // But this code won't run on multiple threads or will be changed
                    // after initialization
                    // TODO: check if this actually might throw exception
                    playlistItemsViewModel
                        .getPlaylist(args.playlistId!!)
                        .collectLatest { playlistItems ->
                            Log.d("UI_PLAYLIST_ITEMS", "Received data: ${playlistItems.size}")
                            if (playlistItems.isEmpty()) {
                                playlistItemsViewModel.requestPlaylistItems(args.playlistId!!)
                            }
                            playlistAdapter?.insertAll(playlistItems)
                        }
                } else {
                   playlistItemsViewModel
                        .getPrimaryPlaylist(args.primaryPlaylist)
                        .collectLatest { playlistItems ->
                            Log.d("UI_PLAYLIST_ITEMS", "Received data: ${playlistItems.size}")
                            if (playlistItems.isEmpty()) {
                                playlistItemsViewModel.requestPlaylistItems(args.primaryPlaylist)
                            }
                            playlistAdapter?.insertAll(playlistItems)
                        }
                }
            } catch (e: IOException) {
                Log.e("API_ERROR", "Received error: $e")
                handleYoutubeError(e)
            }
        }
    }

    // TODO: change toast to snackbar
    private fun handleYoutubeError(error: Throwable) {
        when (error) {
            is GooglePlayServicesAvailabilityIOException -> mainViewModel.checkGoogleApiServices()
            is UserRecoverableAuthIOException -> startActivityForResult(error.intent, REQUEST_AUTHORIZATION)
            is GoogleJsonResponseException ->  {
                // TODO: properly handle API errors
                val message = when (error.statusCode) {
                    403 -> "API limit exceeded"
                    else -> error.localizedMessage
                }

                ui?.let {
                    Snackbar.make(it.root, message, Snackbar.LENGTH_LONG).show()
                }
            }
            else ->
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.generic_error_message, error.message),
                    Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun watchYoutube(context: Context, videoId: String) {
        val component = context.startImplicit { intent ->
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("vnd.youtube:$videoId")
        }

        // Fallback with webview in case youtube is not installed
        if (component == null) {
            context.startImplicit { intent ->
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(
                    "http://www.youtube.com/watch?v=$videoId"
                )
            }
        }
    }

    companion object {
        const val REQUEST_AUTHORIZATION = 1001
    }
}