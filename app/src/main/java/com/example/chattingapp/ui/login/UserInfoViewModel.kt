package com.example.chattingapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


data class UserModel(val email:String, var name:String, var introduction:String)
class UserInfoViewModel: ViewModel() {
    val userNameLiveData = MutableLiveData<String>()
    val emailLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()
    val introductionLiveData = MutableLiveData<String>()
    val otherUsersLiveData = MutableLiveData<ArrayList<UserModel>>()


    // recycler view에서 항목이 클릭된 것을 감지하기 위해.
    val itemClickEvent = MutableLiveData<Int>()

    // 초기화
    init {
        userNameLiveData.value = ""
        emailLiveData.value = ""
        passwordLiveData.value = ""
        introductionLiveData.value=""
        otherUsersLiveData.value=ArrayList()
    }
}