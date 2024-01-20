package com.example.chattingapp.ui

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.User
import com.example.chattingapp.databinding.ActivityEditMySimpleProfileBinding
import com.example.chattingapp.databinding.EditIntroductionDialogBinding
import com.example.chattingapp.databinding.EditNameDialogBinding
import com.example.chattingapp.ui.login.UserInfoViewModel
import java.lang.IllegalStateException
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.amplifyframework.core.model.query.Where
import com.example.chattingapp.R
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Base64

class EditMySimpleProfileActivity : AppCompatActivity() {
    val binding by lazy { ActivityEditMySimpleProfileBinding.inflate(layoutInflater) }
    private val viewModel: UserInfoViewModel by viewModels()
    lateinit var storage: FirebaseStorage
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        val introduction = intent.getStringExtra("introduction")
        val image = intent.getStringExtra("image")

        binding.nameTextView.text = name
        binding.introTextView.text = introduction
        if(introduction == null || introduction==""){
            binding.introTextView.text = "소개글을 입력해보세요!"
        }
        val byteArray = Base64.getDecoder().decode(image)
        val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
        binding.imageView3.clipToOutline = true
        binding.imageView3.setImageBitmap(bm)
        binding.imageView3.setPadding(0,0,0,0)

        viewModel.emailLiveData.value = email
        viewModel.userNameLiveData.value = name
        viewModel.introductionLiveData.value = introduction
        viewModel.imageLiveData.value = image

        // dialog에서 데이터를 변경하면 activity에서도 바꾸기.
        viewModel.userNameLiveData.observe(this){
            binding.nameTextView.text = it
        }
        viewModel.introductionLiveData.observe(this){
            if(it !=null && it!=""){
                binding.introTextView.text = it
            }else {
                binding.introTextView.text = "소개글을 입력해보세요!"
            }
        }


        // 프로필 이름 변경
        binding.EditNameButton.setOnClickListener {
            val nameDialogFragment = EditNameDialogFragment()
            nameDialogFragment.show(supportFragmentManager,"nameDialogFragment")
        }

        // 프로필 소개 변경
        binding.EditIntroButton.setOnClickListener {
            val introDialogFragment = EditIntroductionDialogFragment()
            introDialogFragment.show(supportFragmentManager,"introDialogFragment")
        }

        storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child(email!!)




        // 가져온 사진 보여주기
        val pickImageLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    data?.data?.let {
                        binding.imageView3.clipToOutline = true
                        binding.imageView3.setImageURI(it)
                        binding.imageView3.setPadding(0,0,0,0)

                        val stream = contentResolver.openInputStream(it)
                        val task = ref.putStream(stream!!)
                        task.addOnFailureListener {
                            Log.i("upload image","fail")
                        }.addOnSuccessListener { taskSnapshot ->
                            Log.i("upload image","success")
                            val bm = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                            var bytearray = ByteArrayOutputStream()
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, bytearray)
                            val image = Base64.getEncoder().encodeToString(bytearray.toByteArray())
                            intent.putExtra("image",image)
                            setResult(RESULT_OK, intent)
                        }

//                        Amplify.Storage.uploadInputStream(email!!, stream!!,
//                            { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}") },
//                            { Log.e("MyAmplifyApp", "Upload failed", it) }
//                        )


                    }
                }
            }

        // 갤러리 open
        val requestPermissionLauncher: ActivityResultLauncher<String> =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    pickImageLauncher.launch(gallery)
                }
            }

        // 프로필 사진 변경
        binding.imageEditButton.setOnClickListener {
            val check = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES)
            } else{
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if(check==PackageManager.PERMISSION_GRANTED){
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                pickImageLauncher.launch(gallery)
            }else{
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }else{
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            }
        }

    }



}

class EditNameDialogFragment: DialogFragment(){
    private val userViewModel: UserInfoViewModel by activityViewModels()
    private val roomViewModel: RoomViewModel by activityViewModels()
    val binding by lazy { EditNameDialogBinding.inflate(layoutInflater) }
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        coroutineScope= CoroutineScope(Dispatchers.Main)

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            binding.nameEditText.setText(userViewModel.userNameLiveData.value)

            builder.setView(binding.root)
                .setPositiveButton("확인"
                ) { dialog, id ->
                    coroutineScope.launch {
                        val user = User.builder()
                            .name(binding.nameEditText.text.toString())
                            .id(userViewModel.emailLiveData.value)
                            .introduction(userViewModel.introductionLiveData.value)
                            .build()

                        Amplify.DataStore.save(user,
                            {
                                Log.i("MyAmplifyApp", "Post updated successfully!")
                                userViewModel.userNameLiveData.postValue(binding.nameEditText.text.toString())
                                RoomListAdapter.myName = binding.nameEditText.text.toString()

                                dialog.dismiss()
                            },
                            {
                                Log.e("MyAmplifyApp", "Could not update post, maybe the title has been changed?", it)
                                // Toast 안내문 띄우기
                                dialog.dismiss()
                            }
                        )
                    }

                }
                .setNegativeButton("취소"
                ) { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    fun getUser(): User?{
        var result:User? = null
        Amplify.DataStore.query(User::class.java,
            Where.matches(User.ID.eq(userViewModel.emailLiveData.value)),
            { myInfo ->
                while (myInfo.hasNext()) {
                    val user = myInfo.next()
                    result = user
                }
            },
            { Log.e("MyAmplifyApp", "Query failed", it) }
        )
        return result
    }
}

class EditIntroductionDialogFragment: DialogFragment(){
    val binding by lazy { EditIntroductionDialogBinding.inflate(layoutInflater) }
    private val viewModel: UserInfoViewModel by activityViewModels()
    lateinit var coroutineScope: CoroutineScope

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        coroutineScope= CoroutineScope(Dispatchers.Main)

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            binding.introEditText.setText(viewModel.introductionLiveData.value)

            builder.setView(binding.root)
                .setPositiveButton("확인"
                ) { dialog, it ->
                    // 사용자 소개글 업데이트
                    coroutineScope.launch {
                        val user = User.builder()
                            .name(viewModel.userNameLiveData.value)
                            .id(viewModel.emailLiveData.value)
                            .introduction(binding.introEditText.text.toString())
                            .build()

                        Amplify.DataStore.save(user,
                            {
                                Log.i("MyAmplifyApp", "Post updated successfully!")
                                viewModel.introductionLiveData.postValue(binding.introEditText.text.toString())
                                dialog.dismiss()
                            },
                            {
                                Log.e("MyAmplifyApp", "Could not update post, maybe the title has been changed?", it)
                                // Toast 안내문 띄우기
                                dialog.dismiss()
                            }
                        )
                    }
                }
                .setNegativeButton("취소"
                ) { dialog, it ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}


