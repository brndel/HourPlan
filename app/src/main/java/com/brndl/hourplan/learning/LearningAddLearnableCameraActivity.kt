package com.brndl.hourplan.learning

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brndl.hourplan.R
import com.brndl.hourplan.Views.CameraTextView
import com.brndl.hourplan.select
import com.brndl.hourplan.subjects.SubjectManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_learning_add_learnable_camera.*
import java.io.File

class LearningAddLearnableCameraActivity : AppCompatActivity() {

    var subject = -1
    var topic: String = ""
    lateinit var imageUri: Uri

    private val cameraPermissionCode: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_add_learnable_camera)

        subject = intent.getIntExtra("subject", -1)
        topic = intent.getStringExtra("topic") as String

        toolbar.setNavigationOnClickListener {
            onBackPressed()
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


        if (requestCameraPermission()) {
            startCamera()

        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageUri = FileProvider.getUriForFile(
            this,
            "com.brndl.hourplan.provider",
            getOutputMediaFile()
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, 11)
    }

    var currentPhotoPath: String = ""


    private fun getOutputMediaFile(): File {
        val imageFile = File.createTempFile(
            "JPEG_scannerImg",
            ".jpg",
            cacheDir
        )

        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val inputStream = this.contentResolver.openInputStream(imageUri)!!

            var bitmap = BitmapFactory.decodeStream(inputStream)

            val path = imageUri.path
            if (path != null) {
                val ei = ExifInterface(currentPhotoPath)
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

            imageView.maxHeight = bitmap.height

            imageView.setImageBitmap(bitmap)

            val image = FirebaseVisionImage.fromBitmap(bitmap)
            FirebaseApp.initializeApp(this)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener {
                    loadingCircle.visibility = View.GONE
                    textInputLayout.visibility = View.VISIBLE
                    if(it.textBlocks.size == 0) {
                        textViewWarningNothingFound.visibility = View.VISIBLE
                        return@addOnSuccessListener
                    }
                    selectedCameraChips.setHasFixedSize(false)
                    selectedCameraChips.layoutManager = LinearLayoutManager(this)

                    selectedCameraChips.itemAnimator?.apply {

                        addDuration = 100
                        removeDuration = 20
                        moveDuration = 50
                        changeDuration = 50
                }


                    val adapter = CameraTextAdapter(ArrayList())
                    selectedCameraChips.adapter = adapter

                    val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
                    ) {

                        override fun onMove(
                            recyclerView: RecyclerView,
                            dragged: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {

                            val position_dragged = dragged.adapterPosition
                            val position_target = target.adapterPosition

                            adapter.notifyItemMoved(position_dragged, position_target)
                            return false
                        }

                        override fun onSwiped(
                            viewHolder: RecyclerView.ViewHolder,
                            direction: Int
                        ) {
                        }
                    })
                    itemTouchHelper.attachToRecyclerView(selectedCameraChips)

                    for (text in it.textBlocks) {
                        CameraTextChip(text, this)
                    }

                    fun unselectAll() {
                        val chips: ArrayList<CameraTextChip> = ArrayList(adapter.itemList)
                        for (chip in chips) {
                            chip.selected = false
                        }
                    }

                    fun deleteSelected() {
                        val chips: ArrayList<CameraTextChip> = ArrayList(adapter.itemList)
                        for (chip in chips) {
                            chip.remove()
                        }
                    }

                    imageViewUnselectAll.setOnClickListener {
                        unselectAll()
                    }

                    imageViewDeleteAll.setOnClickListener {
                        deleteSelected()
                    }

                    imageQuestion.setOnClickListener {
                        if (adapter.itemList.size < 1) {
                            Toast.makeText(this, R.string.nothing_selected, Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }
                        val chips = adapter.itemList
                        var text = chips[0].text
                        for (i in 1..chips.lastIndex) {
                            val chip = chips[i]
                            text += " " + chip.text
                        }
                        editTextQuestion.setText(text)
                        deleteSelected()
                    }

                    imageAnswer.setOnClickListener {
                        if (adapter.itemList.size < 1) {
                            Toast.makeText(this, R.string.nothing_selected, Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }
                        val chips = adapter.itemList
                        var text = chips[0].text
                        for (i in 1..chips.lastIndex) {
                            val chip = chips[i]
                            text += " " + chip.text
                        }
                        editTextAnswer.setText(text)
                        deleteSelected()
                    }

                    imageExtraInfo.setOnClickListener {
                        if (adapter.itemList.size < 1) {
                            Toast.makeText(this, R.string.nothing_selected, Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }
                        val chips = adapter.itemList
                        var text = chips[0].text
                        for (i in 1..chips.lastIndex) {
                            val chip = chips[i]
                            text += " " + chip.text
                        }
                        editTextExtraInfo.setText(text)
                        deleteSelected()
                    }

                    imageAddAll.setOnClickListener {
                        editTextQuestion.setText(adapter.itemList[0].text)
                        editTextAnswer.setText(adapter.itemList[1].text)
                        if (adapter.itemList.lastIndex >= 2)
                            editTextExtraInfo.setText(adapter.itemList[2].text)
                        deleteSelected()
                    }

                    buttonAdd.setOnClickListener {
                        if (editTextQuestion.text.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "${getString(R.string.field_cant_be_empty)}: ${getString(R.string.question)}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        if (editTextAnswer.text.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "${getString(R.string.field_cant_be_empty)}: ${getString(R.string.answer)}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        LearningManager.addLearnable(
                            subject,
                            topic,
                            Learnable(
                                editTextQuestion.text.toString(),
                                editTextAnswer.text.toString(),
                                editTextExtraInfo.text.toString()
                            )
                        )
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                        currentFocus?.clearFocus()
                        editTextQuestion.setText("")
                        editTextAnswer.setText("")
                        editTextExtraInfo.setText("")
                    }
                }
                .addOnFailureListener {
                    loadingCircle.visibility = View.GONE
                    textViewError.visibility = View.VISIBLE
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                }
        }
        else if(resultCode == Activity.RESULT_CANCELED) {
            loadingCircle.visibility = View.GONE
            textViewWarningNothingFound.visibility = View.VISIBLE
            textViewWarningNothingFound.text = getText(R.string.warning_no_photo_took)
        }
    }

    val cameraTextChips: ArrayList<CameraTextChip> = ArrayList()


    class CameraTextChip(val context: Context) {

        var text: String? = null
            get() {
                return if (field != null) {
                    field
                } else {
                    val chips = childChips
                    if (chips != null) {
                        var text = chips[0].text
                        for (i in 1..chips.lastIndex) {
                            val chip = chips[i]
                            text += " " + chip.text
                        }
                        text
                    } else null
                }
            }

        private var rect: Rect? = null
            get() {
                return if (field != null) {
                    field
                } else {
                    val chips = childChips
                    if (chips != null) {
                        val rect = Rect()
                        rect.set(chips[0].rect)
                        for (i in 1..chips.lastIndex) {
                            val chip = chips[i]
                            if (chip.rect!!.top < rect!!.top)
                                rect.top = chip.rect!!.top

                            if (chip.rect!!.left < rect.left)
                                rect.left = chip.rect!!.left

                            if (chip.rect!!.bottom > rect.bottom)
                                rect.bottom = chip.rect!!.bottom

                            if (chip.rect!!.right > rect.right)
                                rect.right = chip.rect!!.right
                        }
                        rect
                    } else null
                }
            }

        private var parent: CameraTextChip? = null
            set(value) {
                field = value
                if (parent == null)
                    addViews()
            }

        var childChips: ArrayList<CameraTextChip>? = null

        private var cameraTextView: CameraTextView? = null

        constructor(text: String, rect: Rect?, parent: CameraTextChip?, context: Context) :
                this(context) {
            this.text = text
            this.rect = rect
            this.parent = parent
            if (parent == null) {
                addViews()
            }
        }


        constructor(parent: CameraTextChip, context: Context) :
                this(context) {
            this.parent = parent
        }

        constructor(textBlock: FirebaseVisionText.TextBlock, context: Context) :
                this(context) {
            if (textBlock.lines.size > 1) {
                childChips = ArrayList()
                for (line in textBlock.lines) {
                    val parent = CameraTextChip(this, context)
                    if (line.elements.size > 1) {
                        val childs: ArrayList<CameraTextChip> = ArrayList()
                        for (element in line.elements) {
                            childs.add(CameraTextChip(element.text, element.boundingBox.apply {
                                val pixelDensity = context.resources.displayMetrics.density
                                if (this != null) {
                                    this.top = (this.top / pixelDensity).toInt() - 5
                                    this.bottom = (this.bottom / pixelDensity).toInt() + 5
                                    this.left = (this.left / pixelDensity).toInt() - 5
                                    this.right = (this.right / pixelDensity).toInt() + 5
                                }

                            }, parent, context))
                        }
                        parent.childChips = childs
                    } else {
                        parent.text = line.text
                        parent.rect = line.boundingBox.apply {
                            val pixelDensity = context.resources.displayMetrics.density
                            if (this != null) {
                                this.top = (this.top / pixelDensity).toInt() - 5
                                this.bottom = (this.bottom / pixelDensity).toInt() + 5
                                this.left = (this.left / pixelDensity).toInt() - 5
                                this.right = (this.right / pixelDensity).toInt() + 5
                            }

                        }
                    }
                    childChips?.add(parent)
                }
            } else {
                if (textBlock.lines[0].elements.size > 1) {
                    childChips = ArrayList()
                    for (element in textBlock.lines[0].elements) {
                        childChips?.add(CameraTextChip(element.text, element.boundingBox.apply {
                            val pixelDensity = context.resources.displayMetrics.density
                            if (this != null) {
                                this.top = (this.top / pixelDensity).toInt() - 5
                                this.bottom = (this.bottom / pixelDensity).toInt() + 5
                                this.left = (this.left / pixelDensity).toInt() - 5
                                this.right = (this.right / pixelDensity).toInt() + 5
                            }

                        }, this, context))
                    }
                } else {
                    text = textBlock.text
                    rect = textBlock.boundingBox.apply {
                        val pixelDensity = context.resources.displayMetrics.density
                        if (this != null) {
                            this.top = (this.top / pixelDensity).toInt() - 5
                            this.bottom = (this.bottom / pixelDensity).toInt() + 5
                            this.left = (this.left / pixelDensity).toInt() - 5
                            this.right = (this.right / pixelDensity).toInt() + 5
                        }

                    }
                }
            }
            addViews()
        }

        private fun addViews() {
            (context as LearningAddLearnableCameraActivity).cameraTextChips.add(this)

            val cameraTextView = CameraTextView(context, rect)
            cameraTextView.setOnClickListener {
                selected = !selected
            }

            (context as Activity).findViewById<RelativeLayout>(R.id.cameraTextLayout).addView(cameraTextView)

            this.cameraTextView = cameraTextView
        }

        val recyclerView: RecyclerView =
            (context as Activity).findViewById(R.id.selectedCameraChips)

        val adapter: CameraTextAdapter = recyclerView.adapter as CameraTextAdapter


        var selected: Boolean = false
            set(value) {
                field = value
                cameraTextView?.highlighted = value
                if (value) {
                    adapter.itemList.add(this)
                    adapter.notifyItemInserted(adapter.itemList.indexOf(this))
                    (context as LearningAddLearnableCameraActivity).notifySelectedChipsChanged()

                } else {
                    val index: Int = adapter.itemList.indexOf(this)
                    adapter.itemList.remove(this)
                    adapter.notifyItemRemoved(index)
                    (context as LearningAddLearnableCameraActivity).notifySelectedChipsChanged()
                }
            }


        fun split() {
            val childs = childChips
            if (childs != null) {
                for (child in childs) {
                    child.parent = null
                    child.selected = selected
                }
            }
            remove()
        }

        fun remove() {
            (context as LearningAddLearnableCameraActivity).cameraTextChips.remove(this)
            selected = false
            (context as Activity).findViewById<RelativeLayout>(R.id.cameraTextLayout).removeView(cameraTextView)
        }
    }

    fun notifySelectedChipsChanged() {

        val adapter = (selectedCameraChips.adapter as CameraTextAdapter)

        imageViewUnselectAll.visibility =
            select(adapter.itemList.size > 1, View.VISIBLE, View.GONE) as Int
        imageViewDeleteAll.visibility =
            select(adapter.itemList.size > 1, View.VISIBLE, View.GONE) as Int
        imageAddAll.visibility =
            select(
                adapter.itemList.size == 2 || adapter.itemList.size == 3,
                View.VISIBLE,
                View.GONE
            ) as Int
        bottomLineView.visibility =
            select(adapter.itemList.size > 0, View.VISIBLE, View.GONE) as Int

    }


    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
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
                    this@LearningAddLearnableCameraActivity,
                    android.Manifest.permission.CAMERA
                )
            ) {

                AlertDialog.Builder(this@LearningAddLearnableCameraActivity)
                    .setTitle(R.string.permission_needed)
                    .setTitle(R.string.camera_permission_needed)
                    .setCancelable(false)
                    .setPositiveButton(
                        R.string.ok
                    ) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this@LearningAddLearnableCameraActivity,
                            arrayOf(android.Manifest.permission.CAMERA),
                            cameraPermissionCode
                        )
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { p0, _ ->
                        p0?.dismiss()
                        finish()
                    }
                    .create().show()

            } else {
                ActivityCompat.requestPermissions(
                    this@LearningAddLearnableCameraActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    cameraPermissionCode
                )
            }
            return false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startCamera()
            else
                finish()
        }
    }

    override fun onBackPressed() {
        if (cameraTextChips.size > 0) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(
                getString(R.string.confirm_leave)
            )
                .setCancelable(true)
                .setPositiveButton(R.string.leave) { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton(R.string.cancel) { p0, _ -> p0?.cancel() }
            builder.create().show()
        } else
            super.onBackPressed()
    }

}
