package com.brndl.hourplan.learning

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.brndl.hourplan.R
import com.brndl.hourplan.subjects.SubjectManager
import kotlinx.android.synthetic.main.activity_learning_add_topic.*

class LearningAddTopicActivity : AppCompatActivity() {

    var subject : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_add_topic)

        subject = intent.getIntExtra("SUBJECT", -1)

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

        add_button.setOnClickListener {
            if (validateInput()) {
                LearningManager.addTopic(subject, text_input_topic.editText?.text.toString())
                finish()
            }
        }
    }

    fun validateInput() : Boolean {
        if (text_input_topic.editText?.text.toString().trim().isEmpty()) {
            text_input_topic.error = getString(R.string.field_cant_be_empty)
            return false
        } else {
            val index = LearningManager.getTopicIndex(subject, text_input_topic.editText?.text.toString())
            if (index != null) {
                text_input_topic.error = getString(R.string.topic_already_exists)
                return false

            } else {
                text_input_topic.error = null
                return true
            }
        }
    }
}
