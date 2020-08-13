package me.edujtm.tuyo.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.edujtm.tuyo.MainNavigationDirections
import me.edujtm.tuyo.common.activityInjector
import me.edujtm.tuyo.common.viewModel
import me.edujtm.tuyo.databinding.FragmentHomeBinding
import me.edujtm.tuyo.ui.adapters.FlowPaginator
import me.edujtm.tuyo.ui.adapters.PlaylistHeaderAdapter

@ExperimentalCoroutinesApi
@FlowPreview
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel {
        activityInjector.homeViewModel
    }

    private var ui: FragmentHomeBinding? = null
    private var headerAdapter: PlaylistHeaderAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binds = FragmentHomeBinding.inflate(inflater, container, false)
        ui = binds
        return binds.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hostActivity = requireActivity()
        headerAdapter = PlaylistHeaderAdapter(
            onItemClickListener = { header -> openPlaylist(header.id) }
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        val listManager = LinearLayoutManager(hostActivity)
        val paginator = FlowPaginator(listManager)
        with(ui!!.playlistHeaderList) {
            adapter = headerAdapter
            layoutManager = listManager
            addItemDecoration(decoration)
            addOnScrollListener(paginator)
        }

        getPlaylistHeaders()
        bindPlaylistHeadersToRecyclerView()
        listenForMoreItemRequests(paginator)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ui = null
        headerAdapter = null
    }

    private fun bindPlaylistHeadersToRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.playlistHeaders.collectLatest { headers ->
                headerAdapter?.submitList(headers)
            }
        }
    }

    private fun getPlaylistHeaders() {
        // TODO: Handle Youtube API Error
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.getUserPlaylists()
        }
    }

    private fun listenForMoreItemRequests(paginator: FlowPaginator) {
        viewLifecycleOwner.lifecycleScope.launch {
            paginator.events.collectLatest {
                val currentToken = headerAdapter?.headers?.lastOrNull()?.nextPageToken
                currentToken?.let {
                    homeViewModel.requestPlaylistHeaders(currentToken)
                }
            }
        }
    }

    private fun openPlaylist(playlistId: String) {
        val playlistItemsDirection = MainNavigationDirections.actionViewPlaylistItems(playlistId = playlistId)
        val navController = findNavController()
        navController.navigate(playlistItemsDirection)
    }
}