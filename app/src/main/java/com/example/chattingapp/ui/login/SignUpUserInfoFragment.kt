package com.example.chattingapp.ui.login

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.example.chattingapp.R
import com.example.chattingapp.viewModel.UserInfoViewModel
import com.example.chattingapp.databinding.FragmentSignUpUserInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException

class SignUpUserInfoFragment : Fragment() {
    lateinit var viewModel: UserInfoViewModel
    lateinit var coroutineScope: CoroutineScope
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coroutineScope= CoroutineScope(Dispatchers.Main)
        viewModel=ViewModelProvider(requireActivity()).get(UserInfoViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSignUpUserInfoBinding.bind(view)
        //val viewModel: UserInfoViewModel by activityViewModels()


        binding.signUpButton.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString()
            val username = binding.nameEditText.text.toString()
            val password = binding.editTextPassword.text.toString()
            signUp(email, username, password)
        }

        binding.goToConform.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString()
            val username = binding.nameEditText.text.toString()
            val password = binding.editTextPassword.text.toString()
            signUp(email, username, password)

        }
    }

    fun signUp(email:String, username:String, password:String){
        if(isValidData(email, username, password)){
            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .userAttribute(AuthUserAttributeKey.name(), username)

                .build()
            Amplify.Auth.signUp(email, password, options,
                { Log.i("AuthQuickStart", "Sign up succeeded: $it")

                    coroutineScope.launch {
                        try {
                            viewModel.emailLiveData.value = email
                            viewModel.userNameLiveData.value = username
                            viewModel.passwordLiveData.value = password
                        }catch (e: InvocationTargetException){
                            Log.i(TAG, e.cause.toString())

                        }
                        // dynamoDB에 회원 정보를 저장한다.
                        storeUserInfoDynamoDB(email, username ?: "이름을 다시 설정해주세요.", "")
                        // 코드를 인증하는 화면으로 이동한다.
                        findNavController().navigate(R.id.action_signUpUserInfoFragment_to_enterConformCodeFragment)
                    }

                },
                { Log.e ("AuthQuickStart", "Sign up failed", it)
                    if(it.toString().contains("UsernameExistsException")){
                        showLoginFailed("회원가입에 실패하였습니다. 이미 존재하는 이메일입니다.")
                    }
                }
            )
        }
    }

    // 이메일, 비밀번호가 유효한 형식인지 확인하는 함수
    fun isValidData(email: String, username:String, password: String): Boolean{
        if (!isEmailValid(email)) {
            showLoginFailed("이메일 형식이 유효하지 않습니다.")
        } else if (!isPasswordValid(password)) {
            showLoginFailed("비밀번호 형식이 유효하지 않습니다.")
        } else if(username.isEmpty()){
            showLoginFailed("사용자 이름을 입력해주세요.")
        }
        else {
            return true
        }
        return false
    }
    private fun showLoginFailed(errorString: String) {
        activity?.runOnUiThread {
            Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
        }

    }


    // A placeholder username validation check
    private fun isEmailValid(username: String): Boolean {
        Log.i(TAG,"이메일 형식: $username")
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            false
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {

        val digitAndStr = password.any { it.isDigit() }
                && password.any{it.isUpperCase() }
                && password.any{it.isLowerCase() }
        val regex = Regex("[^a-zA-Z0-9]")
        val specialChar = regex.containsMatchIn(password)
        return password.length >= 8 && digitAndStr && specialChar
    }

    // userInfo를 dynamoDB에 저장한다.
    fun storeUserInfoDynamoDB(email:String, name:String, introduction:String){
        val item = User.builder().name(name).id(email).introduction(introduction).build()
        try{
            Amplify.DataStore.save(item,
                { Log.i("MyAmplifyApp", "Created a new post successfully") },
                { Log.e("MyAmplifyApp", "Error creating post", it) }
            )
        }catch (e: Exception){
            Log.i("MyAmplifyApp","error: $e")
        }
    }


}