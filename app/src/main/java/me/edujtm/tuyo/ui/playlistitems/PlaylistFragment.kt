package me.edujtm.tuyo.ui.playlistitems

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.common.*
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.SelectedPlaylist
import me.edujtm.tuyo.databinding.FragmentPlaylistItemsBinding
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.ui.adapters.FlowPaginator
import me.edujtm.tuyo.ui.adapters.PlaylistAdapter

class PlaylistFragment : Fragment(R.layout.fragment_playlist_items) {

    private val mainViewModel: MainViewModel by activityViewModel {
            activityInjector.mainViewModel
    }
    private val playlistItemsViewModel: PlaylistViewModel by viewModel {
            activityInjector.playlistViewModel
    }

    private val args: PlaylistFragmentArgs by navArgs()

    /**
     * This fragment can be initialized with either a playlist string ID or
     * a [PrimaryPlaylist] enum.
     */
    private val selectedPlaylist: SelectedPlaylist by lazy {
        if (args.playlistId != null) {
            SelectedPlaylist.Extra(args.playlistId!!)
        } else {
            SelectedPlaylist.Primary(args.primaryPlaylist)
        }
    }

    private var playlistAdapter: PlaylistAdapter? = null
    private val ui: FragmentPlaylistItemsBinding by viewBinding(FragmentPlaylistItemsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL)
        val hostActivity = requireActivity()

        playlistAdapter = PlaylistAdapter(
            hostActivity,
            onItemClickListener = { playlistItem ->
                watchYoutube(hostActivity, playlistItem.videoId)
            })

        val listManager = LinearLayoutManager(hostActivity)
        val paginator = FlowPaginator(listManager)
        with(ui.playlistRecyclerView) {
            layoutManager = listManager
            adapter = playlistAdapter
            addItemDecoration(decoration)
            addOnScrollListener(paginator)
        }

        bindPlaylistItemsToRecyclerView()
        getPlaylistItems()
        listenForMoreItemRequests(paginator)
    }

    override fun onDestroyView() {
        playlistAdapter = null
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            playlistItemsViewModel.refresh(selectedPlaylist)
        }
    }

    private fun listenForMoreItemRequests(paginator: FlowPaginator) {
        viewLifecycleOwner.lifecycleScope.launch {
            paginator.events.collect {
                val pageToken = playlistAdapter?.currentList?.lastOrNull()?.nextPageToken
                Log.d("FLOW_PAGINATOR", "Trying to get token: $pageToken")
                pageToken?.let { token ->
                    playlistItemsViewModel.requestPlaylistItems(selectedPlaylist, token)
                }
            }
        }
    }

    private fun bindPlaylistItemsToRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            playlistItemsViewModel.playlistItems.collectLatest { playlistRequest ->
                when (playlistRequest) {
                    is RequestState.Loading -> { }
                    is RequestState.Success -> {
                        Log.d("UI_PLAYLIST_ITEMS", "Received data: ${playlistRequest.data.size}")
                        playlistAdapter?.submitList(playlistRequest.data)
                    }
                    is RequestState.Failure -> handleYoutubeError(playlistRequest.error)
                }
            }
        }
    }

    private fun getPlaylistItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            playlistItemsViewModel.getPlaylist(selectedPlaylist)
        }
    }

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
                Snackbar.make(ui.root, message, Snackbar.LENGTH_LONG).show()
            }
            else -> {
                Snackbar.make(
                    ui.root,
                    getString(R.string.generic_error_message, error.message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
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