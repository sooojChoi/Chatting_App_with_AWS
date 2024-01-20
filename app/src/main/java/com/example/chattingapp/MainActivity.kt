package com.example.chattingapp

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Action
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Consumer
import com.amplifyframework.core.async.Cancelable
import com.amplifyframework.core.model.query.ObserveQueryOptions
import com.amplifyframework.core.model.query.QuerySortBy
import com.amplifyframework.core.model.query.QuerySortOrder
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicates
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.DataStoreConfiguration
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.DataStoreQuerySnapshot
import com.amplifyframework.datastore.generated.model.Group
import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.datastore.generated.model.Room
import com.amplifyframework.datastore.generated.model.User
import com.example.chattingapp.databinding.ActivityMainBinding
import com.example.chattingapp.ui.MessageListAdapter
import com.example.chattingapp.ui.MessageViewModel
import com.example.chattingapp.ui.MsgSentTimeComparator
import com.example.chattingapp.ui.RoomListAdapter
import com.example.chattingapp.ui.RoomMsgTimeComparator
import com.example.chattingapp.ui.RoomViewModel
import com.example.chattingapp.ui.login.SignUpActivity
import com.example.chattingapp.ui.login.UserInfoViewModel
import com.example.chattingapp.ui.login.UserModel
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import java.io.File
import java.util.Base64
import java.util.Collections


