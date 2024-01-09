package com.example.chattingapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.MainActivity
import com.example.chattingapp.databinding.RoomRecyclerItemBinding
import java.text.SimpleDateFormat

class RoomListAdapter(private val viewModel: RoomViewModel): RecyclerView.Adapter<RoomListAdapter.ViewHolder>() {
    companion object {
        var myName=""
    }
    inner class ViewHolder(private val binding: RoomRecyclerItemBinding): RecyclerView.ViewHolder(binding.root){
        fun setContents(pos:Int){
            val room = viewModel.roomLiveData.value?.get(pos)

            // 방 이름 구하기 (멤버들 이름으로 구성됨)
            val members = room?.members?.split("\n")
            var room_name=""
            var check = 0
            if (members != null) {
                for(i in 0 until members.size){
                    val name = members[i]
                    // 동명이인이 있을 경우 대비해서, 내 이름은 한 번만 제외.
                    if(name.equals(myName) && check==0){
                        check=1
                    }else{
                        if(i!=members.size-1){
                            room_name+="${name}, "
                            if(members.size==2){
                                room_name=name
                            }
                        }else{
                            room_name+= name
                        }
                    }
                }
            }

            // 마지막 메시지 시간 구하기
            val dataFormat = SimpleDateFormat("yyyy-MM-dd\nhh:mm")
            val time = dataFormat.format(room?.lastMsgTime?.toLong())

            binding.RoomNameTextView.text = room_name
            binding.MsgTextView.text = room?.lastMsg ?: ""
            binding.msgTimeTextView.text = time

            binding.root.setOnClickListener {
                // itemClickEvent 옵저버에게 항목 번호화 클릭되었음을 알림
                viewModel.itemClickEvent.value = pos
            }
        }
    }

    // ViewHolder 생성, ViewHolder는 View를 담는 상자
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RoomRecyclerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에 데이터 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        return viewModel.roomLiveData.value?.size ?: 0
    }
}