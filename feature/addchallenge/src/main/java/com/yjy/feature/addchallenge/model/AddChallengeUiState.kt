package com.yjy.feature.addchallenge.model

import com.yjy.common.core.constants.ChallengeConst.INIT_CHALLENGE_MAX_PARTICIPANTS
import com.yjy.common.core.constants.ChallengeConst.INIT_CHALLENGE_TARGET_DAYS
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.Mode
import com.yjy.model.challenge.TargetDays
import java.time.LocalDateTime

data class AddChallengeUiState(
    val mode: Mode? = null,
    val category: Category = Category.ALL,
    val title: String = "",
    val description: String = "",
    val startDateTime: LocalDateTime = LocalDateTime.now(),
    val targetDays: TargetDays = TargetDays.Fixed(INIT_CHALLENGE_TARGET_DAYS),
    val isAddingChallenge: Boolean = false,
    val maxParticipants: Int = INIT_CHALLENGE_MAX_PARTICIPANTS,
    val enableRoomPassword: Boolean = false,
    val roomPassword: String = "",
)
