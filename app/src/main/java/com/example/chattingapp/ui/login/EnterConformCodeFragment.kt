package com.example.chattingapp.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.amplifyframework.core.Amplify
import com.example.chattingapp.MainActivity
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentEnterConformCodeBinding


class EnterConformCodeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_conform_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEnterConformCodeBinding.bind(view)
       // val viewModel: UserInfoViewModel by activityViewModels()
        val viewModel = ViewModelProvider(requireActivity()).get(UserInfoViewModel::class.java)

        Log.i(TAG,"email: ${viewModel.emailLiveData.value.toString()}")

        binding.conformButton.setOnClickListener {
            val code = binding.conformCodeEditText.text.toString()
            val username = viewModel.emailLiveData.value
            val password = viewModel.passwordLiveData.value
            if(code.isNotEmpty()){
                if (username != null) {
                    Amplify.Auth.confirmSignUp(
                        username, binding.conformCodeEditText.text.toString(),
                        { result ->
                            if (result.isSignUpComplete) {
                                // 로그인 하고 MainActivity로 가기.
                                Amplify.Auth.signIn(username, password,
                                    { result ->
                                        if (result.isSignedIn) {
                                            Log.i("AuthQuickstart", "Sign in succeeded")
                                            startActivity(Intent(activity, MainActivity::class.java))
                                            activity?.supportFragmentManager
                                                ?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                                        } else {
                                            Log.i("AuthQuickstart", "Sign in not complete")
                                        }
                                    },
                                    { Log.e("AuthQuickstart", "Failed to sign in", it) }
                                )
                            } else {
                                activity?.runOnUiThread {
                                    Toast.makeText(context, "인증코드가 유효하지 않습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        { Log.e("AuthQuickstart", "Failed to confirm sign up", it)
                            activity?.runOnUiThread{
                                Toast.makeText(context, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()}
                            }

                    )
                }
            }else{
                activity?.runOnUiThread {
                    Toast.makeText(context, "인증코드를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}