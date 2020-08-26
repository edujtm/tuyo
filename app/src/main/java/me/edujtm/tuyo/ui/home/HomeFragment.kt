package me.edujtm.tuyo.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.edujtm.tuyo.MainNavigationDirections
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.common.*
import me.edujtm.tuyo.databinding.FragmentHomeBinding
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.ui.adapters.FlowPaginator
import me.edujtm.tuyo.ui.adapters.PlaylistHeaderAdapter
import me.edujtm.tuyo.ui.playlistitems.PlaylistItemsFragment

@ExperimentalCoroutinesApi
@FlowPreview
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val mainViewModel: MainViewModel by activityViewModel {
        activityInjector.mainViewModel
    }

    private val homeViewModel: HomeViewModel by viewModel {
        activityInjector.homeViewModel
    }

    private val ui : FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)
    private var headerAdapter: PlaylistHeaderAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hostActivity = requireActivity()
        headerAdapter = PlaylistHeaderAdapter(
            onItemClickListener = { header -> openPlaylist(header.id) }
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        val listManager = LinearLayoutManager(hostActivity)
        val paginator = FlowPaginator(listManager)
        with(ui.playlistHeaderList) {
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
        headerAdapter = null
    }

    // TODO: implement refresh on HomeViewModel
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleApi.REQUEST_AUTHORIZATION && resultCode == RESULT_OK) {
            homeViewModel.requestPlaylistHeaders()
        }
    }

    private fun bindPlaylistHeadersToRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.playlistHeaders.collectLatest { headersRequest ->
                when (headersRequest) {
                    is RequestState.Success -> headerAdapter?.submitList(headersRequest.data)
                    is RequestState.Failure -> handleYoutubeError(headersRequest.error)
                    is RequestState.Loading -> {}
                }
            }
        }
    }

    private fun getPlaylistHeaders() {
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

    private fun handleYoutubeError(error: Throwable) {
        when (error) {
            is GooglePlayServicesAvailabilityIOException -> mainViewModel.checkGoogleApiServices()
            is UserRecoverableAuthIOException ->
                startActivityForResult(error.intent, GoogleApi.REQUEST_AUTHORIZATION)
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

    private fun openPlaylist(playlistId: String) {
        val playlistItemsDirection = MainNavigationDirections.actionViewPlaylistItems(playlistId = playlistId)
        val navController = findNavController()
        navController.navigate(playlistItemsDirection)
    }
}