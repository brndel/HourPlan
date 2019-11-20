package com.brndl.hourplan.timetable

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.brndl.hourplan.R
import com.brndl.hourplan.TabPagerAdapter
import kotlinx.android.synthetic.main.fragment_timetable.*


class TimetableFragment(val dayIndex: Int = 0) : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewpager.adapter = TabPagerAdapter(context, childFragmentManager, this)
        tabLayout.setupWithViewPager(viewpager)
        tabLayout.getTabAt(dayIndex)?.select()

        addButton.setOnClickListener {
            val intent = Intent(context, TimetableAddSubjectActivity::class.java)
            intent.putExtra("DAY_INDEX", tabLayout.selectedTabPosition)
            startActivity(intent)
        }
    }

    var mListeners: ArrayList<OnEditModeChangendListener> = arrayListOf()

    fun addEditModeListener(listener: OnEditModeChangendListener){
        mListeners.add(listener)
    }

    interface OnEditModeChangendListener {

        fun valueChanged(value: Boolean)

    }

    var isInEditMode: Boolean = false
        get() = field

        set(value) {
            if (value) addButton.show()
            else addButton.hide()
            for(listener in mListeners) {
                listener.valueChanged(value)
            }
            field = value
        }

    fun switchIsInEditMode() {
        isInEditMode = !isInEditMode
    }

    fun onOptionMenuItemSelected(item: MenuItem) {
        if (item.itemId == R.id.item_edit) {
            switchIsInEditMode()
        }
    }
}