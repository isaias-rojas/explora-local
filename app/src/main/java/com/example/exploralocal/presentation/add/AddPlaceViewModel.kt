package com.example.exploralocal.presentation.add

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
class AddPlaceViewModel @Inject constructor(
    private val placeUseCases: PlaceUseCases
) : ViewModel() {

    private val _savingState = MutableLiveData<Boolean>()
    val savingState: LiveData<Boolean> = _savingState

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun savePlace(place: Place) {
        viewModelScope.launch {
            try {
                placeUseCases.addPlace(place)
                _savingState.value = true
            } catch (e: Exception) {
                _error.value = "Error al guardar el lugar: ${e.localizedMessage}"
            }
        }
    }
}