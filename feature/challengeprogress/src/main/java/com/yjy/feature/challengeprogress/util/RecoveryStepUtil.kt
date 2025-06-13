package com.yjy.feature.challengeprogress.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yjy.feature.challengeprogress.R
import com.yjy.feature.challengeprogress.model.RecoveryStep
import com.yjy.model.challenge.core.Category

@Composable
internal fun getRecoverySteps(category: Category): List<RecoveryStep> {
    return when (category) {
        Category.ALL -> getCommonRecoverySteps()
        Category.QUIT_SMOKING -> getQuitSmokingSteps()
        Category.QUIT_DRINKING -> getQuitAlcoholSteps()
        Category.QUIT_PORN -> getQuitPornSteps()
        Category.QUIT_GAMING -> getQuitGamingSteps()
        Category.QUIT_YOUTUBE -> getQuitYoutubeSteps()
        Category.QUIT_SNS -> getQuitSnsSteps()
        Category.QUIT_GLUTEN -> getQuitGlutenSteps()
        Category.QUIT_GAMBLING -> getQuitGamblingSteps()
        Category.QUIT_DRUGS -> getQuitDrugsSteps()
        Category.QUIT_CAFFEINE -> getQuitCaffeineSteps()
        Category.QUIT_EATING_OUT -> getQuitEatingOutSteps()
        Category.QUIT_SHOPPING -> getQuitShoppingSteps()
        Category.QUIT_SWEARING -> getQuitSwearingSteps()
        Category.QUIT_SMARTPHONE -> getQuitSmartphoneSteps()
    }
}

@Composable
private fun getCommonRecoverySteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_common_title_1,
        R.string.feature_challengeprogress_recovery_step_common_title_2,
        R.string.feature_challengeprogress_recovery_step_common_title_3,
        R.string.feature_challengeprogress_recovery_step_common_title_4,
        R.string.feature_challengeprogress_recovery_step_common_title_5,
        R.string.feature_challengeprogress_recovery_step_common_title_6,
        R.string.feature_challengeprogress_recovery_step_common_title_7,
        R.string.feature_challengeprogress_recovery_step_common_title_8,
        R.string.feature_challengeprogress_recovery_step_common_title_9,
        R.string.feature_challengeprogress_recovery_step_common_title_10,
        R.string.feature_challengeprogress_recovery_step_common_title_11,
        R.string.feature_challengeprogress_recovery_step_common_title_12,
        R.string.feature_challengeprogress_recovery_step_common_title_13,
        R.string.feature_challengeprogress_recovery_step_common_title_14,
        R.string.feature_challengeprogress_recovery_step_common_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_common_description_1,
        R.string.feature_challengeprogress_recovery_step_common_description_2,
        R.string.feature_challengeprogress_recovery_step_common_description_3,
        R.string.feature_challengeprogress_recovery_step_common_description_4,
        R.string.feature_challengeprogress_recovery_step_common_description_5,
        R.string.feature_challengeprogress_recovery_step_common_description_6,
        R.string.feature_challengeprogress_recovery_step_common_description_7,
        R.string.feature_challengeprogress_recovery_step_common_description_8,
        R.string.feature_challengeprogress_recovery_step_common_description_9,
        R.string.feature_challengeprogress_recovery_step_common_description_10,
        R.string.feature_challengeprogress_recovery_step_common_description_11,
        R.string.feature_challengeprogress_recovery_step_common_description_12,
        R.string.feature_challengeprogress_recovery_step_common_description_13,
        R.string.feature_challengeprogress_recovery_step_common_description_14,
        R.string.feature_challengeprogress_recovery_step_common_description_15,
    )

    val requireScores = intArrayOf(20, 60, 90, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000)

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitSmokingSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_smoking_description_15,
    )

    val requireScores = intArrayOf(20, 60, 90, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000)

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitPornSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_porn_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_porn_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitAlcoholSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_alcohol_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitGamingSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_gaming_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitYoutubeSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_youtube_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitSnsSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_sns_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_sns_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitGlutenSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_gluten_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitGamblingSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_gambling_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitDrugsSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_drugs_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitCaffeineSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_caffeine_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitEatingOutSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_eating_out_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitShoppingSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_shopping_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitSwearingSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_swearing_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}

@Composable
private fun getQuitSmartphoneSteps(): List<RecoveryStep> {
    val titleResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_1,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_2,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_3,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_4,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_5,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_6,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_7,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_8,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_9,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_10,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_11,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_12,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_13,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_14,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_title_15,
    )

    val descriptionResIds = intArrayOf(
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_1,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_2,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_3,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_4,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_5,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_6,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_7,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_8,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_9,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_10,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_11,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_12,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_13,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_14,
        R.string.feature_challengeprogress_recovery_step_quit_smartphone_description_15,
    )

    val requireScores = intArrayOf(
        20, 60, 70, 100, 250, 250, 250, 300, 500, 600, 600, 1000, 1200, 1800, 1000,
    )

    return titleResIds.mapIndexed { index, titleResId ->
        RecoveryStep(
            title = stringResource(id = titleResId),
            description = stringResource(id = descriptionResIds[index]),
            requireScore = requireScores[index],
        )
    }
}
