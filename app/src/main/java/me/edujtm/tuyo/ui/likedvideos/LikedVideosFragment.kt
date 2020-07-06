package me.edujtm.tuyo.ui.likedvideos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import me.edujtm.tuyo.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedVideosFragment : Fragment() {

    private val likedVideosViewModel: LikedVideosViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_liked_videos, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        likedVideosViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root

    }
}