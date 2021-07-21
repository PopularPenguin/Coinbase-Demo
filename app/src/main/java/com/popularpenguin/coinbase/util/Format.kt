package com.popularpenguin.coinbase.util

import java.text.NumberFormat
import java.util.*

object Format {
    fun formatUS(price: String?): String {
        price?.toDoubleOrNull()?.let { value ->
            val format = NumberFormat.getCurrencyInstance().apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
                currency = Currency.getInstance("USD")
            }

            return format.format(value)
        }

        return "$0.00"
    }
}
