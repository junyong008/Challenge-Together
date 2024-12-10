package com.yjy.feature.notificationsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.user.api.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val notificationSettings = userRepository.notificationSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ALL_NOTIFICATIONS_ENABLED,
        )

    fun setNotificationSetting(flag: Int, enabled: Boolean) = viewModelScope.launch {
        userRepository.setNotificationSetting(flag, enabled)
    }

    companion object {
        private const val ALL_NOTIFICATIONS_ENABLED = 0xFF
    }
}
