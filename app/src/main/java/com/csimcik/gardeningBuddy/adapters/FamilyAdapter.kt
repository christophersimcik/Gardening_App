package com.csimcik.gardeningBuddy.adapters


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.databinding.ViewHolderCategoriesBinding
import com.csimcik.gardeningBuddy.fragments.PlantsFragment
import com.csimcik.gardeningBuddy.models.entities.FamilyDB
import com.csimcik.gardeningBuddy.custom.ColorHelper
import com.csimcik.gardeningBuddy.viewModels.PlantViewModel

class FamilyAdapter(val context: Context, var viewModel: PlantViewModel) :
    RecyclerView.Adapter<FamilyAdapter.ViewHolder>() {
    companion object {
        const val TOP = 0
        const val BOTTOM = 1
        const val TAG = "FAMILIES_ADAPTER"
    }

    private var data: List<FamilyDB> = ArrayList()
    fun setData(list: List<FamilyDB>?) {
        data = list ?: ArrayList()
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewHolderCategoriesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBinding(data[position])
        holder.setBackgrounds(context, data[position].color)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(val binding: ViewHolderCategoriesBinding,val viewModel: PlantViewModel) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        val name = binding.name
        private val commonName = binding.commonName

        init {
            itemView.rootView.setOnClickListener(this)
        }

        fun setBinding(family: FamilyDB){
            binding.family = family
            Log.d(TAG, "fam name is ${family.common_name}")
        }

        fun setBackgrounds(context: Context, color: Int) {
            val drawables = ColorHelper.getDrawables(context, color)
            name.background = drawables[TOP]
            commonName.background = drawables[BOTTOM]
        }

        override fun onClick(view: View?) {
            val text = validateSearchString()
            Log.d(TAG, "search string  = $text")
            done(view, text)
        }

        private fun done(view: View?, search: String) {
            val bundle = Bundle().also {
                it.putString(PlantsFragment.SEARCH, search)
                it.putSerializable(PlantsFragment.SEARCH_TYPE, PlantViewModel.TypeOfSearch.FAMILY)
            }
            view?.let { Navigation.findNavController(view).navigate(R.id.plantsFragment, bundle) }
        }

        private fun validateSearchString(): String{
            if(commonName.text.isEmpty()) return name.text.toString()
            val common = commonName.text.toString()
            return common.removeSuffix(" family")
        }
    }


}