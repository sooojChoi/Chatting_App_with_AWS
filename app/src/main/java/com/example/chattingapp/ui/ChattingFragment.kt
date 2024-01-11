package com.example.chattingapp.ui

import com.example.chattingapp.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.datastore.generated.model.Room
import com.example.chattingapp.MainActivity
import com.example.chattingapp.databinding.FragmentChattingBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject


class ChattingFragment : Fragment() {
    private val userViewModel: UserInfoViewModel by activityViewModels()
    private val roomViewModel: RoomViewModel by activityViewModels()
    private val messageViewModel:MessageViewModel by activityViewModels()
    lateinit var room:Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 현재 방 객체 구하기
        roomViewModel.roomLiveData.value?.forEach {
            if(it.id==roomViewModel.currentRoomId.value){
                Log.i("room id test","chattingFragment, room id: ${it.id}")
                room = it
                // 지금 방의 메시지 데이터를 가져온 적 없다면 dynamodb에서 가져오기
                if(!(messageViewModel.roomIdWithMessage.value ?: ArrayList<String>()).contains(it.id)){
                    (activity as MainActivity).getMessageFromDynamoDB(it.id)
                }
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

        // 전송 버튼 눌리면
        binding.sendButton.setOnClickListener {
            // 1. viewmodel에 메시지 객체 저장, room 객체 수정
            // 2. websocket을 통해 서버로 전송
            // 3. aws lambda 에서 dynamodb에 저장하고, group member들에게 전송
            // 메시지 받을 때는, viewmodel에 message 저장, room 객체 수정

            val time = System.currentTimeMillis().toString()
            val messageItem = Message.builder().fromId(userViewModel.emailLiveData.value)
                .text(binding.sendEditText.text.toString())
                .datetime(System.currentTimeMillis().toString())
                .roomId(room.id)
                .type("text")
                .fromName(userViewModel.userNameLiveData.value)
                .build()
            val roomItem = Room.builder().name(room.name)
                .lastMsgTime(time)
                .lastMsg(binding.sendEditText.text.toString())
                .id(room.id)
                .build()

            // message viewModel에 데이터 추가
            val msgArr = messageViewModel.msgLiveData.value ?: ArrayList<Message>()
            msgArr.add(messageItem)
            messageViewModel.msgLiveData.value = msgArr

            // room viewModel의 데이터 수정
            val roomArr = roomViewModel.roomLiveData.value!!
            val newRoomArr = ArrayList<Room>()
            roomArr.forEach {
                if(it.id == room.id){
                    newRoomArr.add(roomItem)
                }else{
                    newRoomArr.add(it)
                }
            }
            roomViewModel.roomLiveData.value = roomArr

            // json 타입으로 메시지, 방 정보를 만들어서 웹소켓을 통해 서버로 전송한다.
            val sendingMessage = makeJsonObject(messageItem, roomItem)
            (activity as MainActivity).sendMessage(sendingMessage)
            binding.sendEditText.setText("")
        }

        // message 추가되면 recycler view 업데이트.
        messageViewModel.msgLiveData.observe(viewLifecycleOwner){
            // 현재 방의 메시지만 가져온다.
            val msgForMyRoom = ArrayList<Message>()
            messageViewModel.msgLiveData.value?.forEach {
                if(it.roomId == room.id){
                    msgForMyRoom.add(it)
                }
            }
            val adapter = MessageListAdapter(msgForMyRoom)
            binding.chattingRecyclerView.adapter = adapter
            binding.chattingRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.chattingRecyclerView.setHasFixedSize(true)
        }

    }

    fun makeJsonObject(msg:Message, room:Room):JSONObject{
        return JSONObject()
            .put("action", "sendmessage")
            .put("fromId",msg.fromId)
            .put("text",msg.text)
            .put("msgRoomId",msg.roomId)
            .put("type",msg.type)
            .put("fromName",msg.fromName)
            .put("roomId",room.id)
            .put("lastMsgTime",room.lastMsgTime)
            .put("lastMsg",room.lastMsg)
            .put("roomName",room.name)
    }


    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
        activity?.setTitle(getRoomName())

        // toolbar의 back button이 나타나게 함.
        (activity as MainActivity).showUpButton()
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
                // 동명이인이 있을 경우가 있으니 내 이름은 한 번만 제외.
                if(name.equals(RoomListAdapter.myName) && check==0){
                    check=1
                }else{
                    // 마지막 순서이거나, 나보다 하나 남았는데 그게 내 이름일 때
                    if(i==members.size-1 ||(i==members.size-2 &&check==0)){
                        room_name+=name
                    }
                    // 위에 속하지 않는 순서일 때
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