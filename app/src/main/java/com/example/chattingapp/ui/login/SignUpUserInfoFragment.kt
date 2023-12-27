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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentSignUpUserInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException

class SignUpUserInfoFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val viewModel = ViewModelProvider(requireActivity()).get(UserInfoViewModel::class.java)
        val coroutineScope by lazy { CoroutineScope(Dispatchers.Main) }

        val email = binding.editTextEmailAddress
        val username = binding.nameEditText
        val password = binding.editTextPassword

        binding.signUpButton.setOnClickListener {
            val emailText = email.text.toString()
            Log.i(TAG,"emailText: $emailText")
            val passwordText = password.text.toString()

            if(isValidData(emailText, passwordText)){
                val options = AuthSignUpOptions.builder()
                    .userAttribute(AuthUserAttributeKey.email(), email.text.toString())
                    .build()
                Amplify.Auth.signUp(emailText, passwordText, options,
                    { Log.i("AuthQuickStart", "Sign up succeeded: $it")

                        coroutineScope.launch {
                            try {
                                viewModel.emailLiveData.value = emailText
                                viewModel.userNameLiveData.value = username.text.toString()
                                viewModel.passwordLiveData.value = passwordText
                            }catch (e: InvocationTargetException){
                                Log.i(TAG, e.cause.toString())

                            }
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
    }

    // 이메일, 비밀번호가 유효한 형식인지 확인하는 함수
    fun isValidData(email: String, password: String): Boolean{
        if (!isUserNameValid(email)) {
            showLoginFailed("이메일 형식이 유효하지 않습니다.")
        } else if (!isPasswordValid(password)) {
            showLoginFailed("비밀번호 형식이 유효하지 않습니다.")
        } else {
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
    private fun isUserNameValid(username: String): Boolean {
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


}