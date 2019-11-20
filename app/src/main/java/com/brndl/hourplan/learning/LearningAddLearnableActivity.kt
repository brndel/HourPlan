package com.brndl.hourplan.learning

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.exifinterface.media.ExifInterface
import com.brndl.hourplan.R
import com.brndl.hourplan.subjects.SubjectManager
import kotlinx.android.synthetic.main.activity_learning_add_learnable.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LearningAddLearnableActivity : AppCompatActivity() {

    //var subject: Int = -1
    //var topic: String? = null
    //private var changeIndex: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_add_learnable)

        val subject = intent.getIntExtra("SUBJECT", -1)
        val topic = intent.getStringExtra("TOPIC")
        val changeIndex = intent.getIntExtra("CHANGE_INDEX", -1)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        toolbar.menu.findItem(R.id.item_add_camera).isVisible = changeIndex == -1
        toolbar.menu.findItem(R.id.item_delete).isVisible = changeIndex != -1
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_add_camera) {
                val intent = Intent(this, LearningAddLearnableCameraActivity::class.java)
                intent.putExtra("subject", subject)
                intent.putExtra("topic", topic)
                startActivity(intent)
                finish()
            } else if(it.itemId == R.id.item_delete) {
                LearningManager.removeLearnable(subject, topic!!, changeIndex)
                finish()
            }
            true
        }

        val color: Int? = SubjectManager.getSubject(subject)?.color
        if (color != null) {
            window.statusBarColor = Color.rgb(
                (Color.red(color) * 0.8).toInt(),
                (Color.green(color) * 0.8).toInt(),
                (Color.blue(color) * 0.8).toInt()
            )
            toolbar.background = ColorDrawable(color)
        }


        if (changeIndex != -1) {
            val learnable: Learnable? =
                LearningManager.getLearnableOfTopicWithIndex(subject, topic, changeIndex)
            toolbar.title = getString(R.string.edit)
            if (learnable != null) {
                text_input_question.editText?.setText(learnable.question)
                text_input_answer.editText?.setText(learnable.answer)
                text_input_extra_Info.editText?.setText(learnable.extraInfo)
                if(learnable.ImageUri != "null" && learnable.ImageUri != null) {
                    println("imageUri is not null")
                    imageUri = Uri.parse(learnable.ImageUri)
                    imageViewAddImage.setImageResource(R.drawable.ic_wrong)
                    imageViewImage.setImageURI(imageUri)
                }
                if(learnable.ImageUri == null) println("imageUri is null")



                println("imageUri: " + learnable.ImageUri)

                addLeaveButton.text = getText(R.string.save_changes)
                addButton.visibility = View.GONE
            }
        }

        imageViewAddImage.setOnClickListener {
            if(imageUri == null) {
                if(requestCameraPermission()) {
                    startCamera()
                }
            } else {
                imageViewAddImage.setImageResource(R.drawable.ic_add_a_photo)
                imageViewImage.setImageBitmap(null)
                imageViewImage.maxHeight = 0
                imageUri = null
            }
        }

        fun addLearnable(invalidate: Boolean) {
            LearningManager.addLearnable(
                subject, topic, Learnable(
                    text_input_question.editText?.text.toString(),
                    text_input_answer.editText?.text.toString(),
                    text_input_extra_Info.editText?.text.toString(),
                    imageUri.toString()
                ),
                changeIndex
            )
            if(invalidate) {
                toolbar.subtitle =
                    getString(R.string.added_format).format(text_input_question.editText?.text.toString())
                imageUri = null
                imageViewImage.setImageBitmap(null)
                imageViewImage.maxHeight = 0
                text_input_question.editText?.setText("")
                text_input_answer.editText?.setText("")
                text_input_extra_Info.editText?.setText("")
                text_input_question.editText?.requestFocus()
            }
        }

        addLeaveButton.setOnClickListener {
            if (validateQuestion() && validateAnswer()) {
                addLearnable(false)
                finish()
            }
        }
        addButton.setOnClickListener {
            if (validateQuestion() && validateAnswer()) {
                addLearnable(true)
            }
        }

    }

    private var imageUri: Uri? = null

    fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageUri = FileProvider.getUriForFile(
            this,
            "com.brndl.hourplan.provider",
            getOutputMediaFile()
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 11)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (imageUri != null) {
                val inputStream = this.contentResolver.openInputStream(imageUri!!)

                var bitmap = BitmapFactory.decodeStream(inputStream)

                val path = imageUri?.path

                if (currentPhotoPath != null && path != null) {
                    val ei = ExifInterface(currentPhotoPath!!)
                    val orientation: Int = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    println("orientation: $orientation")
                    when (orientation) {
                        ExifInterface.ORIENTATION_NORMAL -> println("orientation normal")
                        ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotateImage(bitmap, 90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotateImage(bitmap, 180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotateImage(bitmap, 270f)
                    }
                }

                imageViewImage.maxHeight = bitmap.height

                imageViewAddImage.setImageResource(R.drawable.ic_wrong)
                imageViewImage.setImageBitmap(bitmap)

            }
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    private var currentPhotoPath: String? = null

    private fun getOutputMediaFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File.createTempFile(
            "JPEG_$timeStamp",
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun validateQuestion(): Boolean {
        return if (text_input_question.editText?.text.toString().trim().isEmpty()) {
            text_input_question.error = getString(R.string.field_cant_be_empty)
            false
        } else true
    }

    private fun validateAnswer(): Boolean {
        return if (text_input_answer.editText?.text.toString().trim().isEmpty()) {
            text_input_answer.error = getString(R.string.field_cant_be_empty)
            false
        } else true
    }

    private fun requestCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@LearningAddLearnableActivity,
                    android.Manifest.permission.CAMERA
                )
            ) {

                AlertDialog.Builder(this@LearningAddLearnableActivity)
                    .setTitle(R.string.permission_needed)
                    .setTitle(R.string.camera_permission_needed)
                    .setCancelable(false)
                    .setPositiveButton(
                        R.string.ok
                    ) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this@LearningAddLearnableActivity,
                            arrayOf(android.Manifest.permission.CAMERA),
                            cameraPermissionCode
                        )
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { p0, _ ->
                        p0?.dismiss()
                    }
                    .create().show()

            } else {
                ActivityCompat.requestPermissions(
                    this@LearningAddLearnableActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    cameraPermissionCode
                )
            }
            return false
        }
    }

    private val cameraPermissionCode = 1

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startCamera()
        }
    }

}
