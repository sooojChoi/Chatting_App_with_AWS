package com.example.chattingapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Message
import com.example.chattingapp.databinding.MessageMyItemBinding
import com.example.chattingapp.databinding.MessageYourItemBinding
import java.text.SimpleDateFormat


class YourChatViewHolder(val binding: MessageYourItemBinding) : RecyclerView.ViewHolder(binding.root)
class MyChatViewHolder(val binding: MessageMyItemBinding) :  RecyclerView.ViewHolder(binding.root)
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
            3->{
                // 다른 사람이 보낸 일반 text 메시지
                val inflater = LayoutInflater.from(parent.context)
                val binding = MessageYourItemBinding.inflate(inflater, parent, false)
                YourChatViewHolder(binding)
            }

            else -> {
                viewHolder
            }
        }

    }

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
        }

    }

    override fun getItemViewType(position: Int): Int {
        val msg = messageList.get(position)
        // 내가 보낸 메시지
        return if(msg?.fromId == myEmail){
            if(msg.type == "text"){
                0
            }else{
                1
            }
        }
        // 다른 사람이 보낸 메시지
        else{
            if(msg?.type == "text"){
                3
            }else{
                4
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size ?: 0
    }

}