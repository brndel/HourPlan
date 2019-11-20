package com.brndl.hourplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.brndl.hourplan.subjects.SubjectItemAdapter
import com.brndl.hourplan.timetable.TimetableManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_home_day.view.*
import java.util.*


class HomeFragment(private val mainActivity: MainActivity) : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todayIndex = TimetableManager.indexOfDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK), true)
        val tomorrowIndex = TimetableManager.indexOfDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 1)

        val cntx = context

        today.textViewRelativeDay.text = getText(R.string.today)
        if(cntx != null)
        today.textViewDay.text = TimetableManager.dayString(todayIndex, cntx)
        today.recyclerView.setHasFixedSize(true)
        today.recyclerView.layoutManager = LinearLayoutManager(context)
        if(todayIndex <= 4) {
            today.recyclerView.adapter =
                SubjectItemAdapter(TimetableManager.timetable[todayIndex], true, false)
        }

        if(todayIndex + 1 == tomorrowIndex)
            tomorrow.textViewRelativeDay.text = getText(R.string.tomorrow)

        else
            tomorrow.textViewRelativeDay.text = getText(R.string.next_day)
        if(cntx != null)
            tomorrow.textViewDay.text = TimetableManager.dayString(tomorrowIndex, cntx)
        tomorrow.recyclerView.setHasFixedSize(true)
        tomorrow.recyclerView.layoutManager = LinearLayoutManager(context)
        tomorrow.recyclerView.adapter = SubjectItemAdapter(
            TimetableManager.timetable[tomorrowIndex],
            true,
            false
        )

        today.setOnClickListener {
            mainActivity.goToTimetable(todayIndex)
        }

        tomorrow.setOnClickListener {
            mainActivity.goToTimetable(tomorrowIndex)
        }


    }
}