package com.yjy.feature.findpassword.model

import com.yjy.feature.findpassword.FindPasswordViewModel.Companion.TIMER_MIN
import com.yjy.feature.findpassword.FindPasswordViewModel.Companion.TIMER_SEC

data class FindPasswordUiState(
    val email: String = "",
    val verifyCode: String = "",
    val isValidEmailFormat: Boolean = true,
    val isSendingVerifyCode: Boolean = false,
    val isVerifyingCode: Boolean = false,
    val canTrySendVerifyCode: Boolean = false,
    val minutes: Int = TIMER_MIN,
    val seconds: Int = TIMER_SEC,
)
