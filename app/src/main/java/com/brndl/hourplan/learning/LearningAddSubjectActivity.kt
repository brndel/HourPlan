package com.brndl.hourplan.learning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.brndl.hourplan.R
import com.brndl.hourplan.subjects.SubjectItemAdapter
import com.brndl.hourplan.subjects.SubjectManager
import kotlinx.android.synthetic.main.activity_learing_add_subject.*

class LearningAddSubjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learing_add_subject)

        val arrayList = SubjectManager.getSubjectsForAdapter()
        for (subject in LearningManager.getLearnableSubjectArray()) {
            arrayList.remove(subject)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = SubjectItemAdapter(arrayList)
        recyclerView.adapter = adapter
        adapter.setOnClickListener(object: SubjectItemAdapter.OnItemClickListener {

            override fun onItemClick(position: Int, viewHolder: SubjectItemAdapter.SubjectItemViewHolder) {
                LearningManager.addSubject(arrayList[position].subject)
                finish()
            }

        })
    }
}
