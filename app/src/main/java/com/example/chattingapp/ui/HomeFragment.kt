package com.example.chattingapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import aws.smithy.kotlin.runtime.util.length
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.User
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentHomeBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import com.example.chattingapp.ui.login.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private val viewModel: UserInfoViewModel by activityViewModels()
    private lateinit var myEmail:String
    private lateinit var myName:String
    lateinit var coroutineScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // viewModel= ViewModelProvider(requireActivity()).get(UserInfoViewModel::class.java)
        // 기기에 저장된 user 정보 가져옴.
        val shared = activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        // 해당 키의 데이터가 없으면 지정한 기본값을 반환한다.
        myEmail = shared?.getString("email", null) ?: "null"
//        myName = shared?.getString("name", null) ?: "이름을 다시 설정하세요."
//        viewModel.emailLiveData.value = myEmail
//        viewModel.userNameLiveData.value = myName

        coroutineScope= CoroutineScope(Dispatchers.Main)

//        coroutineScope.launch {
//            Amplify.DataStore.query(User::class.java,
//                Where.sorted(User.NAME.ascending()),
//                { users ->
//                    while (users.hasNext()) {
//                        Log.i("amplify query","query 하는 중..")
//                        val user = users.next()
//                        // 내 정보이면 text
//                        if(user.id.equals(myEmail)){
//                            viewModel.emailLiveData.value = user.id
//                            viewModel.userNameLiveData.value = user.name
//                            viewModel.introductionLiveData.value = user.introduction
//
//                        }else{
//                            Log.i("amplify query","나 말고 다른 사용자")
//                            val userArray = ArrayList<UserModel>()
//                            userArray.add(UserModel(user.id, user.name, user.introduction))
//                            Log.i("amplify query","${user.id} ${user.name}")
//                            Log.i("amplify query","userArray size: ${userArray.size}")
//                            viewModel.otherUsersLiveData.value = userArray
//                            Log.i("amplify query","view model arr size: ${viewModel.otherUsersLiveData.value!!.size}")
//                        }
//                    }
//                },
//                { Log.e("MyAmplifyApp", "Query failed", it) }
//            )
//        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)

        viewModel.userNameLiveData.observe(viewLifecycleOwner){
            binding.myNameTextView.text = it
        }
        viewModel.introductionLiveData.observe(viewLifecycleOwner){
            if(it==null){
                binding.myIntroTextView.text = "나의 소개글을 입력해보세요!"
            }else {
                binding.myIntroTextView.text = it
            }
        }

        // imageView를 round border로 할 때 border 넘어가는 부분은 잘리도록.
        binding.imageView.clipToOutline = true

        // 내 프로필 클릭됨
        binding.myInfoLayout.setOnClickListener {
            Log.i("myInfoLayout","my profile is clicked")
            // 내 이름, 내 상태 메시지 수정 가능


        }




        viewModel.itemClickEvent.observe(viewLifecycleOwner){
            Log.i("observer","친구 클릭됨. ${it}")
        }


        viewModel.otherUsersLiveData.observe(viewLifecycleOwner){
            val adapter = UserListAdapter(viewModel)
            binding.userListRecyclerView.adapter = adapter
            binding.userListRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.userListRecyclerView.setHasFixedSize(true)
        }







    }
}