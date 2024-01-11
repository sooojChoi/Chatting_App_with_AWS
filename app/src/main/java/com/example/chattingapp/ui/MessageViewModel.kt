package com.example.chattingapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.datastore.generated.model.Message

class MessageViewModel : ViewModel() {
    val msgLiveData = MutableLiveData<ArrayList<Message>>()
    // message 데이터를 dynamoDB에서 가져온 room들의 id
    val roomIdWithMessage = MutableLiveData<ArrayList<String>>()
    init {
        msgLiveData.value = ArrayList()
        roomIdWithMessage.value = ArrayList()
    }
}