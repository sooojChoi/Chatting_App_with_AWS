package com.example.chattingapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.databinding.UserRecyclerItemBinding
import com.example.chattingapp.ui.login.UserInfoViewModel

class UserListAdapter(private val viewModel: UserInfoViewModel): RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: UserRecyclerItemBinding): RecyclerView.ViewHolder(binding.root){
        fun setContents(pos:Int){
            val user = viewModel.otherUsersLiveData.value?.get(pos)
            binding.nameTextView.text = user?.name
            binding.introTextView.text = user?.introduction

            binding.root.setOnClickListener {
                // itemClickEvent 옵저버에게 항목 번호화 클릭되었음을 알림
                viewModel.itemClickEvent.value = pos
            }
        }
    }

    // ViewHolder 생성, ViewHolder는 View를 담는 상자
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserRecyclerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에 데이터 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        return viewModel.otherUsersLiveData.value?.size ?: 0
    }
}