package com.example.chattingapp.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Room
import com.example.chattingapp.MainActivity
import com.example.chattingapp.databinding.RoomRecyclerItemBinding
import java.text.SimpleDateFormat

class RoomListAdapter(private val rooms: ArrayList<Room>, private val viewModel:RoomViewModel): RecyclerView.Adapter<RoomListAdapter.ViewHolder>() {
    companion object {
        var myName=""
    }
    inner class ViewHolder(private val binding: RoomRecyclerItemBinding): RecyclerView.ViewHolder(binding.root){
        fun setContents(pos:Int){
            val room = rooms.get(pos)

            // 방 이름 구하기 (멤버들 이름으로 구성됨)
            val members = room?.name?.split("\n")
            var room_name=""
            var check = 0
            if (members != null) {
                Log.i("roomListname","${members.size}")
                for(i in 0 until members.size){
                    val name = members[i]
                    // 동명이인이 있을 경우 대비해서, 내 이름은 한 번만 제외.
                    if(name.equals(myName) && check==0){
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

            // 마지막 메시지 시간 구하기
            val dataFormat = SimpleDateFormat("yyyy년 MM월 dd일\nhh:mm")
            val time = dataFormat.format(room?.lastMsgTime?.toLong())

            binding.RoomNameTextView.text = room_name
            binding.MsgTextView.text = room?.lastMsg ?: "대화를 시작해보세요!"
            binding.msgTimeTextView.text = time

            binding.root.setOnClickListener {
                // itemClickEvent 옵저버에게 항목 번호화 클릭되었음을 알림
                viewModel.itemClickEvent.value = pos
                // 클릭된 방의 id를 넘겨줌.
                viewModel.currentRoomId.value = room?.id
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
        return rooms.size ?: 0
    }
}