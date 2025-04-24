package com.example.exploralocal.presentation.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.exploralocal.R
import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.presentation.add.AddPlaceActivity
import com.example.exploralocal.presentation.map.MapFragment
import com.example.exploralocal.util.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceDetailFragment : Fragment() {

    private val viewModel: PlaceDetailViewModel by viewModels()
    private var currentPlace: Place? = null

    private lateinit var imageViewPlace: ImageView
    private lateinit var textViewName: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var buttonShowOnMap: Button
    private lateinit var buttonShare: Button

    companion object {
        fun newInstance(placeId: String): PlaceDetailFragment {
            val fragment = PlaceDetailFragment()
            val args = Bundle()
            args.putString(Constants.EXTRA_PLACE_ID, placeId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_place_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        imageViewPlace = view.findViewById(R.id.image_view_place)
        textViewName = view.findViewById(R.id.text_view_name)
        textViewDescription = view.findViewById(R.id.text_view_description)
        ratingBar = view.findViewById(R.id.rating_bar)
        buttonShowOnMap = view.findViewById(R.id.button_show_on_map)
        buttonShare = view.findViewById(R.id.button_share)

        setupMenu()
        setupListeners()

        // Load place data
        arguments?.getString(Constants.EXTRA_PLACE_ID)?.let { placeId ->
            viewModel.loadPlace(placeId)
        }

        observeViewModel()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.place_detail_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        editPlace()
                        true
                    }
                    R.id.menu_delete -> {
                        confirmDelete()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupListeners() {
        buttonShare.setOnClickListener {
            sharePlace()
        }

        buttonShowOnMap.setOnClickListener {
            showOnMap()
        }
    }

    private fun observeViewModel() {
        viewModel.place.observe(viewLifecycleOwner) { place ->
            currentPlace = place
            if (place != null) {
                displayPlaceDetails(place)
            } else {
                showError("No se pudo cargar el lugar")
            }
        }

        viewModel.deleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                Snackbar.make(
                    requireView(),
                    "Lugar eliminado correctamente",
                    Snackbar.LENGTH_SHORT
                ).show()
                navigateBack()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                showError(errorMsg)
            }
        }
    }

    private fun displayPlaceDetails(place: Place) {
        textViewName.text = place.name
        textViewDescription.text = place.description
        ratingBar.rating = place.rating

        // Set placeholder image for now
        imageViewPlace.setImageResource(R.drawable.placeholder_image)
    }

    private fun sharePlace() {
        currentPlace?.let { place ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, place.name)

                val shareText = "¡Mira este lugar que encontré!\n" +
                        "Nombre: ${place.name}\n" +
                        "Descripción: ${place.description}\n" +
                        "Calificación: ${place.rating}\n" +
                        "Ubicación: https://maps.google.com/?q=${place.latitude},${place.longitude}"

                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            startActivity(Intent.createChooser(shareIntent, "Compartir lugar"))
        }
    }

    private fun editPlace() {
        currentPlace?.let { place ->
            val intent = Intent(requireContext(), AddPlaceActivity::class.java).apply {
                putExtra(Constants.EXTRA_PLACE_ID, place.id)
            }
            startActivity(intent)
        }
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar lugar")
            .setMessage("¿Estás seguro de que deseas eliminar este lugar? Esta acción no se puede deshacer.")
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Eliminar") { _, _ ->
                currentPlace?.let { place ->
                    viewModel.deletePlace(place)
                }
            }
            .show()
    }

    private fun showOnMap() {
        currentPlace?.let { place ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.EXTRA_PLACE_ID, place.id)
                    }
                })
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateBack() {
        parentFragmentManager.popBackStack()
    }
}