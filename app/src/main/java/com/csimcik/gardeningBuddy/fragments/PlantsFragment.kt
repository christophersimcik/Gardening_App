package com.csimcik.gardeningBuddy.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csimcik.gardeningBuddy.R
import com.csimcik.gardeningBuddy.adapters.PlantAdapter
import com.csimcik.gardeningBuddy.databinding.FragmentDivisionsBinding
import com.csimcik.gardeningBuddy.models.PlantStub
import com.csimcik.gardeningBuddy.models.Plants
import com.csimcik.gardeningBuddy.custom.ui.LoadingTracerView
import com.csimcik.gardeningBuddy.viewModels.PlantViewModel
import com.csimcik.gardeningBuddy.viewModels.PlantViewModel.TypeOfSearch

class PlantsFragment : Fragment() {

    private lateinit var plantAdapter: PlantAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataObserver: Observer<Plants?>
    private lateinit var loadingTextView: AppCompatTextView
    private lateinit var loadingViewTop: LoadingTracerView
    private lateinit var loadingViewBottom: LoadingTracerView
    private lateinit var loadingScreen: ConstraintLayout
    private lateinit var listObserver: Observer<List<PlantStub>>
    private lateinit var binding: FragmentDivisionsBinding
    private var observer = Observer<String> { search ->
        binding.query = search
    }
    private var onScrollListener = getOnScrollListener()

    private val viewModel: PlantViewModel by viewModels()

    companion object {
        const val TAG = "PLANTS_FRAGMENT"
        const val SEARCH = "SEARCH"
        const val SEARCH_TYPE = "SEARCH_TYPE"
        const val DURATION = 750L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CREATE")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        plantAdapter = PlantAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "ON CREATE VIEW")
        binding = FragmentDivisionsBinding.inflate(inflater, container, false)
        bindViews()
        recyclerView.adapter = plantAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        getFragmentBundleData()
        viewModel.searchString.observe(this.viewLifecycleOwner, observer)
        viewModel.getSearchString()
        dataObserver = initDataObserver()
        listObserver = initListObserver()
        observeData()
        observeList()
        return binding.root
    }

    private fun getFragmentBundleData() {
        val search = arguments?.getString(SEARCH) ?: ""
        val type: TypeOfSearch =
            (arguments?.getSerializable(SEARCH_TYPE) ?: TypeOfSearch.DEFAULT) as TypeOfSearch
        viewModel.setData(search, type)
    }

    private fun isLastPage(totalItemCount: Int): Boolean {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition() + 1
        return lastVisibleItemPosition == totalItemCount
    }

    private fun observeList() {
        viewModel.list.observe(viewLifecycleOwner, listObserver)
    }

    private fun observeData() {
        viewModel.getPlants().observe(viewLifecycleOwner, dataObserver)
    }

    private fun initDataObserver(): Observer<Plants?> {
        return Observer { plants ->
            if (plants == null) {
                informUserOfError()
            } else {
                viewModel.totalEntries = plants.metaData.total ?: (viewModel.list.value?.size ?: 0)
                val incomingList = plants.plants
                val outGoingList = viewModel.list.value ?: ArrayList(
                )
                if (!outGoingList.containsAll(incomingList)) outGoingList.addAll(incomingList)
                viewModel.list.value = outGoingList
            }
        }
    }

    private fun initListObserver(): Observer<List<PlantStub>> {
        return Observer {
            removeLoadingViews()
            if (it.isEmpty()) {
                if (this::loadingTextView.isInitialized) {
                    loadingTextView.text = resources.getString(R.string.no_plants_found)
                    goBackAfterDelay()
                }
            } else {
                if (this::loadingTextView.isInitialized) loadingTextView.visibility = View.GONE
            }
            recyclerView.visibility = View.VISIBLE
            plantAdapter.setData(it)
            recyclerView.addOnScrollListener(onScrollListener)
        }
    }

    private fun informUserOfError() {
        removeLoadingViews()
        if (this::loadingTextView.isInitialized) loadingTextView.text =
            context?.getString(R.string.invalid_search)
        view?.setBackgroundColor(Color.RED)
        goBackAfterDelay()
    }

    private fun removeLoadingViews() {
        if (this::loadingViewBottom.isInitialized && this::loadingViewTop.isInitialized) {
            loadingViewTop.visibility = View.GONE
            loadingViewBottom.visibility = View.GONE
        }
    }

    private fun goBackAfterDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            view?.let { Navigation.findNavController(it).popBackStack() }
        }, DURATION)
    }

    private fun getOnScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager
                val totalItemCount = layoutManager?.itemCount ?: 0
                if (totalItemCount < viewModel.totalEntries && isLastPage(totalItemCount)) {
                    recyclerView.removeOnScrollListener(onScrollListener)
                    viewModel.getNextPage()
                    observeData()
                }
            }
        }
    }

    private fun bindViews() {
        if (this::binding.isInitialized) {
            loadingViewTop = binding.loadingBlock.loadingImageTop
            loadingViewBottom = binding.loadingBlock.loadingImageBottom
            loadingTextView = binding.loadingBlock.loadingText
            loadingScreen = binding.loadingBlock.loadingContainer
            recyclerView = binding.recyclerView
        }
    }
}