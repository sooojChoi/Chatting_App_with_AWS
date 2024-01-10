package com.example.chattingapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.datastore.generated.model.Message

class MessageViewModel : ViewModel() {
    val msgLiveData = MutableLiveData<ArrayList<Message>>()
    init {
        msgLiveData.value = ArrayList()
    }
}