package com.example.chattingapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.chattingapp.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import com.example.chattingapp.HttpWebSocket
import okio.ByteString

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var ws: WebSocket
    private var lineNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // 버튼을 누르면 메시지를 보낸다.
        binding.button.setOnClickListener { sendCommand("turn_on_lights") }
        // 소켓을 생성하고
        setupWebSocket()
    }

    private fun sendCommand(@Suppress("SameParameterValue") command: String) {
        Log.i(TAG,"버튼 눌림")

        // 소켓을 통해 데이터 전송
        ws.send(JSONObject()
            .put("action", "sendmessage")
            .put("data", command)
            .toString())
        // 메시지 보낸 것을 화면 상의 텍스트뷰에 나타냄.
        Log.i(TAG,"데이터 전송")
        appendStatus(getString(R.string.websocket_sent_command, command))


    }

    private fun setupWebSocket() {
        Log.i(TAG,"웹 소켓 생성")
        val request = Request.Builder()
            .url(getString(R.string.websocket_url))
            .build()

        // 웹 소켓 생성.
        ws = OkHttpClient().newWebSocket(request, HttpWebSocket().listener)
    }

    private fun appendStatus(message: String) {
        binding.status.append(getString(R.string.status_line, lineNumber, message))
        lineNumber++
    }
}