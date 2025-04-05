package com.example.projandroid2.network

import com.example.projandroid2.model.Ubs
import retrofit2.Call
import retrofit2.http.GET


interface UbsApi {
    @GET("Unidades")
    fun getUbs(): Call<List<Ubs>>
}
