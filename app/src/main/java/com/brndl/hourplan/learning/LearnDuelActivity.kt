package com.brndl.hourplan.learning

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.brndl.hourplan.R
import kotlinx.android.synthetic.main.activity_learn_duel.*
import kotlin.collections.ArrayList

class LearnDuelActivity : AppCompatActivity() {

    val TestArrayList: MutableList<Learnable> = ArrayList()
    lateinit var currentQuestion: Learnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_duel)

        val subject: Int = intent.getIntExtra("SUBJECT", -1)
        val TopicArrayList = intent.getStringArrayListExtra("TOPIC_ARRAY")


        for(topic in TopicArrayList) {
            for(item in LearningManager.getLearnablesOfTopic(subject, topic)) {
                TestArrayList.add(item)
            }
        }
        TestArrayList.shuffle()

        if(TestArrayList.size <= 0) {
            Toast.makeText(this, "You cant learn something without content", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewFlipperButton2.showNext()

        textViewLives.text = playerLives.toString()
        textViewLives2.text = player2Lives.toString()

        nextQuestion(false)

        showButton.setOnClickListener {
            showAnswer()
            viewFlipperButton.showNext()
        }
        showButton2.setOnClickListener {
            showAnswer()
            viewFlipperButton2.showNext()
        }

        rightButton.setOnClickListener {
            TestArrayList.remove(currentQuestion)
            nextQuestion()

        }
        wrongButton.setOnClickListener {
            player2Lives -= 1
            textViewLives2.text = player2Lives.toString()
            if (player2Lives <= 0) {
                viewFlipperButton.visibility = View.GONE
                viewFlipperButton2.visibility = View.GONE
                textViewCenter.text = getText(R.string.shame_go_and_learn_more)
                textViewCenter.rotation = 180.toFloat()
                linearLayout.setBackgroundResource(R.drawable.gradient_red_white)
                textViewCenter.setTextColor(Color.rgb(255, 255, 255))
            } else {
                TestArrayList.remove(currentQuestion)
                nextQuestion()
            }
        }

        rightButton2.setOnClickListener {
            TestArrayList.remove(currentQuestion)
            nextQuestion()

        }
        wrongButton2.setOnClickListener {
            playerLives -= 1
            textViewLives.text = playerLives.toString()
            if (playerLives <= 0) {
                viewFlipperButton.visibility = View.GONE
                viewFlipperButton2.visibility = View.GONE
                textViewCenter.text = getText(R.string.shame_go_and_learn_more)
                textViewCenter.rotation = 0.toFloat()
                linearLayout.setBackgroundResource(R.drawable.gradient_white_red)
                textViewCenter.setTextColor(Color.rgb(255, 255, 255))
            } else {
                TestArrayList.remove(currentQuestion)
                nextQuestion()
            }
        }
    }
    var currentPlayer = true
    var playerLives = 3
    var player2Lives = 3

    fun showAnswer() {
        textViewCenter.text = currentQuestion.answer
        if (currentPlayer) textViewCenter.rotation = 0.toFloat()
        else textViewCenter.rotation = 180.toFloat()
    }


    fun nextQuestion(flipperShowNext: Boolean = true) {
        if(TestArrayList.size <= 0) {
            textViewCenter.text = ""
            textViewPlayer.text = getText(R.string.both_won)
            textViewPlayer2.text = getText(R.string.both_won)
            if (currentPlayer)
                viewFlipperButton.showNext()
            else
                viewFlipperButton2.showNext()
            return
        }
        currentQuestion = TestArrayList[0]
        currentPlayer = !currentPlayer

        if(flipperShowNext) {
            viewFlipperButton.showNext()
            viewFlipperButton2.showNext()
        }

        textViewCenter.text = currentQuestion.question
        if (currentPlayer) textViewCenter.rotation = 180.toFloat()
        else textViewCenter.rotation = 0.toFloat()
    }
}
