package me.edujtm.tuyo.ui.likedvideos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LikedVideosViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Liked Videos Fragment"
    }
    val text: LiveData<String> = _text
}