package com.example.bt

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var studentName = DEFAULT_NAME
    private var studentClass = DEFAULT_CLASS
    private var studentGpa = DEFAULT_GPA
    private var avatarUri: Uri? = null

    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                studentName = data.getStringExtra(EXTRA_NAME) ?: studentName
                studentClass = data.getStringExtra(EXTRA_CLASS) ?: studentClass
                studentGpa = data.getDoubleExtra(EXTRA_GPA, studentGpa)
                renderProfile()
                toast(getString(R.string.profile_updated))
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            avatarUri = it
            binding.imgAvatar.setImageURI(it)
            toast(getString(R.string.avatar_updated))
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        toast(
            getString(
                if (granted) R.string.camera_permission_granted
                else R.string.camera_permission_denied
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity được khởi tạo")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        studentName = savedInstanceState?.getString(STATE_NAME) ?: DEFAULT_NAME
        studentClass = savedInstanceState?.getString(STATE_CLASS) ?: DEFAULT_CLASS
        studentGpa = savedInstanceState?.getDouble(STATE_GPA) ?: DEFAULT_GPA
        avatarUri = savedInstanceState?.getString(STATE_AVATAR)?.let(Uri::parse)

        renderProfile()
        setupActions()
    }

    private fun setupActions() = with(binding) {
        btnEditProfile.setOnClickListener {
            val intent = Intent(this@MainActivity, EditProfileActivity::class.java).apply {
                putExtra(EXTRA_NAME, studentName)
                putExtra(EXTRA_STUDENT_ID, STUDENT_ID)
                putExtra(EXTRA_CLASS, studentClass)
                putExtra(EXTRA_GPA, studentGpa)
            }
            editProfileLauncher.launch(intent)
        }
        btnChangeAvatar.setOnClickListener { galleryLauncher.launch("image/*") }
        btnCallHotline.setOnClickListener { openDialer() }
        btnRequestCamera.setOnClickListener { requestCameraPermission() }
    }

    private fun renderProfile() = with(binding) {
        tvStudentName.text = studentName
        tvStudentId.text = getString(R.string.student_id_value, STUDENT_ID)
        tvStudentClass.text = getString(R.string.class_value, studentClass)
        tvStudentGpa.text = getString(R.string.gpa_value, studentGpa)
        avatarUri?.let(imgAvatar::setImageURI)
    }

    private fun openDialer() {
        try {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$HOTLINE")))
        } catch (_: ActivityNotFoundException) {
            toast(getString(R.string.no_dialer))
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            toast(getString(R.string.camera_already_granted))
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_NAME, studentName)
        outState.putString(STATE_CLASS, studentClass)
        outState.putDouble(STATE_GPA, studentGpa)
        outState.putString(STATE_AVATAR, avatarUri?.toString())
        super.onSaveInstanceState(outState)
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart: Activity bắt đầu hiển thị") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume: Activity sẵn sàng tương tác") }
    override fun onPause() { Log.d(TAG, "onPause: Activity mất focus"); super.onPause() }
    override fun onStop() { Log.d(TAG, "onStop: Activity không còn hiển thị"); super.onStop() }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart: Activity được mở lại") }
    override fun onDestroy() { Log.d(TAG, "onDestroy: Activity bị hủy"); super.onDestroy() }

    companion object {
        private const val TAG = "TAG_LIFECYCLE"
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_STUDENT_ID = "EXTRA_STUDENT_ID"
        const val EXTRA_CLASS = "EXTRA_CLASS"
        const val EXTRA_GPA = "EXTRA_GPA"
        private const val STATE_NAME = "STATE_NAME"
        private const val STATE_CLASS = "STATE_CLASS"
        private const val STATE_GPA = "STATE_GPA"
        private const val STATE_AVATAR = "STATE_AVATAR"
        private const val DEFAULT_NAME = "Võ Quốc Khánh"
        private const val STUDENT_ID = "2415053122121"
        private const val DEFAULT_CLASS = "126TLTTD01"
        private const val DEFAULT_GPA = 3.5
        private const val HOTLINE = "0905123456"
    }
}
