package com.yjy.model.challenge

import com.yjy.model.challenge.base.Challenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.User

data class DetailedWaitingChallenge(
    override val id: Int,
    override val title: String,
    override val description: String,
    override val category: Category,
    override val targetDays: TargetDays,
    val password: String,
    val author: User,
    val participants: List<User>,
    val maxParticipantCounts: Int,
    val isAuthor: Boolean,
    val isAuthorInActive: Boolean,
    val isParticipated: Boolean,
) : Challenge()
