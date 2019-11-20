package com.brndl.hourplan.learning

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brndl.hourplan.R
import com.brndl.hourplan.subjects.SubjectManager
import kotlinx.android.synthetic.main.activity_learning_test.*
import kotlinx.android.synthetic.main.layout_learning_flipper.view.*
import java.util.*
import kotlin.collections.ArrayList

class LearningTestActivity : AppCompatActivity() {

    data class TestLearnable(val learnbale: Learnable, var count: Int = 0)

    val TestArrayList: MutableList<TestLearnable> = ArrayList()
    lateinit var currentQuestion: TestLearnable
    val instant: ArrayList<Learnable> = ArrayList()
    val fewTries: ArrayList<Learnable> = ArrayList()
    val notKnown: ArrayList<Learnable> = ArrayList()
    var subject: Int = -1
    lateinit var topicArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_test)

        subject = intent.getIntExtra("SUBJECT", -1)
        topicArrayList = intent.getStringArrayListExtra("TOPIC_ARRAY") as ArrayList<String>

        val color: Int? = SubjectManager.getSubject(subject)?.color
        if (color != null) {
            val darkerColor: Int = Color.rgb(
                (Color.red(color) * 0.8).toInt(),
                (Color.green(color) * 0.8).toInt(),
                (Color.blue(color) * 0.8).toInt()
            )

            window.statusBarColor = darkerColor
            toolbar.background = ColorDrawable(color)
        }


        for (topic in topicArrayList) {
            for (item in LearningManager.getLearnablesOfTopic(subject, topic)) {
                TestArrayList.add(TestLearnable(item))
            }
        }
        TestArrayList.shuffle()

        nextQuestion()

        showButton.setOnClickListener {
            currentFlipperItem().textViewAnswer.visibility = View.VISIBLE
            currentFlipperItem().textViewExtraInfo.visibility = View.VISIBLE
            currentFlipperItem().imageView.visibility = View.VISIBLE
            viewFlipperButton.showNext()
        }

        rightButton.setOnClickListener {
            TestArrayList.remove(currentQuestion)
            if (currentQuestion.count == 0) {
                instant.add(currentQuestion.learnbale)
            } else {
                fewTries.add(currentQuestion.learnbale)
            }

            if (TestArrayList.size <= 0) {
                finish()
                openTestResults()
                return@setOnClickListener
            }
            nextQuestion()
            viewFlipperButton.showNext()
        }

        wrongButton.setOnClickListener {
            currentQuestion.count += 1
            if (currentQuestion.count >= 3) {
                TestArrayList.remove(currentQuestion)
                notKnown.add(currentQuestion.learnbale)
            }

            if (TestArrayList.size <= 0) {
                finish()
                openTestResults()
                return@setOnClickListener
            }
            Collections.rotate(
                TestArrayList,
                Random().nextInt(TestArrayList.size - TestArrayList.size / 2) + TestArrayList.size / 2
            )
            nextQuestion()
            viewFlipperButton.showNext()
        }
    }

    fun openTestResults() {
        val intent = Intent(this, LearningTestResultActivity::class.java)
        intent.putExtra(
            "Parcelable",
            LearningTestResultActivity.dataParcelable(
                subject,
                topicArrayList,
                instant,
                fewTries,
                notKnown
            )
        )
        startActivity(intent)
    }

    var currentFlipView = true


    fun nextQuestion() {
        if (TestArrayList.size <= 0) {
            Toast.makeText(this, "You cant learn something without content", Toast.LENGTH_LONG)
                .show()
            finish()
            return
        }
        currentQuestion = TestArrayList[0]
        currentFlipView = !currentFlipView
        viewFlipperText.showNext()
        updateTextViews()
    }

    fun updateTextViews() {
        if (currentQuestion.count > 0) {
            counterLayout.visibility = View.VISIBLE
            textViewCounter.text = currentQuestion.count.toString()
        } else {
            counterLayout.visibility = View.INVISIBLE
        }
        textViewLearnablesLeft.text = TestArrayList.size.toString()
        currentFlipperItem().textViewQuestion.text = currentQuestion.learnbale.question
        currentFlipperItem().textViewAnswer.text = currentQuestion.learnbale.answer
        currentFlipperItem().textViewExtraInfo.text = currentQuestion.learnbale.extraInfo
        if (currentQuestion.learnbale.ImageUri != null)
            currentFlipperItem().imageView.setImageURI(Uri.parse(currentQuestion.learnbale.ImageUri))
        currentFlipperItem().textViewAnswer.visibility = View.GONE
        currentFlipperItem().textViewExtraInfo.visibility = View.GONE
        currentFlipperItem().imageView.visibility = View.GONE
    }

    fun currentFlipperItem(): View {
        return if (currentFlipView) {
            flipper_item
        } else flipper_item2
    }
}
