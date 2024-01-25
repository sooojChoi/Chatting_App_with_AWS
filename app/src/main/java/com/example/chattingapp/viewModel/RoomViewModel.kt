package com.example.chattingapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amplifyframework.datastore.generated.model.Room

class RoomViewModel : ViewModel()  {
    val roomLiveData = MutableLiveData<ArrayList<Room>>()
    // recycler view에서 항목이 클릭된 것을 감지하기 위해.
    val itemClickEvent = MutableLiveData<Int>()
    val currentRoomId = MutableLiveData<String>()

    init {
        roomLiveData.value = ArrayList()
        itemClickEvent.value = -1
        currentRoomId.value = ""
    }
}