package com.example.chattingapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.core.Amplify
import com.example.chattingapp.MainActivity
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentLastPageBinding

class LastPageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 변수 초기화

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_last_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLastPageBinding.bind(view)

        // 로그아웃
        binding.logoutButton.setOnClickListener {
                        Amplify.Auth.signOut { signOutResult ->
                when(signOutResult) {
                    is AWSCognitoAuthSignOutResult.CompleteSignOut -> {
                        // Sign Out completed fully and without errors.
                        Log.i("AuthQuickStart", "Signed out successfully")
                        activity?.runOnUiThread {
                            Toast.makeText(context, "로그아웃 되었습니다. ", Toast.LENGTH_SHORT).show()
                        }
                        // 로그아웃 하면 현재 저장된 유저의 정보를 모두 지운다.
                        val shared = activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                        val editor = shared?.edit()
                        editor?.clear()
                        editor?.apply()
                        (activity as MainActivity).goToSignUpActivity()
                    }
                    is AWSCognitoAuthSignOutResult.PartialSignOut -> {
                        // Sign Out completed with some errors. User is signed out of the device.
                        signOutResult.hostedUIError?.let {
                            Log.e("AuthQuickStart", "HostedUI Error", it.exception)
                            // Optional: Re-launch it.url in a Custom tab to clear Cognito web session.

                        }
                        signOutResult.globalSignOutError?.let {
                            Log.e("AuthQuickStart", "GlobalSignOut Error", it.exception)
                            // Optional: Use escape hatch to retry revocation of it.accessToken.
                        }
                        signOutResult.revokeTokenError?.let {
                            Log.e("AuthQuickStart", "RevokeToken Error", it.exception)
                            // Optional: Use escape hatch to retry revocation of it.refreshToken.
                        }
                    }
                    is AWSCognitoAuthSignOutResult.FailedSignOut -> {
                        // Sign Out failed with an exception, leaving the user signed in.
                        Log.e("AuthQuickStart", "Sign out Failed", signOutResult.exception)
                        activity?.runOnUiThread {
                            Toast.makeText(context, "로그아웃에 실패하였습니다. ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}