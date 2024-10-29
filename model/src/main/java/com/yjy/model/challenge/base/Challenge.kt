package com.yjy.model.challenge.base

import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.ParticipantInfo
import com.yjy.model.challenge.core.TargetDays

abstract class Challenge {
    abstract val id: String
    abstract val title: String
    abstract val description: String
    abstract val category: Category
    abstract val targetDays: TargetDays
    abstract val participantInfo: ParticipantInfo?
}
