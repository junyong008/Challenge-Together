package com.yjy.model.challenge

data class WaitingChallenge(
    override val id: String,
    override val title: String,
    override val description: String,
    override val category: Category,
    override val targetDays: TargetDays,
    override val currentParticipantCount: Int?,
    override val maxParticipantCount: Int?,
    val isPrivate: Boolean,
    val password: String? = null,
    val host: Participant? = null,
) : Challenge(
    id = id,
    title = title,
    description = description,
    category = category,
    targetDays = targetDays,
    currentParticipantCount = currentParticipantCount,
    maxParticipantCount = maxParticipantCount,
)
