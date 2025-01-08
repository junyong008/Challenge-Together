package com.yjy.domain

import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.data.user.api.NotificationRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.common.notification.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<PagingData<Notification>> =
        notificationRepository.getNotifications()
            .map { pagingData ->
                pagingData.map { notification ->
                    val timeDiff = userRepository.timeDiff.first()
                    notification.applyTimeDiff(timeDiff)
                }
            }

    private fun Notification.applyTimeDiff(timeDiff: Long): Notification =
        copy(createdDateTime = createdDateTime.plusSeconds(timeDiff))
}
