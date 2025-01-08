package com.yjy.common.designsystem.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.common.User
import com.yjy.model.common.UserStatus

@Composable
fun User.getDisplayName(): String {
    return when (this.status) {
        UserStatus.ACTIVE -> this.name
        UserStatus.WITHDRAWN -> stringResource(id = R.string.common_designsystem_user_withdrawn)
        UserStatus.DELETED -> stringResource(id = R.string.common_designsystem_user_deleted)
    }
}

@Composable
fun User.getDisplayColor(
    activeColor: Color = CustomColorProvider.colorScheme.onBackground,
    inActiveColor: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
): Color {
    return when (this.status) {
        UserStatus.ACTIVE -> activeColor
        else -> inActiveColor
    }
}
