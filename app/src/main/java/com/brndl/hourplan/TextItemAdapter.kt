package com.brndl.hourplan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.text_item.view.*
import kotlin.coroutines.coroutineContext


data class TextItem(val text: String, val tooltipText: String? = null, val extraText: String? = null, val color: Int? = null)

class TextItemAdapter(val itemList : ArrayList<TextItem>, val setClickListeners: Boolean = true, var selecting: Boolean = false) : RecyclerView.Adapter<TextItemAdapter.TextItemViewHolder>() {

    var clickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, viewHolder: TextItemViewHolder)
        fun onItemLongClick(position:Int, viewHolder: TextItemViewHolder) : Boolean {
            return false
        }
    }

    fun setOnClickListener(listener: OnItemClickListener?) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.text_item, parent, false)
        return TextItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    val selectedViewHolders: ArrayList<TextItemViewHolder> = ArrayList()

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {

        val currentItem: TextItem = itemList.get(position)

        holder.text.text = currentItem.text
        holder.tooltipText.text = currentItem.tooltipText
        holder.extraText.text = currentItem.extraText
        holder.color = currentItem.color
        val col = currentItem.color
        if (col != null) {
            holder.layout.setBackgroundColor(col)
            holder.text.setTextColor(Color.rgb(255, 255, 255))
            holder.tooltipText.setTextColor(Color.argb(230, 255, 255, 255))
            holder.extraText.setTextColor(Color.argb(200, 255, 255, 255))
        }

        //set onclick listeners
        if(setClickListeners) {
            holder.itemView.setOnClickListener {
                clickListener?.onItemClick(holder.adapterPosition, holder)
                if (selecting) {
                    if (holder.selected) {
                        selectedViewHolders.remove(holder)
                    } else {
                        selectedViewHolders.add(holder)
                    }
                    holder.selected = !holder.selected
                }
            }
            holder.itemView.setOnLongClickListener {
                if (clickListener != null) clickListener!!.onItemLongClick(holder.adapterPosition, holder)
                else false
            }
        }
    }

    fun getSelectionStrings() : ArrayList<String> {
        val list: ArrayList<String> = ArrayList()
        for (holder in selectedViewHolders) {
            list.add(holder.text.text.toString())
        }
        return list
    }

    fun clearSelection() {
        for (holder in selectedViewHolders) {
            holder.selected = false
        }
        selectedViewHolders.clear()
    }

    class TextItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val layout: RelativeLayout = itemView.layout
        val text: TextView = itemView.text
        val tooltipText: TextView = itemView.textTooltip
        val extraText: TextView = itemView.textExtraInformation
        var color: Int? = null
        var selected: Boolean = false
        set(value) {
            if (value)
                layout.setBackgroundColor(Color.rgb(50, 150, 255))
            else {
                val col = color
                if (col == null)
                    layout.setBackgroundColor(Color.rgb(255, 255, 255))
                else
                    layout.setBackgroundColor(col)
            }
            field = value
        }
    }
}