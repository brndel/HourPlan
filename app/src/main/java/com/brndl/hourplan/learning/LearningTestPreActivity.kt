package com.brndl.hourplan.learning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.brndl.hourplan.R
import kotlinx.android.synthetic.main.activity_learning_test_pre.*

class LearningTestPreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_test_pre)

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.test_types, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.setOnItemClickListener { adapterView, view, i, l ->  }
    }
}
