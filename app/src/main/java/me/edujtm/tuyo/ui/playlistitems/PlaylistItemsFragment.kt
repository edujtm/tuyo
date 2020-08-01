package me.edujtm.tuyo.ui.playlistitems

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.common.activityInjector
import me.edujtm.tuyo.common.activityViewModel
import me.edujtm.tuyo.common.startImplicit
import me.edujtm.tuyo.common.viewModel
import me.edujtm.tuyo.databinding.FragmentPlaylistItemsBinding
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

        with(ui!!.playlistRecyclerView) {
            layoutManager = LinearLayoutManager(hostActivity)
            adapter = playlistAdapter
            addItemDecoration(decoration)
        }

        setupLoadingScreen()
        getPlaylistItems()
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

    private fun setupLoadingScreen() {
        viewLifecycleOwner.lifecycleScope.launch {
            playlistAdapter?.let { adapter ->
                adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    with(ui!!) {
                        playlistRecyclerView.isVisible = true
                        loadingItemsPb.isVisible = false
                    }
                }
            }
        }
    }

    private fun getPlaylistItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (args.playlistId != null) {
                    // Kotlin can't smart cast complex expressions
                    playlistItemsViewModel.getPlaylist(args.playlistId!!).collectLatest {
                        playlistAdapter?.submitData(it)
                    }
                } else {
                    playlistItemsViewModel.getPrimaryPlaylist(args.primaryPlaylist).collectLatest {
                        playlistAdapter?.submitData(it)
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

    private fun <T> Any?.onNullResult(action: () -> T) : T? {
        return if (this == null) action() else null
    }

}