package com.brndl.hourplan

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.brndl.hourplan.timetable.TimetableDayFragment
import com.brndl.hourplan.timetable.TimetableFragment

val TAB_TITLES = arrayOf(
    R.string.monday_short,
    R.string.tuesday_short,
    R.string.wednesday_short,
    R.string.thursday_short,
    R.string.friday_short
)

class TabPagerAdapter(val context: Context?, fm: FragmentManager, val timetableFragment: TimetableFragment) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return TimetableDayFragment(position, timetableFragment)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context?.resources?.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 5
    }
}