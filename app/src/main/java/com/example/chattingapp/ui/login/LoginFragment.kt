package com.example.chattingapp.ui.login

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.amplifyframework.core.Amplify
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)
        val coroutineScope by lazy { CoroutineScope(Dispatchers.Main) }

        val email = binding.emailTextView
        val password = binding.passwordTextView
        val loginButton = binding.loginButton
        val signUpButton = binding.signUpButton
        val loading = binding.loading


        // 로그인 버튼 눌림
        loginButton.setOnClickListener {
            // 이메일이 올바른 형식이면 로그인 시도
            if(isEmailValid(email.text.toString())){
                loading.visibility = View.VISIBLE
                login(email.text.toString(), password.text.toString())
            }

        }

        // 회원가입 버튼 눌림
        signUpButton.setOnClickListener {
            // 회원가입 화면으로 이동
            coroutineScope.launch {
                findNavController().navigate(R.id.action_loginFragment_to_signUpUserInfoFragment)
            }
        }
    }

    private fun login(username: String, password:String){
        Amplify.Auth.signIn(username, password,
            { result ->
                if (result.isSignedIn) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                    (activity as SignUpActivity).goToMainAcitivity()
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                    activity?.runOnUiThread {
                        Toast.makeText(context, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            { Log.e("AuthQuickstart", "Failed to sign in", it) }
        )    }
//
//    private fun updateUiWithUser() {
//        val welcome = getString(R.string.welcome)
//        // TODO : initiate successful logged in experience
//        Toast.makeText(
//            context,
//            "$welcome 회원가입이 완료되었습니다.",
//            Toast.LENGTH_LONG
//        ).show()
//    }

    private fun showLoginFailed(errorString: String) {
        activity?.runOnUiThread {
            Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            false
        }
    }


}