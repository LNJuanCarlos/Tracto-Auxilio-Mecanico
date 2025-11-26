package com.example.truequesperu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.truequesperu.Models.SunatResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SunatViewModel : ViewModel() {

    private val repo = SunatRepository()

    private val _estado = MutableStateFlow<SunatResponse?>(null)
    val estado: StateFlow<SunatResponse?> = _estado

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun buscarRuc(ruc: String) {
        viewModelScope.launch {
            try {
                val res = repo.consultarRuc(ruc)

                if (res.success && res.data != null) {

                    // Convertimos SunatData → SunatResponse (tu formato)
                    val data = res.data

                    _estado.value = SunatResponse(
                        numero = data.ruc ?: "",
                        nombre = data.nombre ?: "",
                        direccion = data.direccion,
                        estado = data.estado ?: "",
                        condicion = data.condicion ?: ""
                    )

                } else {
                    _error.value = "RUC no encontrado"
                }

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}