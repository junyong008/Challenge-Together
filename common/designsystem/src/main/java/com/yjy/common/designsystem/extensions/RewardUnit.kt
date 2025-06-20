package com.yjy.common.designsystem.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.yjy.common.designsystem.R
import com.yjy.model.challenge.reward.RewardUnit
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun RewardUnit.getDisplayName(): String {
    return when (this) {
        RewardUnit.Money -> getCurrencySymbol()
        RewardUnit.Time -> stringResource(id = R.string.common_designsystem_reward_unit_time)
        is RewardUnit.Custom -> unit
    }
}

@Composable
fun RewardUnit.getDisplayName(amount: Double, maxFractionDigits: Int = 2): String {
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]

    return when (this) {
        is RewardUnit.Money -> {
            try {
                val currencyFormat = NumberFormat.getCurrencyInstance(locale).apply {
                    maximumFractionDigits = maxFractionDigits
                    minimumFractionDigits = if (amount % 1.0 == 0.0) 0 else 1
                }
                currencyFormat.format(amount)
            } catch (e: Exception) {
                "$amount ${getDisplayName()}"
            }
        }

        is RewardUnit.Time, is RewardUnit.Custom -> {
            val numberFormat = NumberFormat.getNumberInstance(locale).apply {
                maximumFractionDigits = maxFractionDigits
                minimumFractionDigits = if (amount % 1.0 == 0.0) 0 else 1
            }
            val formattedAmount = numberFormat.format(amount)
            val unit = getDisplayName()
            "$formattedAmount $unit"
        }
    }
}

@Composable
private fun getCurrencySymbol(): String {
    val context = LocalContext.current
    val locale: Locale = context.resources.configuration.locales[0]

    return try {
        Currency.getInstance(locale).getSymbol(locale)
    } catch (e: Exception) {
        ""
    }
}
