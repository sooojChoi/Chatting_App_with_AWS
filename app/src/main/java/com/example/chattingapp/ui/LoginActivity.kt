package com.example.chattingapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ActivityLoginBinding
import com.example.chattingapp.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}