package com.example.chattingapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Group
import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.datastore.generated.model.Room
import com.example.chattingapp.MainActivity
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentRoomListBinding
import com.example.chattingapp.databinding.SelectFriendDialogBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.Collections


class RoomListFragment : Fragment(), FragmentManager.OnBackStackChangedListener {
    private val userViewModel: UserInfoViewModel by activityViewModels()
    private val roomViewModel: RoomViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // listen to backstack changes
        activity?.getSupportFragmentManager()?.addOnBackStackChangedListener(this);


    }

    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
        activity?.setTitle("채팅")

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.appbar_item, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId==R.id.make_room){
                    // 친구 목록 보여주는 다이얼로그 뛰움
                    val introDialogFragment = SelectFriendDialog()
                    introDialogFragment.show(parentFragmentManager,"introDialogFragment")
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_list, container, false)




    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRoomListBinding.bind(view)


        // 방이 추가되면 화면에 업데이트
        roomViewModel.roomLiveData.observe(viewLifecycleOwner){
            // last msg time을 기준으로 내림차순 정렬하여 나타냄.
            val room_arr = it ?: ArrayList()
            Collections.sort(room_arr, RoomMsgTimeComparator())

            val adapter = RoomListAdapter(room_arr, roomViewModel)
            binding.roomRecyclerView.adapter = adapter
            binding.roomRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.roomRecyclerView.setHasFixedSize(true)
        }

//        // 방 항목이 클릭되면, 채팅 fragment 띄우기.
        roomViewModel.itemClickEvent.observe(viewLifecycleOwner){
            Log.i("roomlistfragment","chatting fragment로 이동, $it")
            if(it!=-1){
                // 처음에 화면을 초기화할 때 and 채팅 fragment에서 뒤로가기 해서 올 때를 제외하고
                // 방이 정말 선택되었을 때만 이동할 수 있도록.
                findNavController().navigate(R.id.action_roomListFragment_to_chattingFragment)
                roomViewModel.itemClickEvent.value=-1
           }
        }

    }

    override fun onBackStackChanged() {
        if (activity != null) {
            // enable Up button only if there are entries on the backstack
            if (activity?.supportFragmentManager?.backStackEntryCount!! < 1) {
                (activity as MainActivity?)!!.hideUpButton()
            }
        }
    }
}



class SelectFriendDialog: DialogFragment(){
    val binding by lazy { SelectFriendDialogBinding.inflate(layoutInflater) }
    private val userViewModel: UserInfoViewModel by activityViewModels()
   // private val roomViewModel: RoomViewModel by activityViewModels<RoomViewModel>()
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        coroutineScope= CoroutineScope(Dispatchers.Main)

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // binding 으로 view 초기화
            val adapter = UserListWithCheckAdapter(userViewModel)
            binding.recyclerViewUser.adapter = adapter
            binding.recyclerViewUser.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewUser.setHasFixedSize(true)

            val roomViewModel = ViewModelProvider(requireActivity()).get(RoomViewModel::class.java)


            builder.setView(binding.root)
                .setPositiveButton("확인"
                ) { dialog, it ->
                    // 체크표시된 친구들 목록 가지고 room_table, group_table에 항목 추가
                    coroutineScope.launch {
                        // 선택된 user id 목록
                        val checkedUserList = arrayListOf<String>()
                        var members:String =""
                        var ids:String=""
                        UserListWithCheckAdapter.checkboxList.forEach {
                            if(it.checked){
                                checkedUserList.add(it.id)
                                members+="${it.name}\n"
                                ids+="${it.id}\n"
                            }
                        }
                        members+=userViewModel.userNameLiveData.value
                        ids+=userViewModel.emailLiveData.value
                        // dialog를 다시 열었을 때 체크 표시 초기화 되어있도록.
                        UserListWithCheckAdapter.checkboxList.clear()

                        if(checkedUserList.size==0){
                            dialog.cancel()
                        }else{
                            val currentTime = System.currentTimeMillis().toString()
                            // Room table에 항목 추가
                            val roomItem = Room.builder().name(members)
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
                                      //  roomViewModel.currentRoomId.postValue(it.item().id)

                                        // 방이 성공적으로 만들어졌으면 group도 생성
                                        var groupItem = Group.builder().userId(userViewModel.emailLiveData.value)
                                            .roomId(it.item().id).joinTime(currentTime).build()
                                        Amplify.DataStore.save(groupItem,
                                            {
                                                Log.i("MyAmplifyApp", "Created a new group item successfully")
                                            },
                                            {
                                                Log.e("MyAmplifyApp", "Error creating a group item", it)
                                            })
                                        checkedUserList.forEach {
                                            groupItem = Group.builder().userId(it)
                                                .roomId(roomItem.id).joinTime(currentTime).build()
                                            Amplify.DataStore.save(groupItem,
                                                {
                                                    Log.i("MyAmplifyApp", "Created a new group item successfully")
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
                .setNegativeButton("취소"
                ) { dialog, it ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}

class RoomMsgTimeComparator: Comparator<Room>{
    override fun compare(o1: Room?, o2: Room?): Int {
        //lastMsgTime을 기준으로 내림차순 정렬
        if(o1?.lastMsgTime!! > o2?.lastMsgTime!!){
            return -1
        }else{
            return 1
        }
    }
}

class MsgSentTimeComparator: Comparator<Message>{
    override fun compare(o1: Message?, o2: Message?): Int {
        if(o1?.datetime!! > o2?.datetime!!){
            return 1
        }else{
            return -1
        }
    }
}