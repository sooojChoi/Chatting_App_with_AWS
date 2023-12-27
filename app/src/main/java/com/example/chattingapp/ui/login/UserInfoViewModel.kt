package com.example.chattingapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserInfoViewModel: ViewModel() {
    val userNameLiveData = MutableLiveData<String>()
    val emailLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()

    // 초기화
    init {
        userNameLiveData.value = ""
        emailLiveData.value = ""
        passwordLiveData.value = ""
    }
}