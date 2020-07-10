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
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.common.observe
import me.edujtm.tuyo.repository.http.RequestState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedVideosFragment : Fragment() {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val likedVideosViewModel: LikedVideosViewModel by viewModel()
    private lateinit var textView: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_liked_videos, container, false)
        textView = root.findViewById(R.id.text_dashboard)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likedVideosViewModel.videoInfo.observe(viewLifecycleOwner, Observer { request ->
            when (request) {
                is RequestState.Loading -> textView.text = "Loading request"
                is RequestState.Success -> textView.text = request.data.joinToString("\n")
                is RequestState.Failure -> handleYoutubeError(request.e)
            }
        })

        likedVideosViewModel.getVideoInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            likedVideosViewModel.getVideoInfo()
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