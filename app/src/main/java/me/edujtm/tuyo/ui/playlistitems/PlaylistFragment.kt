package me.edujtm.tuyo.ui.playlistitems

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
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
import me.edujtm.tuyo.domain.domainmodel.PrimaryPlaylist
import me.edujtm.tuyo.domain.domainmodel.SelectedPlaylist
import me.edujtm.tuyo.databinding.FragmentPlaylistItemsBinding
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.ui.adapters.FlowPaginator
import me.edujtm.tuyo.ui.adapters.PlaylistAdapter
import timber.log.Timber

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decoration = DividerItemDecoration(requireContext(),  DividerItemDecoration.VERTICAL)
        val hostActivity = requireActivity()

        playlistAdapter = PlaylistAdapter(
            hostActivity,
            //onItemClickListener = ::watchYoutube,
            onItemLongClickListener = ::handleLongClick
        )

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
        getSelectedItems()
        listenForMoreItemRequests(paginator)
    }

    override fun onDestroyView() {
        playlistAdapter = null
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GoogleApi.REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            playlistItemsViewModel.refresh(selectedPlaylist)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.playlist_item_selected_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun listenForMoreItemRequests(paginator: FlowPaginator) {
        viewLifecycleOwner.lifecycleScope.launch {
            paginator.events.collect {
                val pageToken = playlistAdapter?.currentList?.lastOrNull()?.nextPageToken
                Timber.d("Trying to get token: $pageToken")
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
                        Timber.d("Received data ${playlistRequest.data.size}")
                        playlistAdapter?.submitList(playlistRequest.data)
                    }
                    is RequestState.Failure ->
                        mainViewModel.sendEvent(MainViewModel.Event.UiError(playlistRequest.error))
                }
            }
        }
    }

    private fun getPlaylistItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            playlistItemsViewModel.getPlaylist(selectedPlaylist)
        }
    }

    private fun getSelectedItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            playlistItemsViewModel.selectedItems.collectLatest { ids ->
                playlistAdapter?.let { adapter ->
                    adapter.currentList.forEachIndexed { index, playlistItem ->
                        if (playlistItem.id in ids) {
                            adapter.setItemChecked(index, true)
                        }
                    }
                }
            }
        }
    }

    private fun watchYoutube(playlistItem: PlaylistItem) {
        val context = requireContext()
        val videoId = playlistItem.videoId

        val sucessful = context.startImplicit { intent ->
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("vnd.youtube:$videoId")
        }

        // Fallback with webview in case youtube is not installed
        if (!sucessful) {
            context.startImplicit { intent ->
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(
                    "http://www.youtube.com/watch?v=$videoId"
                )
            }
        }
    }

    private fun handleLongClick(playlistItem: PlaylistItem) {
        Timber.d("Long click on item: ${playlistItem.title}")
        playlistItemsViewModel.toggleSelectedItem(playlistItem.id)
    }
}