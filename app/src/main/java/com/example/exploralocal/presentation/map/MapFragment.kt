package com.example.exploralocal.presentation.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.exploralocal.R
import com.example.exploralocal.databinding.FragmentMapBinding
import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.presentation.add.AddPlaceActivity
import com.example.exploralocal.presentation.detail.PlaceDetailFragment
import com.example.exploralocal.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val markerPlaceMap = mutableMapOf<Marker, Place>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getUserLocation()
        } else {
            Snackbar.make(
                binding.root,
                R.string.location_permission_denied,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Set up UI interactions
        binding.fabMyLocation.setOnClickListener {
            checkLocationPermission()
        }

        binding.checkboxNearbyPlaces.setOnCheckedChangeListener { _, isChecked ->
            viewModel.showNearbyPlaces(isChecked)
        }

        // Observe places to display on map
        viewModel.places.observe(viewLifecycleOwner) { places ->
            updateMapMarkers(places)
        }

        // Observe nearby places when requested
        viewModel.nearbyPlaces.observe(viewLifecycleOwner) { places ->
            updateNearbyMarkers(places)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set up map settings
        googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            setOnMapClickListener(this@MapFragment)
            setOnMarkerClickListener(this@MapFragment)
        }

        // Load all places
        viewModel.loadPlaces()

        // Try to get user location
        checkLocationPermission()
    }

    override fun onMapClick(latLng: LatLng) {
        // Open add place activity
        val intent = Intent(requireContext(), AddPlaceActivity::class.java).apply {
            putExtra(Constants.EXTRA_LATITUDE, latLng.latitude)
            putExtra(Constants.EXTRA_LONGITUDE, latLng.longitude)
        }
        startActivity(intent)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Get the place associated with this marker
        val place = markerPlaceMap[marker] ?: return false

        // Show place details
        showPlaceDetails(place)
        return true
    }

    private fun showPlaceDetails(place: Place) {
        val detailFragment = PlaceDetailFragment.newInstance(place.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateMapMarkers(places: List<Place>) {
        // Clear existing markers
        googleMap.clear()
        markerPlaceMap.clear()

        // Add markers for each place
        places.forEach { place ->
            val latLng = LatLng(place.latitude, place.longitude)
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(place.name)
            )

            marker?.let {
                markerPlaceMap[it] = place
            }
        }
    }

    private fun updateNearbyMarkers(places: List<Place>) {
        // Handle display of nearby places (different icon, etc.)
        places.forEach { place ->
            val latLng = LatLng(place.latitude, place.longitude)
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(place.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )

            marker?.let {
                markerPlaceMap[it] = place
            }
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getUserLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Show rationale and request permission
                Snackbar.make(
                    binding.root,
                    R.string.location_permission_required,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.grant) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getUserLocation() {
        try {
            googleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
                    )

                    // If "show nearby" is checked, load nearby places
                    if (binding.checkboxNearbyPlaces.isChecked) {
                        viewModel.loadNearbyPlaces(it.latitude, it.longitude)
                    }
                }
            }
        } catch (e: SecurityException) {
            // Handle permission denied
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}