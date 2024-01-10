package com.example.chattingapp.ui

import com.example.chattingapp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.amplifyframework.datastore.generated.model.Room
import com.example.chattingapp.databinding.FragmentChattingBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class ChattingFragment : Fragment() {
    private val userViewModel: UserInfoViewModel by activityViewModels()
    private val roomViewModel: RoomViewModel by activityViewModels()
    lateinit var room:Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 현재 방 객체 구하기
        roomViewModel.roomLiveData.value?.forEach {
            if(it.id==roomViewModel.currentRoomId.value){
                room = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        hideBottomNavigation(true)
        return inflater.inflate(R.layout.fragment_chatting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChattingBinding.bind(view)

        binding.sendEditText.setText(roomViewModel.currentRoomId.value.toString())
    }

    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
        activity?.setTitle(getRoomName())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // bottom navigation bar를 다시 보이게 함.
        hideBottomNavigation(false)
    }

    fun getRoomName():String{
        // 방 이름 구하기 (멤버들 이름으로 구성됨)
        val members = room.name?.split("\n")
        var room_name=""
        var check = 0
        if (members != null) {
            for(i in 0 until members.size){
                val name = members[i]
                // 동명이인이 있을 경우 대비해서, 내 이름은 한 번만 제외.
                if(name.equals(RoomListAdapter.myName) && check==0){
                    check=1
                }else{
                    // 마지막 순서이거나, 나보다 하나 남았는데 그게 내 이름일 때
                    if(i==members.size-1 ||(i==members.size-2 &&check==0)){
                        room_name+=name
                    }
                    // 일반적인 순서일 때
                    else{
                        room_name+="${name}, "
                    }

                }
            }
        }

        return room_name
    }

    fun hideBottomNavigation(bool: Boolean) {
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
        if (bool) bottomNavigation.visibility = View.GONE
        else bottomNavigation.visibility = View.VISIBLE
    }



}