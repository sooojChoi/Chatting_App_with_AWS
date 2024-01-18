package com.example.chattingapp.ui

import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Message
import com.example.chattingapp.databinding.DatetimeItemBinding
import com.example.chattingapp.databinding.MessageMyItemBinding
import com.example.chattingapp.databinding.MessageMyPictureItemBinding
import com.example.chattingapp.databinding.MessageYourItemBinding
import com.example.chattingapp.databinding.MessageYourPictureItemBinding
import java.text.SimpleDateFormat
import java.util.Base64


class YourChatViewHolder(val binding: MessageYourItemBinding) : RecyclerView.ViewHolder(binding.root)
class MyChatViewHolder(val binding: MessageMyItemBinding) :  RecyclerView.ViewHolder(binding.root)
class MyPictureViewHolder(val binding: MessageMyPictureItemBinding): RecyclerView.ViewHolder(binding.root)
class YourPictureViewHolder(val binding: MessageYourPictureItemBinding): RecyclerView.ViewHolder(binding.root)
class DateItemViewHolder(val binding: DatetimeItemBinding): RecyclerView.ViewHolder(binding.root)
class MessageListAdapter(private val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        var myEmail=""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var viewHolder: RecyclerView.ViewHolder
        return when(viewType){
            0->{
                // 내가 보낸 일반 text 메시지
                val inflater = LayoutInflater.from(parent.context)
                val binding = MessageMyItemBinding.inflate(inflater, parent, false)
                MyChatViewHolder(binding)
            }
            1->{
                // 내가 보낸 picture 메시지
                val inflater = LayoutInflater.from(parent.context)
                val binding = MessageMyPictureItemBinding.inflate(inflater, parent, false)
                MyPictureViewHolder(binding)
            }
            2->{
                // 날짜를 나타내는 item
                val inflater = LayoutInflater.from(parent.context)
                val binding = DatetimeItemBinding.inflate(inflater, parent, false)
                DateItemViewHolder(binding)
            }
            3->{
                // 다른 사람이 보낸 일반 text 메시지
                val inflater = LayoutInflater.from(parent.context)
                val binding = MessageYourItemBinding.inflate(inflater, parent, false)
                YourChatViewHolder(binding)
            }
            4->{
                val inflater = LayoutInflater.from(parent.context)
                val binding = MessageYourPictureItemBinding.inflate(inflater, parent, false)
                YourPictureViewHolder(binding)
            }
            else -> {
                viewHolder
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messageList.get(position)
        when(holder){
            is MyChatViewHolder -> {
                holder.binding.myMsgTextView.text = msg?.text ?: ""
                val dataFormat = SimpleDateFormat("hh:mm")
                val time = dataFormat.format(msg?.datetime?.toLong())
                holder.binding.timeTextView.text = time ?: ""
            }
            is YourChatViewHolder -> {
                holder.binding.yourNameTextView.text = msg?.fromName ?: ""
                holder.binding.yourMsgTextView.text = msg?.text ?: ""
                val dataFormat = SimpleDateFormat("hh:mm")
                val time = dataFormat.format(msg?.datetime?.toLong())
                holder.binding.timeTextView.text = time ?: ""
            }
            is MyPictureViewHolder -> {
                val dataFormat = SimpleDateFormat("hh:mm")
                val time = dataFormat.format(msg?.datetime?.toLong())
                holder.binding.timeTextView.text = time ?: ""

                val byteArray = Base64.getDecoder().decode(msg?.text)
                val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
                holder.binding.myPictureImageView.setImageBitmap(bm)
                holder.binding.myPictureImageView.clipToOutline = true
            }
            is YourPictureViewHolder -> {
                holder.binding.yourNameTextView.text = msg?.fromName ?: ""
                val dataFormat = SimpleDateFormat("hh:mm")
                val time = dataFormat.format(msg?.datetime?.toLong())
                holder.binding.timeTextView.text = time ?: ""

                val byteArray = Base64.getDecoder().decode(msg?.text)
                val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
                holder.binding.yourPictureImageView.setImageBitmap(bm)
                holder.binding.yourPictureImageView.clipToOutline = true
            }
            is DateItemViewHolder -> {
                val dataFormat = SimpleDateFormat("yyyy년 MM월 dd일")
                val time = dataFormat.format(msg.datetime?.toLong())
                holder.binding.dateTimeTextView.text = time
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val msg = messageList.get(position)
        // 내가 보낸 메시지
        return if(msg?.fromId == myEmail){
            if(msg.type == "text"){
                0
            }else if(msg.type == "picture"){
                1
            }else{
                // msg.type == datetime
                2
            }
        }
        // 다른 사람이 보낸 메시지
        else{
            if(msg?.type == "text"){
                3
            }else if(msg.type == "picture"){
                4
            }else{
                // msg.type == datetime
                2
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size ?: 0
    }

}