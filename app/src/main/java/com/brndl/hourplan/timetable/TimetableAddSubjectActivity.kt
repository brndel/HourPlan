package com.brndl.hourplan.timetable

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.brndl.hourplan.R
import com.brndl.hourplan.subjects.SubjectInfo
import com.brndl.hourplan.subjects.SubjectItemAdapter
import com.brndl.hourplan.subjects.SubjectManager
import kotlinx.android.synthetic.main.activity_timetable_add_subject.*
import kotlinx.android.synthetic.main.subject_item.view.*

class TimetableAddSubjectActivity : AppCompatActivity() {

    var mSubjectItem = SubjectItem(-1)
    var day: Int = -1
    var positionIndex: Int = -1


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (positionIndex != -1) {
            menuInflater.inflate(R.menu.delete_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.item_delete -> {
                TimetableManager.removeSubjectAtDay(day, positionIndex)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable_add_subject)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        day = intent.getIntExtra("DAY_INDEX", -1)
        val dayString: String = TimetableManager.dayString(day, this)
        toolbar.title = getString(R.string.add_subject_to_format).format(dayString)

        if (intent.hasExtra("POSITION_INDEX")) {
            positionIndex = intent.getIntExtra("POSITION_INDEX", -1)
            mSubjectItem = TimetableManager.timetable[day][positionIndex].copy()
            edit_room.setText(mSubjectItem.room)
            toolbar.title =
                getString(R.string.edit_format).format(SubjectManager.getSubject(mSubjectItem.subject)?.name ?: getString(R.string.deleted_subject))
            addButton.text = getString(R.string.save_changes)
            updateSubjectItem()
        }


        val subjectList: MutableList<SubjectItem> =
            SubjectManager.getSubjectsForAdapter()
        if (TimetableManager.timetable[day].size > 0 && positionIndex == -1) {
            val lastIndex = TimetableManager.timetable[day].lastIndex
            if (TimetableManager.timetable[day][lastIndex].subject != -1)
                subjectList.add(
                    0,
                    TimetableManager.timetable[day][lastIndex]
                )
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = SubjectItemAdapter(subjectList)
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object : SubjectItemAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                viewHolder: SubjectItemAdapter.SubjectItemViewHolder
            ) {
                if (!subjectList[position].room.isNullOrEmpty())
                    edit_room.setText(subjectList[position].room)
                mSubjectItem.subject = subjectList[position].subject
                updateSubjectItem()
            }

        })


        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (edit_room.text.toString().isNotEmpty())
                    mSubjectItem.room = edit_room.text.toString()
                else mSubjectItem.room = null
                updateSubjectItem()
            }


        }
        edit_room.addTextChangedListener(textWatcher)

        addButton.setOnClickListener {
            if (mSubjectItem.subject != -1) {
                if (positionIndex == -1) TimetableManager.addSubjectAtDay(day, mSubjectItem)
                else TimetableManager.setSubjectAtDay(day, positionIndex, mSubjectItem)
                finish()
            }
        }

    }

    fun updateSubjectItem() {
        val subjectInfo: SubjectInfo? = SubjectManager.getSubject(mSubjectItem.subject)

        subjectItem.subjectText.text = subjectInfo?.name
        subjectItem.roomText.text = mSubjectItem.room
        subjectItem.teacherText.text = subjectInfo?.teacher
        val color = subjectInfo?.color
        if (color != null) (subjectItem as CardView).setCardBackgroundColor(color)
    }
}
