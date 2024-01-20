package com.example.chattingapp.ui.login

import android.renderscript.ScriptGroup.Input
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.InputStream


data class UserModel(val email:String, var name:String, var introduction:String, var image: String?)
class UserInfoViewModel: ViewModel() {
    val userNameLiveData = MutableLiveData<String>()
    val emailLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()
    val introductionLiveData = MutableLiveData<String>()
    val otherUsersLiveData = MutableLiveData<ArrayList<UserModel>>()
    val imageLiveData = MutableLiveData<String>()


    // recycler view에서 항목이 클릭된 것을 감지하기 위해.
    val itemClickEvent = MutableLiveData<Int>()

    // 초기화
    init {
        userNameLiveData.value = ""
        emailLiveData.value = ""
        passwordLiveData.value = ""
        introductionLiveData.value=""
        otherUsersLiveData.value=ArrayList()
        imageLiveData.value = ""
    }
}