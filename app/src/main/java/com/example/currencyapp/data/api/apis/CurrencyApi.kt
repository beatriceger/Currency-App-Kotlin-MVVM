package com.example.currencyapp.data.api.apis

import com.example.currencyapp.data.api.response.RateResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("api/android/latest")
    fun getRates(@Query("base") base: String): Observable<RateResponse>
}