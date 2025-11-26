package com.example.truequesperu

import com.example.truequesperu.Models.ApiPeruResponse
import com.example.truequesperu.Models.RetrofitSunat
import com.example.truequesperu.Models.RucRequest

class SunatRepository {

    suspend fun consultarRuc(ruc: String): ApiPeruResponse {
        return RetrofitSunat.api.consultarRuc(RucRequest(ruc))
    }
}