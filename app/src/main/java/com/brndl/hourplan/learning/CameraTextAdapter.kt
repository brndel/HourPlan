package com.brndl.hourplan.learning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brndl.hourplan.R
import com.brndl.hourplan.select
import kotlinx.android.synthetic.main.item_selected_camera_text.view.*

class CameraTextAdapter(val itemList: ArrayList<LearningAddLearnableCameraActivity.CameraTextChip>) :
    RecyclerView.Adapter<CameraTextAdapter.CameraTextViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraTextViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_camera_text, parent, false)
        return CameraTextViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CameraTextViewHolder, position: Int) {

        val currentItem = itemList[position]

        holder.textView.text = currentItem.text
        holder.iconSplit.visibility =
            select(currentItem.childChips == null, View.GONE, View.VISIBLE) as Int


        holder.iconUnselect.setOnClickListener {
            currentItem.selected = false
        }
        holder.iconSplit.setOnClickListener {
            currentItem.split()
        }
        holder.iconDelete.setOnClickListener {
            currentItem.remove()
        }

    }


    class CameraTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardView = itemView.cardView
        val textView: TextView = itemView.textView
        val iconUnselect: ImageView = itemView.imageViewUnselect
        val iconDelete: ImageView = itemView.imageViewDelete
        val iconSplit: ImageView = itemView.imageViewSplit
    }
}