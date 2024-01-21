package com.example.chattingapp.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Group
import com.amplifyframework.datastore.generated.model.Room
import com.example.chattingapp.MainActivity
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentUserProfileBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Base64
import java.util.Collections


class UserProfileFragment : Fragment() {
    private val userViewModel: UserInfoViewModel by activityViewModels()
    private val roomViewModel: RoomViewModel by activityViewModels()
    private lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 기기에 저장된 user 정보 가져옴.
        val shared = activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        // 해당 키의 데이터가 없으면 지정한 기본값을 반환한다.
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentUserProfileBinding.bind(view)


        val clickedIndex = userViewModel.itemClickEvent.value ?: 0
        userViewModel.itemClickEvent.value = -1
        val user = userViewModel.otherUsersLiveData.value?.get(clickedIndex)
        if(user!=null){
            binding.nameTextView.text = user.name
            binding.introTextView.text = user.introduction ?: ""
            if(user.image !=null && user.image!=""){
                val byteArray = Base64.getDecoder().decode(user.image)
                val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
                binding.profileImageView.clipToOutline = true
                binding.profileImageView.setImageBitmap(bm)
                binding.profileImageView.setPadding(0,0,0,0)
            }
        }

        binding.closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.chattingButton.setOnClickListener {
            // 방이 있는지 확인 후, 있으면 해당 방으로 이동, 없으면 만들고 이동
            var existRoom = 0
            roomViewModel.roomLiveData.value?.forEach {
            room->
                if (room.members.contains(user?.email.toString())) {
                    // 해당 방으로 이동
                    roomViewModel.currentRoomId.postValue(room.id)
                    findNavController().navigate(R.id.action_userProfileFragment_to_chattingFragment)
                    userViewModel.itemClickEvent.postValue(clickedIndex)
                    existRoom=1
                }
            }
            // 방이 없으면 만들고 이동
            if(existRoom==0){
                val coroutineScope = CoroutineScope(Dispatchers.Main)

                    val name = userViewModel.userNameLiveData.value+"\n"+user?.name
                    val ids = userViewModel.emailLiveData.value+"\n"+user?.email

                    val currentTime = System.currentTimeMillis().toString()
                    // Room table에 항목 추가
                    val roomItem = Room.builder().name(name)
                        .lastMsgTime(currentTime).members(ids).build()

                    try{
                        Amplify.DataStore.save(roomItem,
                            { it ->
                                Log.i("MyAmplifyApp", "Created a new room successfully")
                                //view model에도 추가
                                val room_arr = roomViewModel.roomLiveData.value ?: arrayListOf()
                                room_arr.add(it.item())
                                Collections.sort(room_arr, RoomMsgTimeComparator())

                                roomViewModel.roomLiveData.postValue(room_arr)
                                roomViewModel.currentRoomId.postValue(roomItem.id)
                                coroutineScope.launch {
                                    findNavController().navigate(R.id.action_userProfileFragment_to_chattingFragment)
                                    userViewModel.itemClickEvent.postValue(clickedIndex)
                                    existRoom=1

                                    // 방이 성공적으로 만들어졌으면 group도 생성
                                    var groupItem = Group.builder().userId(userViewModel.emailLiveData.value)
                                        .roomId(it.item().id).joinTime(currentTime).build()
                                    Amplify.DataStore.save(groupItem,
                                        {
                                            Log.i("MyAmplifyApp", "Created a new group item successfully")
                                            groupItem = Group.builder().userId(user?.email)
                                                .roomId(roomItem.id).joinTime(currentTime).build()
                                            Amplify.DataStore.save(groupItem,
                                                {
                                                    Log.i("MyAmplifyApp", "Created a new group item successfully")
                                                },
                                                {
                                                    Log.e("MyAmplifyApp", "Error creating a group item", it)
                                                })
                                        },
                                        {
                                            Log.e("MyAmplifyApp", "Error creating a group item", it)
                                        })
                                }


                            },
                            { Log.e("MyAmplifyApp", "Error creating a room", it) }
                        )
                    }catch (e: Exception){
                        Log.i("MyAmplifyApp","error: $e")
                    }
                }

        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 하단의 뒤로가기 버튼을 누르면,
                // tool bar의 뒤로가기 버튼을 눌렀을 때와 동일하게 동작한다.
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

}