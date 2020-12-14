package com.csimcik.gardeningBuddy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csimcik.gardeningBuddy.models.DivisionClass
import com.csimcik.gardeningBuddy.R

class DivisionClassAdapter : RecyclerView.Adapter<DivisionClassAdapter.ViewHolder>() {
    companion object {
        const val TAG = "DIVISION_CLASS_ADAPTER"
    }

    private var data: List<DivisionClass> = ArrayList()
    fun setData(list: List<DivisionClass>?) {
        data = list ?: ArrayList()
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_categories, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = data[position].name
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val textView: TextView = itemView.findViewById<TextView>(R.id.name)
        init{
            textView.setOnClickListener { this }
        }
        override fun onClick(p0: View?) {

        }
    }
}