class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var ws: WebSocket
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var myEmail:String
    private val userViewModel: UserInfoViewModel by viewModels()
  //  private val roomViewModel: RoomViewModel by viewModels()
    lateinit var roomViewModel: RoomViewModel
    lateinit var messageViewModel: MessageViewModel
   // private val messageViewModel: MessageViewModel by viewModels()
    lateinit var coroutineScope: CoroutineScope
    private lateinit var appBarConfiguration: AppBarConfiguration

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        coroutineScope= CoroutineScope(Dispatchers.IO)

        roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        // amplify pulgin추가, 초기화 작업
        initAmplify()

        // toolbar 설정
        setSupportActionBar(findViewById(R.id.toolbar2))

        // bottom navigation 설정
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.setupWithNavController(navController)

        //app bar와 navigation 연결
    //    appBarConfiguration = AppBarConfiguration(navController.graph)
  //      setupActionBarWithNavController(navController, appBarConfiguration)


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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)

        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun sendCommand(@Suppress("SameParameterValue") command: String) {
        Log.i(TAG,"버튼 눌림")

        // 소켓을 통해 데이터 전송
        ws.send(JSONObject()
            .put("action", "sendmessage")
            .put("data", command)
            .toString())


        Log.i(TAG,"데이터 전송")
    }

    fun sendMessage(obj: JSONObject){
        // websocket으로 메시지 전송
        Log.i("websocket sending process","MainActivity sendMessage 함수 호출됨")

        ws.send(obj.toString())
        Log.i("websocket sending process","데이터 전송됨")
    }

    fun receiveMessage(obj: JSONObject){
        // websocket으로부터 메시지 받은 함수에서 호출되는 함수
        val tag = "websocket receiving process"
        Log.i(tag, "MainActivity receiveMessage 함수 호출됨")

        val fromId = obj.get("fromId").toString()
        // 내가 보낸 메시지가 아닐 경우, viewModel에 추가.
        // 내가 보낸 메시지일 경우 ChattingFragment에서 viewModel에 추가한다.
        if(fromId != userViewModel.emailLiveData.value){
            val messageItem = Message.builder().fromId(fromId)
                .text(obj.get("text").toString())
                .datetime(obj.get("msgDateTime").toString())
                .roomId(obj.get("msgRoomId").toString())
                .type(obj.get("type").toString())
                .fromName(obj.get("fromName").toString())
                .id(obj.get("msgId").toString())
                .build()
            val lastMsg = if(obj.get("type").toString()=="text"){
                obj.get("lastMsg").toString()
            }else{
                "사진"
            }
            val roomItem = Room.builder().name(obj.get("roomName").toString())
                .lastMsgTime(obj.get("lastMsgTime").toString())
                .members(obj.get("members").toString())
                .lastMsg(lastMsg)
                .id(obj.get("roomId").toString())
                .build()


            // message viewModel에 데이터 추가
            val msgArr = messageViewModel.msgLiveData.value ?: ArrayList<Message>()
            msgArr.add(messageItem)
            messageViewModel.msgLiveData.postValue(msgArr)

            // room viewModel의 데이터 수정
            val roomArr = roomViewModel.roomLiveData.value!!
            val newRoomArr = ArrayList<Room>()
            roomArr.forEach {
                if(it.id == roomItem.id){
                    newRoomArr.add(roomItem)
                }else{
                    newRoomArr.add(it)
                }
            }
            roomViewModel.roomLiveData.postValue(newRoomArr)
        }
    }



    private fun setupWebSocket() {
        Log.i(TAG,"웹 소켓 생성")
        val request = Request.Builder()
            .addHeader("user-id",myEmail!!)
            .url(getString(R.string.websocket_url))
            .build()

        // 웹 소켓 생성
        //ws = OkHttpClient().newWebSocket(request, HttpWebSocket().listener)
        ws = OkHttpClient().newWebSocket(request, object: WebSocketListener() {
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

                // 받은 데이터를 viewModel에 저장.
                val json = JSONObject(text)
                receiveMessage(json)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.d("TLOG", "ByteString 데이터 확인 : $bytes")
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d("TLOG", "소켓 onOpen, 전송 데이터 확인 : $webSocket : $response")

            }
        })


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
    @RequiresApi(Build.VERSION_CODES.O)
    fun initAmplify(){
        // aws의 amplify를 사용하기 위해 초기화. (for using cognito, dynamoDB, S3)
        try {
            Amplify.addPlugin(AWSApiPlugin())
        //    Amplify.addPlugin(AWSDataStorePlugin())
            // cloud의 DynamoDB에 있는 데이터들을 local datastore로 sync한다.
            Amplify.addPlugin(AWSDataStorePlugin.builder().dataStoreConfiguration(
                DataStoreConfiguration.builder()
                    .syncExpression(User::class.java){ QueryPredicates.all()}
                    .syncExpression(Room::class.java){ QueryPredicates.all()}
                    .syncExpression(Group::class.java){ QueryPredicates.all()}
                    .syncExpression(Message::class.java) {QueryPredicates.all()}
                    .build())
                .build())

            Amplify.addPlugin(AWSCognitoAuthPlugin())
         //   Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)

            Log.i("chattingApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("chattingApp", "Could not initialize Amplify", error)
        }
        // 현재 인증 세션을 가져온다.
        Amplify.Auth.fetchAuthSession(
            {
                if(!it.isSignedIn){
                    // 인증 세션이 없으면 로그인 화면으로 이동
                    startActivity(Intent(this, SignUpActivity::class.java))
                    finish()
                }else {
                    //인증 세션이 있으면 이메일 정보 가져옴
                    val shared = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                    // 해당 키의 데이터가 없으면 지정한 기본값을 반환한다.
                    myEmail = shared.getString("email", null) ?: "null"

                    Log.i(TAG,"SharedPreferences: my email: $myEmail")
                    // 소켓을 생성.
                    if(myEmail.contains('@')){
                        setupWebSocket()
                        // local store에 대한 observe 등록
                        changeSync()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserInfoFromDynamoDB(){
        val localFile = File.createTempFile("images", "jpg")

        // local store에 대한 observe 등록
        val tag = "ObserveQuery"
        val onQuerySnapshot: Consumer<DataStoreQuerySnapshot<User>> =
            Consumer<DataStoreQuerySnapshot<User>> { value: DataStoreQuerySnapshot<User> ->
                Log.d(tag, "success on snapshot")
                Log.d(tag, "number of records: " + value.items.size)
                Log.d(tag, "sync status: " + value.isSynced)


                val userArray = ArrayList<UserModel>()
                value.items.forEach {
                    if(it.id.equals(myEmail)){
                        userViewModel.emailLiveData.postValue(it.id)
                        userViewModel.userNameLiveData.postValue(it.name)
                        userViewModel.introductionLiveData.postValue(it.introduction)
//                        // 디바이스에 이름을 저장한다.
//                        val shared = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
//                        val editor = shared?.edit()
//                        editor?.putString("name",it.name)
//                        editor?.apply()

                        RoomListAdapter.myName = it.name
                        MessageListAdapter.myEmail = it.id

                        val islandRef = FirebaseStorage.getInstance().reference.child(it.id)
                        val ONE_MEGABYTE: Long = 512 * 512
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                            userViewModel.imageLiveData.postValue(Base64.getEncoder().encodeToString(it))
                        }.addOnFailureListener {
                            // Handle any errors
                        }
                    }else{
                        var image:String? = ""
                        val islandRef = FirebaseStorage.getInstance().reference.child(it.id)
                        val ONE_MEGABYTE: Long = 512 * 512
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { ba->
                            image = (Base64.getEncoder().encodeToString(ba))
                            userArray.add(UserModel(it.id, it.name, it.introduction, image))
                            userViewModel.otherUsersLiveData.postValue(userArray)
                        }.addOnFailureListener {
                            exception->
                            image = null
                            userArray.add(UserModel(it.id, it.name, it.introduction, image))
                            userViewModel.otherUsersLiveData.postValue(userArray)
                        }
                       // userArray.add(UserModel(it.id, it.name, it.introduction, null))

                    }


                }


                getRoomFromDynamoDB()
            }

        val observationStarted =
            Consumer { _: Cancelable ->
                Log.d(tag, "success on cancelable")

            }
        val onObservationError =
            Consumer { value: DataStoreException ->
                Log.d(tag, "error on snapshot $value")
            }
        val onObservationComplete = Action {
            Log.d(tag, "complete")
        }
        // val predicate: QueryPredicate = QueryPredicate()
        val querySortBy = QuerySortBy("user", "name", QuerySortOrder.ASCENDING)

        val options = ObserveQueryOptions(null, listOf(querySortBy))
        Amplify.DataStore.observeQuery(
            User::class.java,
            options,
            observationStarted,
            onQuerySnapshot,
            onObservationError,
            onObservationComplete
        )

        // local data를 cloud와 re-sync
        //changeSync()
    }

    fun getRoomFromDynamoDB(){
        // room 정보는 observe하지 않고 query를 통해 가져온다.
        // room 정보가 바뀌는 것은 새 메세지가 올 때마다 바뀌기 때문에 빈번하게 일어난다.
        // websocket으로부터 메시지가 올 때마다 viewModel에 추가하도록 한다.
        val TAG = "room from dynamodb"
        Log.i(TAG, "getRoomFromDynamoDB 호출됨")
        val roomArray = ArrayList<Room>()
        Amplify.DataStore.query(Group::class.java,
            Where.matches(Group.USER_ID.eq(myEmail)),
            { myGroups ->
                while (myGroups.hasNext()) {
                    Log.i(TAG, "일치하는 group 있음")
                    val group = myGroups.next()
                    Amplify.DataStore.query(Room::class.java,
                        Where.matches(Room.ID.eq(group.roomId)),
                        { rooms ->
                            while (rooms.hasNext()) {
                                Log.i(TAG, "일치하는 room 있음")
                                val room = rooms.next()
                                roomArray.add(room)
                                Log.i(TAG,"roomArray size: ${roomArray.size}")
                                Log.i(TAG, "Title: ${room.id}")
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                roomViewModel.roomLiveData.postValue(roomArray)
                            }
                        },
                        { Log.e(TAG, "Query failed", it) }
                    )
                }

                Log.i(TAG,"room에 데이터 추가")
            },
            { Log.e(TAG, "Query failed", it) }
        )

    }

    fun getMessageFromDynamoDB(roomId:String) {
        val TAG = "message from dynamodb"

        val messageArray = messageViewModel.msgLiveData.value ?: ArrayList()
        Amplify.DataStore.query(Message::class.java,
            Where.matches(Message.ROOM_ID.eq(roomId))
                .sorted(Message.DATETIME.descending()),
            {
                messages ->
                while(messages.hasNext()){
                    val message = messages.next()
                    messageArray.add(message)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    // 처음에 db에서 가져올 때만 오름차순 정렬해주면,
                    // 그 다음부터 추가되는 msg는 viewModel에 순차적으로 추가되기 때문에
                    // 또 정렬해주지 않아도 된다!
                    Collections.sort(messageArray, MsgSentTimeComparator())
                    messageViewModel.msgLiveData.postValue(messageArray)
                    val roomIdArray = messageViewModel.roomIdWithMessage.value ?: ArrayList<String>()
                    roomIdArray?.add(roomId)
                    messageViewModel.roomIdWithMessage.postValue(roomIdArray)
                }
            },
            {
                Log.i(TAG, "Qeury failed", it)
            }
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun changeSync(){
        //local store의 데이터를 지우고 다시 cloud data와 sync 한다.
        // 이때 syncExpression 조건을 변경하고 sync할 수 있다.
        Amplify.DataStore.clear(
            {
                Amplify.DataStore.start(
                    {
                        Log.i("MyAmplifyApp", "DataStore started")
                        getUserInfoFromDynamoDB()
                        //getRoomFromDynamoDB()
                    },{
                        Log.i("MyAmplifyApp", "DataStore Exception", it)
                    }
                )

            },
            { Log.e("MyAmplifyApp", "Error clearing DataStore", it) }
        )
    }


    // toolbar의 back button이 눌리면 프래그먼트 스택 하나 뒤로 감.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                hideUpButton()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showUpButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun hideUpButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 화면 터치시 키보드 내려가도록. 채팅 전송 버튼을 눌렀을 때는 키보드가 내려가지 않도록.
//        if(!(currentFocus is ImageButton)){
//            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
//
//            if(currentFocus is EditText) {
//                currentFocus!!.clearFocus()
//            }
//        }


        return super.dispatchTouchEvent(ev)
    }

}