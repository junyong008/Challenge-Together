package com.yjy.model.challenge

data class WaitingChallenge(
    override val id: String,
    override val title: String,
    override val description: String,
    override val targetDays: TargetDays,
    override val participants: List<Participant>,
    override val category: Category,
    val host: Participant,
    val isPrivate: Boolean,
) : Challenge(id, title, description, targetDays, participants, category)
