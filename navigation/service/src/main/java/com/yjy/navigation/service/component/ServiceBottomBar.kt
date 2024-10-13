package com.yjy.navigation.service.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.navigation.service.navigation.MainTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun ServiceBottomBar(
    mainTabs: ImmutableList<MainTab>,
    currentTab: MainTab?,
    onTabSelected: (MainTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(65.dp)
            .background(CustomColorProvider.colorScheme.surface),
    ) {
        HorizontalDivider(color = CustomColorProvider.colorScheme.divider, thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            mainTabs.forEach { tab ->
                ServiceBottomBarItem(
                    tab = tab,
                    selected = tab == currentTab,
                    onClick = { onTabSelected(tab) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.ServiceBottomBarItem(
    tab: MainTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .selectable(
                selected = selected,
                indication = null,
                role = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = if (selected) {
                painterResource(id = tab.selectedIconResId)
            } else {
                painterResource(id = tab.unselectedIconResId)
            },
            contentDescription = stringResource(id = tab.iconTextId),
            tint = if (selected) {
                CustomColorProvider.colorScheme.brand
            } else {
                CustomColorProvider.colorScheme.onSurfaceMuted
            },
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = tab.iconTextId),
            color = if (selected) {
                CustomColorProvider.colorScheme.brand
            } else {
                CustomColorProvider.colorScheme.onSurfaceMuted
            },
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@ComponentPreviews
@Composable
private fun ServiceBottomBarPreview() {
    ChallengeTogetherTheme {
        ServiceBottomBar(
            mainTabs = MainTab.entries.toImmutableList(),
            currentTab = MainTab.HOME,
            onTabSelected = {},
        )
    }
}