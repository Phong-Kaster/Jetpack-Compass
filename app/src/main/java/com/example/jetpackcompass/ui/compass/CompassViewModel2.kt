package com.example.jetpackcompass.ui.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompass.data.repository.CompassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompassViewModel2(
    private val compassRepository: CompassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompassUiState())
    val uiState = _uiState.asStateFlow()



    fun start() {
        // Start compass once this ViewModel is alive
        compassRepository.start(viewModelScope)

        viewModelScope.launch {
            compassRepository.state.collect { compassState ->
                _uiState.value = _uiState.value.copy(
                    azimuth = compassState.azimuth,
                    directionText = compassState.directionText,
                    qiblaBearing = compassState.qiblaBearing
                )
            }
        }
    }


    fun stop() {
        compassRepository.stop()
    }

    override fun onCleared() {
        stop()
        super.onCleared()
    }
}
