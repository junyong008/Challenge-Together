package com.yjy.platform.widget

import android.content.Context
import com.yjy.platform.widget.widgets.ChallengeListWidget
import com.yjy.platform.widget.widgets.ChallengeSimpleWidget
import com.yjy.platform.widget.widgets.ChallengeTallWidget
import com.yjy.platform.widget.widgets.ChallengeWideWidget

object WidgetManager {
    suspend fun updateAllWidgets(context: Context) {
        ChallengeListWidget.updateWidget(context)
        ChallengeTallWidget.updateWidget(context)
        ChallengeWideWidget.updateWidget(context)
        ChallengeSimpleWidget.updateWidget(context)
    }
}
