package com.csimcik.gardeningBuddy.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.csimcik.gardeningBuddy.databinding.ViewHolderCategoriesBinding
import com.csimcik.gardeningBuddy.models.Division
import com.csimcik.gardeningBuddy.custom.ColorHelper

class DivisionAdapter(val context : Context?) : RecyclerView.Adapter<DivisionAdapter.ViewHolder>() {
    companion object {
        const val TAG = "DIVISION_ADAPTER"
    }

    private var data: List<Division> = ArrayList()
    fun setData(list: List<Division>?) {
        data = list ?: ArrayList()
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewHolderCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = data[position].name
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(val binding: ViewHolderCategoriesBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        val name = binding.name
        val commonName = binding.commonName

        init {
            name.setOnClickListener(this)
            commonName.setOnClickListener(this)
        }

        fun setBackgrounds(context: Context, color : Int) {
            val drawables = ColorHelper.getDrawables(context, color)
            name.background = drawables[FamilyAdapter.TOP]
            commonName.background = drawables[FamilyAdapter.BOTTOM]
        }

        override fun onClick(view: View?) {
            Log.d(TAG, "division selected")
        }
    }
}