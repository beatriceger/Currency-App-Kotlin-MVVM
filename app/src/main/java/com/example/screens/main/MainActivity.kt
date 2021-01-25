package com.example.screens.main

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyapp.R
import com.example.currencyapp.data.api.response.RateDetailed
import com.example.currencyapp.data.api.response.RateResponse
import com.example.currencyapp.databinding.ActivityMainBinding
import com.example.currencyapp.domain.util.DELAY
import com.example.screens.main.adaptor.CurrencyAdapter
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.get
import timber.log.Timber
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel = get()

    private lateinit var currencyAdapter: CurrencyAdapter
    private val handler: Handler = Handler()
    val syncRates = PublishSubject.create<Double>()
    private var firstTime = false

    private var updater = Runnable {
        viewModel.getRates(baseRate)
    }

    private var doubleBackToExitPressedOnce: Boolean = false
    var baseRate = "EUR"
    private lateinit var rateWithDetailsList: MutableList<RateDetailed>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        viewModel.onCreate( syncRates)
        viewModel.getRates(baseRate)
        initAdapter()
        observeData()
    }

    private fun initAdapter() {
        currencyAdapter = CurrencyAdapter(this, viewModel, syncRates)
        binding.ratesList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun populateCurrencyAdaptor(rates: MutableList<RateDetailed>) {
        firstTime = true
        binding.ratesList.itemAnimator = null
        binding.ratesList.adapter = currencyAdapter
        currencyAdapter.setRatesList(rates)
        handler.removeCallbacks(updater)
        handler.postDelayed(
            updater,
            DELAY
        )
    }

    private fun observeData() {
        viewModel.rates.observe(this, Observer {
            createDetailedCurrency(it)
        })
        viewModel.currencyClicked.observe(this, Observer {
            handler.removeCallbacks(updater)
            currencyAdapter.currentRate = it
            currencyAdapter.inputMultiplier = it.result.toDouble()
            rearrangeItems(it)
            handler.postDelayed(updater, DELAY)
        })
        viewModel.userInput.observe(this, Observer {
            viewModel.getRates(baseRate)
        })
    }

    private fun rearrangeItems(item: RateDetailed) {
        if (item != rateWithDetailsList.first()) {
            baseRate = item.rateName.toString()
            currencyAdapter.swapItem(rateWithDetailsList.indexOf(item), 0)
            ratesList.scrollToPosition(0)
        }
    }

    private fun createDetailedCurrency(rateDate: RateResponse) {
        rateWithDetailsList =
            viewModel.getDetailedRates(rateDate, baseRate, currencyAdapter.currentRate)
        if (!firstTime) {
            populateCurrencyAdaptor(rateWithDetailsList)
        } else {
            currencyAdapter.clearRatesList()
            rateWithDetailsList.forEach {
                currencyAdapter.updateRates(
                    it,
                    rateWithDetailsList.indexOf(it)
                )
                handler.removeCallbacks(updater)
                handler.postDelayed(
                    updater,
                    DELAY
                )
            }
        }

    }

    private fun exitAppOnBackPress() {
        if (doubleBackToExitPressedOnce) {
            finish()
            exitProcess(0)
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.press_again), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        handler.removeCallbacks(updater)
        currencyAdapter.inputMultiplier = viewModel.rateMultiplier
        super.onDestroy()
    }

    override fun onBackPressed() {
        exitAppOnBackPress()
    }
}