package com.yjy.platform.widget.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.yjy.common.core.constants.DeepLinkConfig.SCHEME_AND_HOST
import com.yjy.common.core.constants.DeepLinkPath.PREMIUM

object DeepLinkUtils {
    private fun createPremiumIntent() = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("${SCHEME_AND_HOST}/$PREMIUM"),
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP or
            Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    fun navigateToPremium(context: Context) {
        context.startActivity(createPremiumIntent())
    }
}
