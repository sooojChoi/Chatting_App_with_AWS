package com.example.chattingapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.databinding.FragmentHomeBinding
import com.example.chattingapp.viewModel.UserInfoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Base64


class HomeFragment : Fragment(){
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        lateinit var resultLauncher: ActivityResultLauncher<Intent>


        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result.resultCode == Activity.RESULT_OK){
                val image = result.data?.getStringExtra("image")
                if(image != null){
                    Log.i("TAG",image)
                }
                viewModel.imageLiveData.value = image
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
            if(binding.progressBar.visibility == View.VISIBLE && it!="" && it!=null){
                binding.myInfoLayout.visibility = View.VISIBLE
                binding.friendsLayout.visibility = View.VISIBLE
                binding.lineImageView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
        viewModel.introductionLiveData.observe(viewLifecycleOwner){
            if(it==null || it==""){
                binding.myIntroTextView.text = "나의 소개글을 입력해보세요!"
            }else {
                binding.myIntroTextView.text = it
            }
        }
        viewModel.imageLiveData.observe(viewLifecycleOwner){
            if(it!=null && it!=""){
                val byteArray = Base64.getDecoder().decode(it)
                val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
                binding.imageView.clipToOutline = true
                binding.imageView.setImageBitmap(bm)
                binding.imageView.setPadding(0,0,0,0)
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
            if(viewModel.imageLiveData.value != null){
                intent.putExtra("image",viewModel.imageLiveData.value)
            }
            resultLauncher.launch(intent)

        }

        viewModel.itemClickEvent.observe(viewLifecycleOwner){
            Log.i("observer","친구 클릭됨. ${it}")
            // 새로운 프래그먼트 띄운다 가운데에 프로필 사진, 이름, 소개글.
            // 프로필 사진 누르면 크게 볼 수 있다
            // 하단에 채팅하기 버튼이 있어서 누르면 바로 채팅방으로 이동한다.
            // roomViewModel에서, 얘랑 나랑 둘만이 member로 있는 방을 찾는다.
            // 해당 방이 있다면 그 방으로 가고, 없다면 새로운 방 생성
            // currentRoomId,
            if(it!=-1 && it!=null){
                findNavController().navigate(R.id.action_homeFragment_to_userProfileFragment)
            }

        }


        viewModel.otherUsersLiveData.observe(viewLifecycleOwner){
            val adapter = UserListAdapter(viewModel)
            binding.userListRecyclerView.adapter = adapter
            binding.userListRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.userListRecyclerView.setHasFixedSize(true)
        }

    }


}