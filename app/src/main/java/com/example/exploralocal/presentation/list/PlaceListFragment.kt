package com.example.exploralocal.presentation.list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exploralocal.R
import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.presentation.detail.PlaceDetailFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceListFragment : Fragment(), PlaceAdapter.PlaceClickListener {

    private val viewModel: PlaceListViewModel by viewModels()
    private lateinit var placeAdapter: PlaceAdapter

    private lateinit var recyclerViewPlaces: RecyclerView
    private lateinit var textViewEmptyState: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerViewPlaces = view.findViewById(R.id.recycler_view_places)
        textViewEmptyState = view.findViewById(R.id.text_view_empty_state)

        setupRecyclerView()
        setupMenu()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        placeAdapter = PlaceAdapter(this)
        recyclerViewPlaces.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placeAdapter
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.place_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_sort_name -> {
                        viewModel.sortByName()
                        true
                    }
                    R.id.menu_sort_rating -> {
                        viewModel.sortByRating()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        viewModel.places.observe(viewLifecycleOwner) { places ->
            placeAdapter.submitList(places)

            // Show empty state or list
            if (places.isEmpty()) {
                textViewEmptyState.visibility = View.VISIBLE
                recyclerViewPlaces.visibility = View.GONE
            } else {
                textViewEmptyState.visibility = View.GONE
                recyclerViewPlaces.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Snackbar.make(requireView(), errorMsg, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onPlaceClick(place: Place) {
        // Navigate to detail fragment
        val detailFragment = PlaceDetailFragment.newInstance(place.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}