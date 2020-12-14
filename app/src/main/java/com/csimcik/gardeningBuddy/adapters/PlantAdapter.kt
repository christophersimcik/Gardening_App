package com.csimcik.gardeningBuddy.adapters

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.hide
import com.csimcik.gardeningBuddy.models.PlantStub
import com.csimcik.gardeningBuddy.custom.ui.CustomOnPress
import com.csimcik.gardeningBuddy.viewModels.PlantViewModel

class PlantAdapter(var viewModel: PlantViewModel) :
    RecyclerView.Adapter<PlantAdapter.ViewHolder>() {
    companion object {
        const val TAG = "PLANTS_ADAPTER"
        const val ID = "ID"
        const val NAME = "NAME"
        const val NONE = "<i>None</i>"

    }

    private var data: List<PlantStub> = ArrayList()
    fun setData(list: List<PlantStub>?) {
        data = list ?: ArrayList()
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_plants, parent, false)
        return ViewHolder(view, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plantType = data[position]
        val imageUrl = plantType.image_url
        val scientificName = plantType.scientific_name
        val commonName = plantType.common_name
        val familyName = plantType.family
        val commonFamilyName = plantType.family_common_name
        val genusName = plantType.genus
        val synonyms = plantType.listSynonyms()
        holder.showImage(holder.imageView, imageUrl)
        holder.showText(holder.scientificName, scientificName)
        holder.showText(holder.commonName, commonName)
        holder.showText(holder.familyName, familyName)
        holder.showText(holder.commonFamilyName, commonFamilyName)
        holder.showText(holder.genusName, genusName)
        holder.showText(holder.synonyms, synonyms)
        holder.id = plantType.id.toString()
        Log.d(
            TAG, "" +
                    "genus =  ${plantType.links?.genus}" +
                    " self = ${plantType.links?.self}" +
                    " plant ${plantType.links?.plant}"
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View, viewModel: PlantViewModel) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val viewModel = viewModel
        val button: AppCompatImageButton =
            itemView.findViewById<androidx.appcompat.widget.AppCompatImageButton>(R.id.more_info_button)
        val imageView: AppCompatImageView =
            itemView.findViewById<AppCompatImageView>(R.id.plant_image)
        val scientificName: TextView = itemView.findViewById<TextView>(R.id.scientific_name)
        val commonName: TextView = itemView.findViewById<TextView>(R.id.common_name)
        val familyName: TextView = itemView.findViewById<TextView>(R.id.family_name)
        val commonFamilyName: TextView = itemView.findViewById<TextView>(R.id.common_family_name)
        val genusName: TextView = itemView.findViewById<TextView>(R.id.genus)
        val synonyms: TextView = itemView.findViewById<TextView>(R.id.synonyms)
        val main: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.main)

        //labels
        val scientificNameLabel: TextView = itemView.findViewById<TextView>(R.id.scientific_name_label)
        val commonNameLabel: TextView = itemView.findViewById<TextView>(R.id.common_name_label)
        val familyNameLabel: TextView = itemView.findViewById<TextView>(R.id.scientific_family_label)
        val commonFamilyNameLabel: TextView = itemView.findViewById<TextView>(R.id.common_family_label)
        val genusNameLabel: TextView = itemView.findViewById<TextView>(R.id.genus_lable)

        //dividers
        val scientificNameDivider: View = itemView.findViewById<View>(R.id.name_divider)
        val commonNameDivider: View = itemView.findViewById<View>(R.id.common_name_divider)
        val familyNameDivider: View = itemView.findViewById<View>(R.id.family_name_divider)
        val commonFamilyNameDivider: View = itemView.findViewById<View>(R.id.common_family_name_divider)
        var id = ""


        init {
            Log.d(TAG, "init")
            itemView.rootView.setOnClickListener(this)
            button.setOnClickListener {
                if (synonyms.visibility == View.VISIBLE) {
                    synonyms.visibility = View.GONE
                    button.background =
                        ResourcesCompat.getDrawable(it.context.resources, R.drawable.plus, null)
                } else {
                    synonyms.visibility = View.VISIBLE
                    button.background =
                        ResourcesCompat.getDrawable(it.context.resources, R.drawable.minus, null)
                }
            }

            itemView.setOnTouchListener(CustomOnPress())
        }

        override fun onClick(view: View?) {
            view?.let {
                val bundle = bundleOf(ID to id)
                bundle.putString(NAME, scientificName.text.toString())
                Navigation.findNavController(view).navigate(R.id.plantDetailFragment, bundle)
            }
        }

        fun showImage(view: AppCompatImageView, string: String?) {
            Glide.with(view.context)
                .load(string).placeholder(R.drawable.loading_image_rotate)
                .error(R.drawable.no_image_grn)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        view.context.resources.getDimension(R.dimen.family_item_view_radius).toInt()
                    )
                )
                .into(view)
        }

        fun showText(view: TextView, string: String?) {
            if (string != null && string != "") {
                if (string == NONE) view.text =
                    Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT) else view.text = string
            } else {
                hideEmptyTextViews(view)
            }
        }

        private fun hideEmptyTextViews(view: View) {
            view.hide()
            when (view.id) {
                R.id.scientific_name -> {
                    scientificNameLabel.hide()
                    scientificNameDivider.hide()
                }
                R.id.common_name -> {
                    commonNameLabel.hide()
                    commonNameDivider.hide()
                }
                R.id.family_name -> {
                    familyNameLabel.hide()
                    familyNameDivider.hide()
                }
                R.id.common_family_name -> {
                    commonFamilyNameLabel.hide()
                    commonFamilyNameDivider.hide()
                }
                R.id.genus -> {
                    genusNameLabel.hide()
                }
            }
        }
    }

}