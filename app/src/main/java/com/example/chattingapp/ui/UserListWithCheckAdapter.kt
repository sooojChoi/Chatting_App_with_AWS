package com.example.chattingapp.ui

import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.databinding.UserRecyclerItemWithCheckboxBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import java.util.Base64


data class checkboxData(
    var id: String,
    var name:String,
    var checked: Boolean)

class UserListWithCheckAdapter(private val viewModel: UserInfoViewModel): RecyclerView.Adapter<UserListWithCheckAdapter.ViewHolder>() {
    companion object {
        var checkboxList = arrayListOf<checkboxData>()
    }
    inner class ViewHolder(private val binding: UserRecyclerItemWithCheckboxBinding): RecyclerView.ViewHolder(binding.root){
        @RequiresApi(Build.VERSION_CODES.O)
        fun setContents(pos:Int){
            val user = viewModel.otherUsersLiveData.value?.get(pos)
            binding.nameTextView.text = user?.name
            binding.introTextView.text = user?.introduction

//            binding.root.setOnClickListener {
//                // itemClickEvent 옵저버에게 항목 번호화 클릭되었음을 알림
//                viewModel.itemClickEvent.value = pos
//            }

            // 새로운 아이템이면 checkBoxList에 추가한다.
            if(pos >= checkboxList.size)
                checkboxList.add(pos, checkboxData(user?.email ?: "null", user?.name ?: "null",false))


            // 현재 체크박스리스트에 따라 체크박스 유무를 표시한다.
            binding.checkBox.isChecked = checkboxList[pos].checked

            // 체크박스를 표시하면 체크한다.
            binding.checkBox.setOnClickListener {
                checkboxList[pos].checked = binding.checkBox.isChecked
            }

            if(user?.image != null && user.image != ""){
                val byteArray = Base64.getDecoder().decode(user.image)
                val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
                binding.imageView2.clipToOutline = true
                binding.imageView2.setImageBitmap(bm)
                binding.imageView2.setPadding(0,0,0,0)
            }

        }
    }

    // ViewHolder 생성, ViewHolder는 View를 담는 상자
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserRecyclerItemWithCheckboxBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에 데이터 연결
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        return viewModel.otherUsersLiveData.value?.size ?: 0
    }
}