package com.brndl.hourplan.timetable

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brndl.hourplan.R
import com.brndl.hourplan.subjects.SubjectItemAdapter

class TimetableDayFragment(val position: Int, val timetableFragment: TimetableFragment) : Fragment() {

    lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        setupAdapter(mView)
    }

    override fun onResume() {
        super.onResume()
        setupAdapter(mView)
    }

    lateinit var adapter: SubjectItemAdapter
    var itemTouchHelper: ItemTouchHelper? = null

    fun setupAdapter(view: View) {

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter =
            SubjectItemAdapter(TimetableManager.timetable[position])
        recyclerView.adapter = adapter


        adapter.setOnClickListener(object: SubjectItemAdapter.OnItemClickListener {
            override fun onItemClick(itemPosition: Int, viewHolder: SubjectItemAdapter.SubjectItemViewHolder) {
                if (timetableFragment.isInEditMode) {
                    val intent = Intent(context, TimetableAddSubjectActivity::class.java)
                    intent.putExtra("DAY_INDEX", position)
                    intent.putExtra("POSITION_INDEX", itemPosition)
                    startActivity(intent)
                }
            }
        })
        itemTouchHelper?.attachToRecyclerView(null)
        itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            override fun onMove(recyclerView: RecyclerView, dragged: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                val position_dragged = dragged.adapterPosition
                val position_target = target.adapterPosition

                TimetableManager.switchSubjects(position, position_dragged, position_target)
                adapter.notifyItemMoved(position_dragged, position_target)
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        if (timetableFragment.isInEditMode)
            itemTouchHelper?.attachToRecyclerView(recyclerView)

        timetableFragment.addEditModeListener(object: TimetableFragment.OnEditModeChangendListener {
            override fun valueChanged(value: Boolean) {
                if (value)
                    itemTouchHelper?.attachToRecyclerView(recyclerView)
                else
                    itemTouchHelper?.attachToRecyclerView(null)
            }

        })
    }
}
