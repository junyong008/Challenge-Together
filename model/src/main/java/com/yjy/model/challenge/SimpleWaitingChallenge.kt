package com.yjy.model.challenge

import com.yjy.model.challenge.base.Challenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays

data class SimpleWaitingChallenge(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val category: Category,
    override val targetDays: TargetDays,
    val isPrivate: Boolean,
    val currentParticipantCounts: Int,
    val maxParticipantCounts: Int,
) : Challenge()
