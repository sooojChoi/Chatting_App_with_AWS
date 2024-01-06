package com.example.chattingapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ActivityEditMySimpleProfileBinding
import com.example.chattingapp.databinding.EditIntroductionDialogBinding
import com.example.chattingapp.databinding.EditNameDialogBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import java.lang.IllegalStateException
import android.util.Log
import com.amplifyframework.core.model.query.Where
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditMySimpleProfileActivity : AppCompatActivity() {
    val binding by lazy { ActivityEditMySimpleProfileBinding.inflate(layoutInflater) }
    private val viewModel: UserInfoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        val introduction = intent.getStringExtra("introduction")

        binding.nameTextView.text = name
        binding.introTextView.text = introduction
        if(introduction == null || introduction==""){
            binding.introTextView.text = "소개글을 입력해보세요!"
        }

        viewModel.emailLiveData.value = email
        viewModel.userNameLiveData.value = name
        viewModel.introductionLiveData.value = introduction

        // dialog에서 데이터를 변경하면 activity에서도 바꾸기.
        viewModel.userNameLiveData.observe(this){
            binding.nameTextView.text = it
        }
        viewModel.introductionLiveData.observe(this){
            if(it !=null && it!=""){
                binding.introTextView.text = it
            }else {
                binding.introTextView.text = "소개글을 입력해보세요!"
            }
        }

        binding.EditNameButton.setOnClickListener {
            val nameDialogFragment = EditNameDialogFragment()
            nameDialogFragment.show(supportFragmentManager,"nameDialogFragment")
        }

        binding.EditInroButton.setOnClickListener {
            val introDialogFragment = EditIntroductionDialogFragment()
            introDialogFragment.show(supportFragmentManager,"introDialogFragment")
        }

    }


}

class EditNameDialogFragment: DialogFragment(){
    private val viewModel: UserInfoViewModel by activityViewModels()
    val binding by lazy { EditNameDialogBinding.inflate(layoutInflater) }
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        coroutineScope= CoroutineScope(Dispatchers.Main)

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            binding.nameEditText.setText(viewModel.userNameLiveData.value)

            builder.setView(binding.root)
                .setPositiveButton("확인"
                ) { dialog, id ->
                    coroutineScope.launch {
                        val user = User.builder()
                            .name(binding.nameEditText.text.toString())
                            .id(viewModel.emailLiveData.value)
                            .introduction(viewModel.introductionLiveData.value)
                            .build()

                        Amplify.DataStore.save(user,
                            {
                                Log.i("MyAmplifyApp", "Post updated successfully!")
                                viewModel.userNameLiveData.postValue(binding.nameEditText.text.toString())
                                dialog.dismiss()
                            },
                            {
                                Log.e("MyAmplifyApp", "Could not update post, maybe the title has been changed?", it)
                                // Toast 안내문 띄우기
                                dialog.dismiss()
                            }
                        )
                    }

                }
                .setNegativeButton("취소"
                ) { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    fun getUser(): User?{
        var result:User? = null
        Amplify.DataStore.query(User::class.java,
            Where.matches(User.ID.eq(viewModel.emailLiveData.value)),
            { myInfo ->
                while (myInfo.hasNext()) {
                    val user = myInfo.next()
                    result = user
                }
            },
            { Log.e("MyAmplifyApp", "Query failed", it) }
        )
        return result
    }
}

class EditIntroductionDialogFragment: DialogFragment(){
    val binding by lazy { EditIntroductionDialogBinding.inflate(layoutInflater) }
    private val viewModel: UserInfoViewModel by activityViewModels()
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        coroutineScope= CoroutineScope(Dispatchers.Main)

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            binding.introEditText.setText(viewModel.introductionLiveData.value)

            builder.setView(binding.root)
                .setPositiveButton("확인"
                ) { dialog, it ->
                    // 사용자 소개글 업데이트
                    coroutineScope.launch {
                        val user = User.builder()
                            .name(viewModel.userNameLiveData.value)
                            .id(viewModel.emailLiveData.value)
                            .introduction(binding.introEditText.text.toString())
                            .build()

                        Amplify.DataStore.save(user,
                            {
                                Log.i("MyAmplifyApp", "Post updated successfully!")
                                viewModel.introductionLiveData.postValue(binding.introEditText.text.toString())
                                dialog.dismiss()
                            },
                            {
                                Log.e("MyAmplifyApp", "Could not update post, maybe the title has been changed?", it)
                                // Toast 안내문 띄우기
                                dialog.dismiss()
                            }
                        )
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


