package com.example.bt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bt.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Mở màn hình chỉnh sửa")

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.edtName.setText(intent.getStringExtra(MainActivity.EXTRA_NAME).orEmpty())
        binding.edtStudentId.setText(intent.getStringExtra(MainActivity.EXTRA_STUDENT_ID).orEmpty())
        binding.edtClass.setText(intent.getStringExtra(MainActivity.EXTRA_CLASS).orEmpty())
        binding.edtGpa.setText(intent.getDoubleExtra(MainActivity.EXTRA_GPA, 0.0).toString())

        binding.btnSave.setOnClickListener { saveAndReturn() }
        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        binding.toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun saveAndReturn() {
        val name = binding.edtName.text?.toString()?.trim().orEmpty()
        val studentClass = binding.edtClass.text?.toString()?.trim().orEmpty()
        val gpaText = binding.edtGpa.text?.toString()?.trim().orEmpty()
        val gpa = gpaText.toDoubleOrNull()

        binding.inputName.error = if (name.isBlank()) getString(R.string.error_name) else null
        binding.inputClass.error = if (studentClass.isBlank()) getString(R.string.error_class) else null
        binding.inputGpa.error = if (gpa == null || gpa !in 0.0..4.0) getString(R.string.error_gpa) else null

        if (name.isBlank() || studentClass.isBlank() || gpa == null || gpa !in 0.0..4.0) return

        val result = Intent().apply {
            putExtra(MainActivity.EXTRA_NAME, name)
            putExtra(MainActivity.EXTRA_CLASS, studentClass)
            putExtra(MainActivity.EXTRA_GPA, gpa)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart: EditProfile hiển thị") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume: EditProfile sẵn sàng") }
    override fun onPause() { Log.d(TAG, "onPause: EditProfile mất focus"); super.onPause() }
    override fun onStop() { Log.d(TAG, "onStop: EditProfile bị che") ; super.onStop() }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart: EditProfile mở lại") }
    override fun onDestroy() { Log.d(TAG, "onDestroy: EditProfile bị hủy"); super.onDestroy() }

    companion object {
        private const val TAG = "TAG_LIFECYCLE"
    }
}
