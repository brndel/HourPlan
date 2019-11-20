package com.brndl.hourplan.subjects

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.brndl.hourplan.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_subject.*
import kotlinx.android.synthetic.main.subject_item.view.*
import yuku.ambilwarna.AmbilWarnaDialog

class AddSubjectActivity : AppCompatActivity() {


    private var editSubject: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subject)

        var subject = ""
        var teacher = ""
        var color: Int = Color.rgb(200, 200, 200)

        editSubject = intent.getIntExtra("EDIT_SUBJECT", -1)
        if (editSubject == -1) editSubject = null


        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.setOnMenuItemClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.confirm_delete_format).format(SubjectManager.getSubject(editSubject)?.name))
                .setCancelable(true)
                .setPositiveButton(R.string.delete
                ) { _, _ ->
                    SubjectManager.removeSubject(editSubject)
                    finish()
                }
                .setNegativeButton(R.string.cancel) { p0, _ -> p0?.cancel() }
            builder.create().show()

            true
        }
        toolbar.menu.findItem(R.id.item_delete).isVisible = editSubject != null

        val editColor: Int? = SubjectManager.getSubject(editSubject)?.color
        if(editColor != null) {
            window.statusBarColor = Color.rgb((Color.red(editColor)*0.8).toInt(), (Color.green(editColor)*0.8).toInt(), (Color.blue(editColor)*0.8).toInt())
            toolbar.background = ColorDrawable(editColor)
        }

        if (editSubject != null) {
            val subjectInfo = SubjectManager.getSubject(editSubject)
            if (subjectInfo != null) {
                toolbar.title = getText(R.string.edit_format).toString().format(SubjectManager.getSubject(editSubject)?.name)
                addButton.text = getText(R.string.save_changes).toString()
                subject = subjectInfo.name
                text_input_subject.editText?.setText(subject)
                subjectItem.subjectText.text = subject
                teacher = subjectInfo.teacher
                text_input_teacher.editText?.setText(teacher)
                subjectItem.teacherText.text = teacher
                color = subjectInfo.color
                color_layout.setBackgroundColor(color)
                (subjectItem as CardView).setCardBackgroundColor(color)
            }
        }

        button_select_color.setOnClickListener {

            val colorPicker =
                AmbilWarnaDialog(this, color, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onCancel(dialog: AmbilWarnaDialog?) {
                    }

                    override fun onOk(dialog: AmbilWarnaDialog?, fcolor: Int) {
                        color = fcolor
                        color_layout.setBackgroundColor(color)
                        (subjectItem as CardView).setCardBackgroundColor(color)
                    }


                })
            colorPicker.show()
        }

        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                subject = text_input_subject.editText?.text.toString()
                teacher = text_input_teacher.editText?.text.toString()
                subjectItem.subjectText.text = subject
                subjectItem.teacherText.text = teacher
            }


        }
        text_input_subject.editText?.addTextChangedListener(textWatcher)
        text_input_teacher.editText?.addTextChangedListener(textWatcher)

        addButton.setOnClickListener {
            if (validateName()) {
                if (SubjectManager.addSubject(SubjectInfo(subject, teacher, color), editSubject)) {
                    finish()
                } else {
                    val snackbar: Snackbar = Snackbar.make(
                        coordinatorLayout,
                        getText(R.string.subject_already_exists),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(getText(R.string.update_format).toString().format(subject)) {
                            SubjectManager.addSubject(
                                SubjectInfo(
                                    subject, teacher,
                                    color
                                )
                            )
                            finish()

                        }

                    val colorSubject = SubjectManager.getSubject(editSubject)
                    if (colorSubject != null)
                        snackbar.setActionTextColor(colorSubject.color)
                    snackbar.show()

                }
            }
        }

    }

    private fun validateName(): Boolean {
        return if (text_input_subject.editText?.text.toString().trim().isEmpty()) {
            text_input_subject.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            text_input_subject.error = null
            true
        }
    }
}
