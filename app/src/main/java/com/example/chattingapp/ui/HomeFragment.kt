package com.example.chattingapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import aws.smithy.kotlin.runtime.util.length
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.User
import com.example.chattingapp.MainActivity
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



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
        activity?.setTitle("친구")


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        lateinit var resultLauncher: ActivityResultLauncher<Intent>


        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result.resultCode == Activity.RESULT_OK){
                // 어차피 datastore가 업데이트 되면서 observe 함수가 동작하여 viewmodel을 수정하고 ui도 수정될 듯.
//                val name = result.data?.getStringExtra("name") ?: "not found"
//                val introduction = result.data?.getStringExtra("introduction") ?: "not found"
//
//                viewModel.userNameLiveData.value = name
//                viewModel.introductionLiveData.value = introduction
            }
        }


        // 내 정보가 바뀌면 화면 정보를 바꾼다.
        viewModel.userNameLiveData.observe(viewLifecycleOwner){
            binding.myNameTextView.text = it
        }
        viewModel.introductionLiveData.observe(viewLifecycleOwner){
            if(it==null || it==""){
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
            // 내 이름, 내 상태 메시지 수정하는 액티비티로 이동
            val intent = Intent(activity, EditMySimpleProfileActivity::class.java)
            intent.putExtra("email", viewModel.emailLiveData.value)
            intent.putExtra("name", viewModel.userNameLiveData.value)
            intent.putExtra("introduction", viewModel.introductionLiveData.value)
            resultLauncher.launch(intent)

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