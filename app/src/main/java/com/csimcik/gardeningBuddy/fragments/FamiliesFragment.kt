package com.csimcik.gardeningBuddy.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.SHARED_PREFERENCES
import com.csimcik.gardeningBuddy.adapters.FamilyAdapter
import com.csimcik.gardeningBuddy.custom.ui.AlphabetView
import com.csimcik.gardeningBuddy.extensions.half
import com.csimcik.gardeningBuddy.models.entities.FamilyDB
import com.csimcik.gardeningBuddy.viewModels.FamilyViewModel
import com.csimcik.gardeningBuddy.viewModels.PlantViewModel
import kotlin.math.abs

class FamiliesFragment : Fragment(), AlphabetView.OnIndexChangedListener {
    private val viewModel: FamilyViewModel by viewModels()
    private val plantViewModel: PlantViewModel by viewModels()
    private lateinit var familyAdpater: FamilyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var indexView: AlphabetView
    private lateinit var observer: Observer<List<FamilyDB>>

    companion object { const val TAG = "DIVISIONS_FRAGMENT" }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "CREATE")
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        familyAdpater = FamilyAdapter(context, plantViewModel)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        if (this::recyclerView.isInitialized) {
            context?.let {
                val position =
                    viewModel.getScrollPosition(it.getSharedPreferences(SHARED_PREFERENCES, 0))
                recyclerView.scrollToPosition(position)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "ON CREATE VIEW")
        val view = inflater.inflate(R.layout.fragment_families, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        indexView = view.findViewById(R.id.index_view)
        indexView.onIndexChangedListener = this
        recyclerView.adapter = familyAdpater
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (indexView.isNotBeingScrolled) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstLetterOfName =
                        getFirstLetterOfName(recyclerView.layoutManager as LinearLayoutManager)
                    indexView.updateFromRecyclerViewScroll(firstLetterOfName)
                    setScrollPosition(getScrollPosition())
                }
            }
        })
        observer = Observer { families ->
            if (families.isNullOrEmpty()) viewModel.populateLocalDatabase()
            recyclerView.visibility = VISIBLE
            familyAdpater.setData(families)
            viewModel.makeIndex()
            Log.d(TAG, " size = ${families.size}")
        }
        viewModel.families?.observe(viewLifecycleOwner, observer)
        return view
    }

    override fun onIndexChanged(index: String) {
        val layoutManager = recyclerView.layoutManager
        val position: Int? = viewModel.index[index.first()]
        position?.let { layoutManager?.scrollToPosition(((viewModel.index[index.first()]) ?: 0)) }
        setScrollPosition(getScrollPosition())
    }

    private fun getFirstLetterOfName(layoutManager: LinearLayoutManager): String {
        val parent = layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition())
        val textView = parent?.rootView?.findViewById<AppCompatTextView>(R.id.name)
        return textView?.let {
            if (it.text.isNotBlank()) {
                it.text.first().toString()
            } else ""
        } ?: ""
    }

    private fun getScrollPosition(): Int {
        val first =
            (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        val last =
            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        val diff = abs(last - first)
        return first + diff.half()
    }

    private fun setScrollPosition(position: Int) {
        context?.let {
            viewModel.setScrollPosition(
                it.getSharedPreferences(SHARED_PREFERENCES, 0),
                position
            )
        }
    }

}