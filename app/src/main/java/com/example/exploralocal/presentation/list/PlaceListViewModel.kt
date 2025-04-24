// com/example/exploralocal/presentation/list/PlaceListViewModel.kt
package com.example.exploralocal.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.domain.usecase.PlaceUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PlaceListViewModel @Inject constructor(
    private val placeUseCases: PlaceUseCases
) : ViewModel() {

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var sortByName = true

    init {
        loadPlaces()
    }

    fun sortByName() {
        sortByName = true
        loadPlaces()
    }

    fun sortByRating() {
        sortByName = false
        loadPlaces()
    }

    private fun loadPlaces() {
        val flow = if (sortByName) {
            placeUseCases.getPlacesByName()
        } else {
            placeUseCases.getPlacesByRating()
        }

        flow.onEach { placesList ->
            _places.value = placesList
        }.catch { e ->
            _error.value = "Error al cargar lugares: ${e.localizedMessage}"
        }.launchIn(viewModelScope)
    }
}