package com.example.chattingapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.chattingapp.MainActivity
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



    }

    fun goToMainAcitivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}