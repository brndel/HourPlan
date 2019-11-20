package com.brndl.hourplan.learning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.brndl.hourplan.R
import com.brndl.hourplan.TextItemAdapter
import kotlinx.android.synthetic.main.activity_learning_test_result.*

class LearningTestResultActivity : AppCompatActivity() {

    class dataParcelable(val subject: Int, val topics: ArrayList<String>, val instant: ArrayList<Learnable>, val fewTries: ArrayList<Learnable>, val notKnown: ArrayList<Learnable>) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readArrayList(null) as ArrayList<String>,
            parcel.readArrayList(null) as ArrayList<Learnable>,
            parcel.readArrayList(null) as ArrayList<Learnable>,
            parcel.readArrayList(null) as ArrayList<Learnable>
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(subject)
            parcel.writeArray(topics.toArray())
            parcel.writeArray(instant.toArray())
            parcel.writeArray(fewTries.toArray())
            parcel.writeArray(notKnown.toArray())
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<dataParcelable> {
            override fun createFromParcel(parcel: Parcel): dataParcelable {
                return dataParcelable(parcel)
            }

            override fun newArray(size: Int): Array<dataParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }

    var subject: Int? = null
    var instant: ArrayList<Learnable> = ArrayList()
    var fewTries: ArrayList<Learnable> = ArrayList()
    var notKnown: ArrayList<Learnable> = ArrayList()
    var topicList: ArrayList<String> = ArrayList()

    var showingWords: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_test_result)

        recyclerViewWords.setHasFixedSize(true)
        recyclerViewWords.layoutManager = LinearLayoutManager(this)
        recyclerViewWords.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        val parcelable: dataParcelable = intent.getParcelableExtra("Parcelable")!!

        subject = parcelable.subject
        instant = parcelable.instant
        fewTries = parcelable.fewTries
        notKnown = parcelable.notKnown
        topicList = parcelable.topics

        textInstant.text = instant.size.toString()
        textFewTries.text = fewTries.size.toString()
        textNotKnown.text = notKnown.size.toString()

        buttonInstant.setOnClickListener {
            setAdapter(instant)
        }
        buttonInstant.setOnLongClickListener {
            addTopic(getString(R.string.instantly_known), instant, ContextCompat.getColor(this, R.color.colorRight))
            true
        }

        buttonFewTries.setOnClickListener {
            setAdapter(fewTries)
        }
        buttonFewTries.setOnLongClickListener {
            addTopic(getString(R.string.known_after_trys), fewTries, ContextCompat.getColor(this, R.color.colorRetry))
            true
        }

        buttonNotKnown.setOnClickListener {
            setAdapter(notKnown)
        }
        buttonNotKnown.setOnLongClickListener {
            addTopic(getString(R.string.not_known), notKnown, ContextCompat.getColor(this, R.color.colorWrong))
            true
        }

    }

    private fun addTopic(topic: String, list: ArrayList<Learnable>, color: Int) {
        val subj = subject
        if (subj != null) {
            var countedTopic= topic
            var counter = 1
            while (!LearningManager.addTopic(subj, countedTopic, topicsAsString(), color, 0)) {
                countedTopic = "%s %s".format(topic, counter)
                counter++
            }
            LearningManager.addLearnables(subj, countedTopic, list)
        }
    }

    private fun topicsAsString(): String {
        var string = topicList[0]
        for (i in 1..topicList.lastIndex) {
            string += ", " + topicList[i]
        }

        return string
    }


    private fun setAdapter(words: ArrayList<Learnable>) {
        showingWords = true
        viewFlipper.showNext()

        val adapter = TextItemAdapter(toTextItemArrayList(words))
        recyclerViewWords.adapter = adapter
    }

    override fun onBackPressed() {
        if (showingWords) {
            showingWords = false
            viewFlipper.showPrevious()
        } else
            super.onBackPressed()
    }
}
