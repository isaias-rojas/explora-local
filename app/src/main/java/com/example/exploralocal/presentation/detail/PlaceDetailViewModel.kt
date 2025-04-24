package com.example.exploralocal.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exploralocal.domain.model.Place
import com.example.exploralocal.domain.usecase.PlaceUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val placeUseCases: PlaceUseCases
) : ViewModel() {

    private val _place = MutableLiveData<Place?>()
    val place: LiveData<Place?> = _place

    private val _deleted = MutableLiveData<Boolean>()
    val deleted: LiveData<Boolean> = _deleted

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadPlace(placeId: String) {
        viewModelScope.launch {
            try {
                val place = placeUseCases.getPlaceById(placeId)
                _place.value = place
                if (place == null) {
                    _error.value = "No se encontró el lugar"
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar el lugar: ${e.localizedMessage}"
            }
        }
    }

    fun deletePlace(place: Place) {
        viewModelScope.launch {
            try {
                placeUseCases.deletePlace(place)
                _deleted.value = true
            } catch (e: Exception) {
                _error.value = "Error al eliminar el lugar: ${e.localizedMessage}"
            }
        }
    }
}