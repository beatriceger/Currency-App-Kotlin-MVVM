package com.example.screens.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyapp.data.api.apis.CurrencyApi
import com.example.currencyapp.data.api.response.RateDetailed
import com.example.currencyapp.data.api.response.RateResponse
import com.example.currencyapp.domain.util.rx.AppRxSchedulers
import com.example.currencyapp.domain.util.rx.RxBus
import com.example.screens.main.utils.setCurrencyIcon
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


class MainActivityViewModel(
    private val compositeDisposable: CompositeDisposable,
    private val rxSchedulers: AppRxSchedulers,
    private val context: Context,
    private val currencyApi: CurrencyApi
) : ViewModel() {

    val rates = MutableLiveData<RateResponse>()
    val currencyClicked = MutableLiveData<RateDetailed>()
    var rateMultiplier: Double = 0.0

    val userInput = MutableLiveData<Double>()


    fun onCreate(syncRates: PublishSubject<Double>) {
        observeUserInput(syncRates)
    }

    fun getRates(baseRate: String) {
        compositeDisposable.add(
            currencyApi.getRates(baseRate)
                .subscribeOn(rxSchedulers.io())
                .observeOn(rxSchedulers.androidUI())
                .subscribe({
                    rates.postValue(it)
                    Timber.i("Currency data:  $it")
                }, {
                    Timber.e("No currency data  $it")
                })
        )
    }

    private fun observeUserInput(syncRates: PublishSubject<Double>) {
        compositeDisposable.add(
            syncRates
                .observeOn(rxSchedulers.androidUI())
                .subscribe(
                    {
                        userInput.postValue(it)
                    },
                    {
                        Timber.i(it.localizedMessage)
                    }
                ))
    }

    fun getDetailedRates(
        rateDate: RateResponse,
        baseRate: String,
        clickedRate: RateDetailed?
    ): MutableList<RateDetailed> {
        val detailedRatesList = mutableListOf<RateDetailed>()

        var inputMultiplier = 1.0
        if (clickedRate != null) {
            inputMultiplier = clickedRate.result.toDouble()
        }

        detailedRatesList.add(
            RateDetailed(
                setCurrencyIcon(context, baseRate.toLowerCase()),
                baseRate,
                "$baseRate lorem ipsum",
                inputMultiplier
                , inputMultiplier.toString()
            )
        )

        rateDate.rates.forEach {
            var result = it.value * inputMultiplier
            detailedRatesList.add(
                RateDetailed(
                    icon = setCurrencyIcon(context, it.key.toLowerCase(Locale.ENGLISH)),
                    rateName = it.key,
                    rateDescription = it.key + " lorem ipsum",
                    rateMultiplier = it.value
                    , result = result.toString()
                )
            )
        }
        return detailedRatesList
    }

    fun onCurrencyClicked(currency: RateDetailed) {
        currencyClicked.postValue(currency)
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }
}