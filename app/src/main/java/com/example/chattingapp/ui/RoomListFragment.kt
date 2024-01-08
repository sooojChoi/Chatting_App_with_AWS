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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Room
import com.amplifyframework.datastore.generated.model.User
import com.example.chattingapp.R
import com.example.chattingapp.databinding.EditIntroductionDialogBinding
import com.example.chattingapp.databinding.FragmentRoomListBinding
import com.example.chattingapp.databinding.SelectFriendDialogBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException


/**
 * A simple [Fragment] subclass.
 * Use the [RoomListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RoomListFragment : Fragment() {
    private val viewModel: UserInfoViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



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


    }
}



class SelectFriendDialog: DialogFragment(){
    val binding by lazy { SelectFriendDialogBinding.inflate(layoutInflater) }
    private val viewModel: UserInfoViewModel by activityViewModels()
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        coroutineScope= CoroutineScope(Dispatchers.Main)

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // binding 으로 view 초기화
            val adapter = UserListWithCheckAdapter(viewModel)
            binding.recyclerViewUser.adapter = adapter
            binding.recyclerViewUser.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewUser.setHasFixedSize(true)


            builder.setView(binding.root)
                .setPositiveButton("확인"
                ) { dialog, it ->
                    // 체크표시된 친구들 목록 가지고 room_table, group_table에 항목 추가
                    coroutineScope.launch {
                        // 선택된 user id 목록
                        val checkedUserList = arrayListOf<String>()
                        //var roomName:String =""
                        UserListWithCheckAdapter.checkboxList.forEach {
                            if(it.checked){
                                checkedUserList.add(it.id)
                            }
                        }
                        // dialog를 다시 열었을 때 체크 표시 초기화 되어있도록.
                        UserListWithCheckAdapter.checkboxList.clear()

                        if(checkedUserList.size==0){
                            dialog.cancel()
                        }else{
//                            // Room table에 항목 추가
//                            val item = User.builder().name(name).id(email).introduction(introduction).build()
//                            val roomItem = Room.builder().name(viewModel.emailLiveData.value)
//                                .lastMsgTime()
//                            try{
//                                Amplify.DataStore.save(item,
//                                    { Log.i("MyAmplifyApp", "Created a new post successfully") },
//                                    { Log.e("MyAmplifyApp", "Error creating post", it) }
//                                )
//                            }catch (e: Exception){
//                                Log.i("MyAmplifyApp","error: $e")
//                            }

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