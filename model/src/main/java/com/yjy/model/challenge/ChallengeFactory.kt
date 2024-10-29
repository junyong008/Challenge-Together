package com.yjy.model.challenge

import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.Participant
import com.yjy.model.challenge.core.ParticipantInfo
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime

object ChallengeFactory {
    fun createStartedChallenge(
        id: String,
        title: String,
        description: String,
        category: Category,
        targetDays: TargetDays,
        mode: Mode,
        recentResetDateTime: LocalDateTime,
        isCompleted: Boolean = false,
        maxCount: Int? = null,
        participants: List<Participant>? = null,
        currentRecordInSeconds: Long? = null,
        startDateTime: LocalDateTime? = null,
    ) = StartedChallenge(
        id = id,
        title = title,
        description = description,
        category = category,
        targetDays = targetDays,
        participantInfo = ParticipantInfo(
            currentCount = participants?.size ?: 0,
            maxCount = maxCount,
            participants = participants,
        ),
        mode = mode,
        recentResetDateTime = recentResetDateTime,
        isCompleted = isCompleted,
        currentRecordInSeconds = currentRecordInSeconds,
        startDateTime = startDateTime,
    )

    fun createWaitingChallenge(
        id: String,
        title: String,
        description: String,
        category: Category,
        targetDays: TargetDays,
        currentCount: Int,
        maxCount: Int,
        isPrivate: Boolean,
        password: String? = null,
        host: Participant? = null,
        participants: List<Participant>? = null,
    ) = WaitingChallenge(
        id = id,
        title = title,
        description = description,
        category = category,
        targetDays = targetDays,
        participantInfo = ParticipantInfo(
            currentCount = currentCount,
            maxCount = maxCount,
            participants = participants,
        ),
        isPrivate = isPrivate,
        password = password,
        host = host,
    )
}
