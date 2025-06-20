package com.yjy.common.core.util

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager

fun Modifier.keyboardHide(): Modifier = composed {
    val focusManager = LocalFocusManager.current

    var keyboardVisibleDuringThisFocusSession by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    val keyboardVisible by rememberKeyboardVisible()

    fun clear() {
        focusManager.clearFocus()
    }

    LaunchedEffect(keyboardVisible, hasFocus) {
        if (hasFocus) {
            if (keyboardVisible) {
                keyboardVisibleDuringThisFocusSession = true
            } else {
                if (keyboardVisibleDuringThisFocusSession) {
                    clear()
                }
            }
        } else {
            keyboardVisibleDuringThisFocusSession = false
        }
    }

    this
        .onFocusEvent {
            if (hasFocus != it.hasFocus) {
                hasFocus = it.hasFocus
            }
        }
        .pointerInput(Unit) {
            detectTapGestures {
                if (hasFocus && keyboardVisible) {
                    clear()
                }
            }
        }
}

@Composable
fun rememberKeyboardVisible(): State<Boolean> {
    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime

    return remember {
        derivedStateOf {
            imeInsets.getBottom(density) > 0
        }
    }
}
