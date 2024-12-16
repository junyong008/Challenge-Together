package com.yjy.platform.widget.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class WidgetClickAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val challengeId = parameters[CHALLENGE_ID]

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("${DeepLink.SCHEME_AND_HOST}/${DeepLink.CHALLENGE_STARTED}/$challengeId"),
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
            component = ComponentName(
                context.packageName,
                DeepLink.SERVICE_ACTIVITY,
            )
        }
        context.startActivity(intent)
    }

    companion object {
        val CHALLENGE_ID = ActionParameters.Key<String>("challengeId")
    }
}
