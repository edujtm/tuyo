package me.edujtm.tuyo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import me.edujtm.tuyo.R
import me.edujtm.tuyo.common.injector
import me.edujtm.tuyo.common.viewModel

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel {
        requireActivity().injector.homeViewModel
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

}