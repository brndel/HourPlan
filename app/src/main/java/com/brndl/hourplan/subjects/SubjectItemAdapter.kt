package com.brndl.hourplan.subjects

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.brndl.hourplan.R
import com.brndl.hourplan.timetable.SubjectItem
import kotlinx.android.synthetic.main.subject_item.view.*

class SubjectItemAdapter(val itemList : MutableList<SubjectItem>, val small : Boolean = false, val setClickListeners: Boolean = true) : RecyclerView.Adapter<SubjectItemAdapter.SubjectItemViewHolder>() {

    var clickListener: OnItemClickListener? = null

     interface OnItemClickListener {
        fun onItemClick(position: Int, viewHolder: SubjectItemViewHolder)
         fun onItemLongClick(position:Int, viewHolder: SubjectItemViewHolder) : Boolean {
            return false
         }
    }

    fun setOnClickListener(listener: OnItemClickListener?) {
        clickListener = listener
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectItemViewHolder {
        context = parent.context
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.subject_item, parent, false)
        val svh = SubjectItemViewHolder(view)
        return svh
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SubjectItemViewHolder, position: Int) {

        if (small){
            holder.subjectText.textSize = 20f
            holder.roomText.textSize = 10f
            holder.teacherText.textSize = 8f
            holder.cardView.cardElevation = 1.5f
            holder.cardView.maxCardElevation = 1.5f
        }

        val currentItem: SubjectItem = itemList[position]

        val subjectInfo: SubjectInfo? = SubjectManager.getSubject(currentItem.subject)

        if (subjectInfo != null) holder.subjectText.text = subjectInfo.name
        else holder.subjectText.text = context.getText(R.string.deleted_subject)
        holder.roomText.text = currentItem.room
        holder.teacherText.text = subjectInfo?.teacher
        val color = subjectInfo?.color
        if(color != null) holder.cardView.setCardBackgroundColor(color)
        else holder.cardView.setCardBackgroundColor(Color.rgb(200, 200, 200))



        //set onclick listeners
        if(setClickListeners) {
            holder.itemView.setOnClickListener {
                clickListener?.onItemClick(holder.adapterPosition, holder)
            }
            holder.itemView.setOnLongClickListener {
                if (clickListener != null) clickListener!!.onItemLongClick(holder.adapterPosition, holder)
                else false
            }
        }

    }

    class SubjectItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.cardView
        val subjectText: TextView = itemView.subjectText
        val roomText: TextView = itemView.roomText
        val teacherText: TextView = itemView.teacherText
    }
}
