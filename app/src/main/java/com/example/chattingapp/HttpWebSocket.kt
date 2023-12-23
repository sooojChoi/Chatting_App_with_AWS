package com.example.chattingapp

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


class HttpWebSocket {
    var listener: WebSocketListener = object : WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("TLOG", "소켓 onClosed. code: $code, reason: $reason")
            
            webSocket.close(1000, null)
            webSocket.cancel()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            Log.d("TLOG", "소켓 onClosing. code: $code, reason: $reason")

        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.d("TLOG", "소켓 onFailure : $t")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.d("TLOG", "text 데이터 확인 : $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            Log.d("TLOG", "ByteString 데이터 확인 : $bytes")
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d("TLOG", "소켓 onOpen, 전송 데이터 확인 : $webSocket : $response")

        }


    }
}