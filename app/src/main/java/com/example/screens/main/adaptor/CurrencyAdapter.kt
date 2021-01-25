package com.example.screens.main.adaptor

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyapp.data.api.response.RateDetailed
import com.example.currencyapp.databinding.CurrencyElementBinding
import com.example.screens.main.MainActivityViewModel
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*


class CurrencyAdapter(
    private val context: Context,
    private val viewModel: MainActivityViewModel,
    private val syncRates: PublishSubject<Double>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ratesList: MutableList<RateDetailed> = mutableListOf()
    private var firstView: EditText? = null
    var currentRate: RateDetailed? = null
    var inputMultiplier: Double = 1.0

    private val textListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrEmpty()) {
                try {
                    inputMultiplier = s.toString().toDouble()
                    notifyItemRangeChanged(1, itemCount - 1)
                } catch (e: Exception) {
                    Timber.e("Exception: %s", e)
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun setRatesList(rates: MutableList<RateDetailed>) {
        this.ratesList.clear()
        this.ratesList.addAll(rates)
    }

    fun clearRatesList() {
        this.ratesList.clear()
    }

    fun updateRates(item: RateDetailed, index: Int) {
        ratesList.add(index, item)
        if (index > 0) {
            notifyItemChanged(index)
        }
    }

    override fun getItemCount(): Int = ratesList.size

    override fun getItemId(position: Int): Long {
        return ratesList[position].rateName.hashCode().toLong()
    }

    fun swapItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(ratesList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is BaseViewHolder) {
            with(holder as BaseViewHolder) {
                if (adapterPosition == 0) {
                    binding.rateMultiplier.removeTextChangedListener(textListener)
                }
            }
        }

        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_BASE
        } else {
            TYPE_RATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CurrencyElementBinding.inflate(inflater, parent, false)

        return when (viewType) {
            TYPE_BASE -> {
                BaseViewHolder(textListener, binding)
            }
            else -> {
                ViewHolder(textListener, binding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val rate = ratesList[position]
        when (viewHolder) {
            is BaseViewHolder -> {
                rate.result = inputMultiplier.toString()
                viewHolder.bind(rate)
            }
            is ViewHolder -> {
                rate.result = (inputMultiplier * rate.rateMultiplier).toString()
                viewHolder.bind(rate)
            }
        }
    }

    inner class BaseViewHolder(
        val txtListener: TextWatcher,
        val binding: CurrencyElementBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rate: RateDetailed) {
            binding.viewModel = viewModel
            binding.currency = rate
            rate.icon?.let { binding.rateIcon.setImageResource(it) }
            binding.rateMultiplier.post { binding.rateMultiplier.setText(rate.result) }
            binding.rateMultiplier.addTextChangedListener(textListener)
            firstView = binding.rateMultiplier
            binding.rateMultiplier.requestFocus()
            binding.rateMultiplier.setSelection(binding.rateMultiplier.length())
            binding.rateMultiplier.addTextChangedListener(txtListener)
        }
    }

    inner class ViewHolder(
        val txtListener: TextWatcher,
        val binding: CurrencyElementBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rate: RateDetailed) {
            binding.rateMultiplier.removeTextChangedListener(txtListener)
            binding.viewModel = viewModel
            binding.currency = rate
            rate.icon?.let { binding.rateIcon.setImageResource(it) }
            binding.rateMultiplier.post { binding.rateMultiplier.setText(rate.result) }
        }

    }

    companion object {
        const val TYPE_BASE = 0
        const val TYPE_RATE = 1
    }
}


