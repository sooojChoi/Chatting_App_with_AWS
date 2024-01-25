package com.example.chattingapp.ui.login

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amplifyframework.core.Amplify
import com.example.chattingapp.R
import com.example.chattingapp.viewModel.UserInfoViewModel
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
            val email = viewModel.emailLiveData.value
            val password = viewModel.passwordLiveData.value
            val name = viewModel.userNameLiveData.value

            if(code.isNotEmpty()){
                if (email != null) {
                    // email로 전송된 code로 confirm한다.
                    Amplify.Auth.confirmSignUp(
                        email, code,
                        { result ->
                            if (result.isSignUpComplete) {
                                // 로그인 하고 MainActivity로 가기.
                                Amplify.Auth.signIn(email, password,
                                    { result ->
                                        if (result.isSignedIn) {
                                            Log.i("AuthQuickstart", "Sign in succeeded")
                                            // device에 email, name 정보를 저장한다.
                                            storeUserId(email, name ?: "이름을 다시 설정해주세요.")
                                            // 이 액티비티를 제거하고 Main 화면으로 이동한다.
                                            (activity as SignUpActivity).goToMainAcitivity()
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

    // user의 email을 내부저장소에 XML 파일로 저장한다.
    fun storeUserId(email:String, name:String){
        // 첫 번째 인자는 파일명, 두 번째 인자는 파일 접근 권한(보안상의 이유로 MODE_PRIVATE만 가능)
        val shared = activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val editor = shared?.edit()
        editor?.putString("email",email)
        editor?.putString("name",name)
        editor?.apply()
    }

}