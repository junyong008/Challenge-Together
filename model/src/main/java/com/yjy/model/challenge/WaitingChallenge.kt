package com.yjy.model.challenge

import com.yjy.model.challenge.base.Challenge
import com.yjy.model.challenge.base.PrivateChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Participant
import com.yjy.model.challenge.core.ParticipantInfo
import com.yjy.model.challenge.core.TargetDays

data class WaitingChallenge(
    override val id: String,
    override val title: String,
    override val description: String,
    override val category: Category,
    override val targetDays: TargetDays,
    override val participantInfo: ParticipantInfo?,
    override val isPrivate: Boolean,
    override val password: String?,
    val host: Participant?,
) : Challenge(), PrivateChallenge
