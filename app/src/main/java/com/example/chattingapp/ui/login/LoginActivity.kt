package com.example.chattingapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ActivityLoginBinding
import kotlin.io.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.emailTextView
        val password = binding.passwordTextView
        val loginButton = binding.loginButton
        val signUpButton = binding.signUpButton
        val loading = binding.loading


        // 로그인 버튼 눌림
        loginButton?.setOnClickListener {
            // 이메일이 올바른 형식이면 로그인 시도
            if(isUserNameValid(email?.text.toString())){
                loading.visibility = View.VISIBLE
                login(email?.text.toString(), password?.text.toString())
            }

        }

        // 회원가입 버튼 눌림
        signUpButton?.setOnClickListener {
            // 회원가입 화면으로 이동
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun login(username: String, password:String){

    }

    private fun updateUiWithUser() {
        val welcome = getString(R.string.welcome)
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome 회원가입이 완료되었습니다.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
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

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
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



/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}