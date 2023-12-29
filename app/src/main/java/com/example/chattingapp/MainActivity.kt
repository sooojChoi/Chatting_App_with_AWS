package com.example.chattingapp

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.chattingapp.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONObject
import android.Manifest
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.example.chattingapp.ui.login.SignUpActivity
import java.time.Duration

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var ws: WebSocket
    private var lineNumber = 0
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var myEmail:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initAmplify()

        // setup actionbar with nav controller to show up button
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.setupWithNavController(navController)

        // Declare the launcher at the top of your Activity/Fragment:
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // TODO: Inform user that that your app will not show notifications.
            }
        }
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
            .addHeader("user-id",myEmail!!)
            .url(getString(R.string.websocket_url))
            .build()

        // 웹 소켓 생성.
        ws = OkHttpClient().newWebSocket(request, HttpWebSocket().listener)
    }

    private fun appendStatus(message: String) {
//        binding.status.append(getString(R.string.status_line, lineNumber, message))
//        lineNumber++
    }

    fun goToSignUpActivity(){
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }
    fun closeWebSocket(reason:String){
        // 웹 소켓을 정상적으로 종료한다 .
        if(this::ws.isInitialized){
            ws.close(1000,reason)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        closeWebSocket("app is closed.")
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // cognito 사용을 위해 amplify를 초기화한다.
    fun initAmplify(){
        // aws의 amplify를 사용하기 위해 초기화. (for using cognito)
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("chattingApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("chattingApp", "Could not initialize Amplify", error)
        }
        // 현재 인증 세션을 가져온다.
        Amplify.Auth.fetchAuthSession(
            { //Log.i("AmplifyQuickstart", "Auth session = $it")
                if(!it.isSignedIn){
                    // 인증 세션이 없으면 로그인 화면으로 이동
                    startActivity(Intent(this, SignUpActivity::class.java))
                    finish()
                }else{
                    //인증 세션이 있으면 이메일 정보 가져옴
                    val shared = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                    // 해당 키의 데이터가 없으면 지정한 기본값을 반환한다.
                    myEmail = shared.getString("email", null) ?: "null"

                    Log.i(TAG,"SharedPreferences: my email: $myEmail")
                    // 소켓을 생성.
                    if(myEmail.contains('@')){
                        setupWebSocket()
                    }else{
                        runOnUiThread {
                            Toast.makeText(this,"서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }

                    }

                }
            },
            { error -> Log.e("AmplifyQuickstart", "Failed to fetch auth session", error) }
        )
    }

}