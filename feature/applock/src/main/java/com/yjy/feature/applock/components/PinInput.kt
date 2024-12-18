package com.yjy.feature.applock.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.applock.R

private object PinInputDefaults {
    const val PIN_LENGTH = 4
    const val PIN_INDICATOR_SIZE = 16
    const val NUMBER_BUTTON_SIZE = 64
    const val NUMBER_PAD_SPACING = 16
    const val GRID_ROWS = 3
    const val NUMBERS_PER_ROW = 3
}

@Composable
fun PinInput(
    title: String,
    pin: String,
    onPinChanged: (String) -> Unit,
    onClose: () -> Unit,
    onPinComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CustomColorProvider.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(top = 12.dp, end = 12.dp)
                .align(Alignment.End),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.Close),
                contentDescription = stringResource(id = R.string.feature_applock_close),
                tint = CustomColorProvider.colorScheme.onBackground,
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = CustomColorProvider.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(PinInputDefaults.NUMBER_PAD_SPACING.dp)) {
                    repeat(PinInputDefaults.PIN_LENGTH) { index ->
                        Box(
                            modifier = Modifier
                                .size(PinInputDefaults.PIN_INDICATOR_SIZE.dp)
                                .background(
                                    color = if (index < pin.length) {
                                        CustomColorProvider.colorScheme.brandDim
                                    } else {
                                        CustomColorProvider.colorScheme.divider
                                    },
                                    shape = CircleShape,
                                ),
                        )
                    }
                }
            }
        }
        NumberPad(
            onNumberClick = { number ->
                if (pin.length < PinInputDefaults.PIN_LENGTH) {
                    val newPin = pin + number
                    onPinChanged(newPin)
                    if (newPin.length == PinInputDefaults.PIN_LENGTH) {
                        onPinComplete(newPin)
                    }
                }
            },
            onDelete = {
                if (pin.isNotEmpty()) {
                    onPinChanged(pin.dropLast(1))
                }
            },
            modifier = Modifier.padding(32.dp),
        )
    }
}

@Composable
private fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(PinInputDefaults.NUMBER_PAD_SPACING.dp),
    ) {
        for (row in 0 until PinInputDefaults.GRID_ROWS) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PinInputDefaults.NUMBER_PAD_SPACING.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                val startNumber = row * PinInputDefaults.NUMBERS_PER_ROW + 1
                for (number in startNumber until startNumber + PinInputDefaults.NUMBERS_PER_ROW) {
                    NumberButton(
                        number = number,
                        onClick = { onNumberClick(number) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(PinInputDefaults.NUMBER_PAD_SPACING.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            NumberButton(
                number = 0,
                onClick = { onNumberClick(0) },
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .size(PinInputDefaults.NUMBER_BUTTON_SIZE.dp)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.Backspace),
                    contentDescription = stringResource(id = R.string.feature_applock_backspace),
                    tint = CustomColorProvider.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
private fun NumberButton(
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(PinInputDefaults.NUMBER_BUTTON_SIZE.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = CustomColorProvider.colorScheme.onBackground,
        )
    }
}

fun Context.vibrate(duration: Long = 50L) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(duration)
    }
}
