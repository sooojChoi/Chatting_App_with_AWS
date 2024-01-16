package com.example.chattingapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.input.InputManager
import com.example.chattingapp.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
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
    private lateinit var callback: OnBackPressedCallback

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

    @SuppressLint("ClickableViewAccessibility")
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
            val messageItem = Message.builder().fromId(userViewModel.emailLiveData.value ?: "")
                .text(binding.sendEditText.text.toString())
                .datetime(System.currentTimeMillis().toString())
                .roomId(room.id)
                .type("text")
                .fromName(userViewModel.userNameLiveData.value)
                .build()
            val roomItem = Room.builder().name(room.name)
                .lastMsgTime(time)
                .members(room.members)
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
            roomViewModel.roomLiveData.value = newRoomArr

            // json 타입으로 메시지, 방 정보를 만들어서 웹소켓을 통해 서버로 전송한다.
            val sendingMessage = makeJsonObject(messageItem, roomItem)
            (activity as MainActivity).sendMessage(sendingMessage)
            binding.sendEditText.setText("")
        }

        // message 추가되면 recycler view 업데이트.
        messageViewModel.msgLiveData.observe(viewLifecycleOwner){
            Log.i("message","msg livedata observer 호출됨, ${room.id}")
            // 현재 방의 메시지만 가져온다.
            val msgForMyRoom = ArrayList<Message>()
            it.forEach {
                msg ->
                if(msg.roomId == room.id){
                    msgForMyRoom.add(msg)
                }
            }
            val adapter = MessageListAdapter(msgForMyRoom)
            binding.chattingRecyclerView.adapter = adapter
            binding.chattingRecyclerView.layoutManager = LinearLayoutManager(context).apply {
                this.stackFromEnd = false  // 가장 최근의 대화를 표시하기 위해 맨 아래로 스크롤 하는 것을 false
                this.reverseLayout = false  // data들을 반대로 쌓아올리는 것을 false
            }
            binding.chattingRecyclerView.setHasFixedSize(false)  // 각 item 크기가 바뀔 수 있음.
            binding.chattingRecyclerView.scrollToPosition(msgForMyRoom.size-1)  // 마지막 item으로 스크롤(아래로 스크롤)
            binding.chattingRecyclerView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                // 키보드가 올라오면 감지해서 마지막 item 보여지도록.
                binding.chattingRecyclerView.scrollToPosition(msgForMyRoom.size-1)
            }

        }


        binding.sendButton.isEnabled = false
        // text를 입력하지 않으면 메시지 전송 버튼이 비활성화됨.
        binding.sendEditText.doOnTextChanged { text, start, before, count ->
            binding.sendButton.isEnabled = !(text.toString().equals(""))
        }

        binding.chattingRecyclerView.setOnTouchListener { v, event ->
            // 화면 터치시 키보드 내려가도록. 채팅 전송 버튼을 눌렀을 때는 키보드가 내려가지 않도록.
            if(event.action == MotionEvent.ACTION_DOWN){
                if(activity != null && activity?.currentFocus != null){
                    val imm: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)

                    if(activity?.currentFocus is EditText) {
                        activity?.currentFocus!!.clearFocus()
                    }
                }
            }
            true
        }
        

    }

    fun makeJsonObject(msg:Message, room:Room):JSONObject{
        return JSONObject()
            .put("action", "sendmessage")
            .put("msgId",msg.id)
            .put("fromId",msg.fromId)
            .put("text",msg.text)
            .put("msgDateTime", msg.datetime)
            .put("msgRoomId",msg.roomId)
            .put("type",msg.type)
            .put("fromName",msg.fromName)
            .put("roomId",room.id)
            .put("lastMsgTime",room.lastMsgTime)
            .put("lastMsg",room.lastMsg)
            .put("roomName",room.name)
            .put("members",room.members)
    }


    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
        activity?.setTitle(getRoomName())

        // toolbar의 back button이 나타나게 함.
        (activity as MainActivity).showUpButton()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 하단의 뒤로가기 버튼을 누르면,
                // tool bar의 뒤로가기 버튼을 눌렀을 때와 동일하게 동작한다.
                parentFragmentManager.popBackStack()
                (activity as MainActivity).hideUpButton()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
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

//    fun hideKeyboard(){
//        if(activity != null && requireActivity().currentFocus !=null){
//            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
//        }
//    }


}