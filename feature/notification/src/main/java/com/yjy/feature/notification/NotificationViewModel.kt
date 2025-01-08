package com.yjy.feature.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.user.api.NotificationRepository
import com.yjy.domain.GetNotificationsUseCase
import com.yjy.feature.notification.model.NotificationUiAction
import com.yjy.feature.notification.model.NotificationUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    getNotificationsUseCase: GetNotificationsUseCase,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _uiEvent = Channel<NotificationUiEvent>()
    val uiEvent: Flow<NotificationUiEvent> = _uiEvent.receiveAsFlow()

    val notifications = getNotificationsUseCase().cachedIn(viewModelScope)

    private fun sendEvent(event: NotificationUiEvent) = viewModelScope.launch {
        _uiEvent.send(event)
    }

    fun processAction(action: NotificationUiAction) {
        when (action) {
            is NotificationUiAction.OnDeleteItemClick -> deleteNotification(action.notificationId)
            NotificationUiAction.OnDeleteAllClick -> deleteAllNotifications()
        }
    }

    private fun deleteNotification(notificationId: Int) = viewModelScope.launch {
        notificationRepository.deleteNotification(notificationId)
            .onSuccess { sendEvent(NotificationUiEvent.DeleteSuccess) }
            .onFailure { sendEvent(NotificationUiEvent.DeleteFailed) }
    }

    private fun deleteAllNotifications() = viewModelScope.launch {
        notificationRepository.deleteAllNotifications()
            .onSuccess { sendEvent(NotificationUiEvent.DeleteSuccess) }
            .onFailure { sendEvent(NotificationUiEvent.DeleteFailed) }
    }
}
