package com.yjy.platform.widget.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.yjy.common.core.constants.DeepLinkConfig.SCHEME_AND_HOST
import com.yjy.common.core.constants.DeepLinkPath.STARTED

class WidgetClickAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val challengeId = parameters[CHALLENGE_ID]

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("${SCHEME_AND_HOST}/$STARTED/$challengeId"),
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    companion object {
        val CHALLENGE_ID = ActionParameters.Key<String>("challengeId")
    }
}
