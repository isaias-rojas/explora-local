// com/example/exploralocal/presentation/map/MapViewModel.kt
package com.example.exploralocal.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.domain.usecase.PlaceUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placeUseCases: PlaceUseCases
) : ViewModel() {

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    private val _nearbyPlaces = MutableLiveData<List<Place>>()
    val nearbyPlaces: LiveData<List<Place>> = _nearbyPlaces

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private var showingNearby = false

    fun loadPlaces() {
        placeUseCases.getPlacesByName()
            .onEach { places ->
                _places.value = places
            }
            .launchIn(viewModelScope)
    }

    fun loadNearbyPlaces(latitude: Double, longitude: Double) {
        currentLatitude = latitude
        currentLongitude = longitude

        if (showingNearby) {
            fetchNearbyPlaces()
        }
    }

    fun showNearbyPlaces(show: Boolean) {
        showingNearby = show

        if (show && currentLatitude != null && currentLongitude != null) {
            fetchNearbyPlaces()
        } else {
            _nearbyPlaces.value = emptyList()
        }
    }

    private fun fetchNearbyPlaces() {
        viewModelScope.launch {
            val lat = currentLatitude ?: return@launch
            val lng = currentLongitude ?: return@launch

            val nearby = placeUseCases.getNearbyPlaces(lat, lng, 2.0)
            _nearbyPlaces.value = nearby
        }
    }
}