package com.yjy.platform.widget.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.yjy.platform.widget.R
import com.yjy.platform.widget.components.CircularProgressDefaults.Bitmap.THICKNESS_DIVIDER
import com.yjy.platform.widget.components.CircularProgressDefaults.FULL_CIRCLE_DEGREES
import com.yjy.platform.widget.components.CircularProgressDefaults.ICON_SIZE_RATIO
import com.yjy.platform.widget.components.CircularProgressDefaults.MIN_SDK_FOR_ARC
import com.yjy.platform.widget.components.CircularProgressDefaults.PROGRESS_START_ANGLE
import com.yjy.platform.widget.components.CircularProgressDefaults.Progress.BOTTOM_MULTIPLIER
import com.yjy.platform.widget.components.CircularProgressDefaults.Progress.LEFT_MULTIPLIER
import com.yjy.platform.widget.components.CircularProgressDefaults.Progress.START_BOTTOM
import com.yjy.platform.widget.components.CircularProgressDefaults.Progress.START_LEFT
import com.yjy.platform.widget.components.CircularProgressDefaults.Progress.START_RIGHT
import com.yjy.platform.widget.components.CircularProgressDefaults.Progress.START_TOP
import com.yjy.platform.widget.components.CircularProgressDefaults.RECT_SIDES
import com.yjy.platform.widget.theme.WidgetRadius

object CircularProgressDefaults {
    // Default sizes
    const val DEFAULT_SIZE = 100
    const val DEFAULT_SCALE_FACTOR = 2
    const val DEFAULT_THICKNESS = 8f

    // Progress calculation related
    const val PROGRESS_START_ANGLE = -90f
    const val FULL_CIRCLE_DEGREES = 360f

    // Rectangle divisions
    const val RECT_SIDES = 4

    // Icon size ratio
    const val ICON_SIZE_RATIO = 2

    // Android version specific
    const val MIN_SDK_FOR_ARC = Build.VERSION_CODES.S

    // Progress stroke settings
    object Progress {
        const val START_TOP = 0
        const val START_RIGHT = 1
        const val START_BOTTOM = 2
        const val START_LEFT = 3
        const val BOTTOM_MULTIPLIER = 2
        const val LEFT_MULTIPLIER = 3
    }

    // Bitmap configurations
    object Bitmap {
        const val THICKNESS_DIVIDER = 2f
    }
}

@Composable
fun GlanceCircularProgressBar(
    iconProvider: ImageProvider,
    iconColor: ColorProvider,
    progressColor: ColorProvider,
    backgroundColor: ColorProvider,
    modifier: GlanceModifier = GlanceModifier,
    percentage: Float? = null,
    size: Int = CircularProgressDefaults.DEFAULT_SIZE,
    scaleFactor: Int = CircularProgressDefaults.DEFAULT_SCALE_FACTOR,
    thickness: Float = CircularProgressDefaults.DEFAULT_THICKNESS,
) {
    val context = LocalContext.current
    val progressColorInt = progressColor.getColor(context).toArgb()

    val bitmapSize = size * scaleFactor
    val bitmapThickness = thickness * scaleFactor
    val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val progressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = bitmapThickness
        strokeCap = Paint.Cap.ROUND
        color = progressColorInt
    }

    percentage?.let { percent ->
        if (Build.VERSION.SDK_INT >= MIN_SDK_FOR_ARC) {
            val progressRect = RectF(
                bitmapThickness / THICKNESS_DIVIDER,
                bitmapThickness / THICKNESS_DIVIDER,
                bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
                bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
            )
            canvas.drawArc(
                progressRect,
                PROGRESS_START_ANGLE,
                FULL_CIRCLE_DEGREES * percent,
                false,
                progressPaint,
            )
        } else {
            val totalLength = (bitmapSize - bitmapThickness) * RECT_SIDES
            val progressLength = totalLength * percent

            // Top
            val topLength = minOf(progressLength, bitmapSize - bitmapThickness)
            if (topLength > START_TOP) {
                canvas.drawLine(
                    bitmapThickness / THICKNESS_DIVIDER,
                    bitmapThickness / THICKNESS_DIVIDER,
                    topLength + bitmapThickness / THICKNESS_DIVIDER,
                    bitmapThickness / THICKNESS_DIVIDER,
                    progressPaint,
                )
            }

            // Right
            if (progressLength > bitmapSize * START_RIGHT) {
                val rightLength = minOf(progressLength - bitmapSize, bitmapSize - bitmapThickness)
                canvas.drawLine(
                    bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
                    bitmapThickness / THICKNESS_DIVIDER,
                    bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
                    rightLength + bitmapThickness / THICKNESS_DIVIDER,
                    progressPaint,
                )
            }

            // Bottom
            if (progressLength > bitmapSize * START_BOTTOM) {
                val bottomLength = minOf(
                    progressLength - bitmapSize * BOTTOM_MULTIPLIER,
                    bitmapSize - bitmapThickness,
                )
                canvas.drawLine(
                    bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
                    bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
                    bitmapSize - bottomLength - bitmapThickness / THICKNESS_DIVIDER,
                    bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
                    progressPaint,
                )
            }

            // Left
            if (progressLength > bitmapSize * START_LEFT) {
                val leftLength = minOf(
                    progressLength - bitmapSize * LEFT_MULTIPLIER,
                    bitmapSize - bitmapThickness,
                )
                canvas.drawLine(
                    bitmapThickness / THICKNESS_DIVIDER,
                    bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
                    bitmapThickness / THICKNESS_DIVIDER,
                    bitmapSize - leftLength - bitmapThickness / THICKNESS_DIVIDER,
                    progressPaint,
                )
            }
        }
    }

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center,
    ) {
        Spacer(
            modifier = GlanceModifier
                .background(backgroundColor)
                .cornerRadius(WidgetRadius.full)
                .size(size.dp),
        )

        Image(
            provider = ImageProvider(bitmap),
            contentDescription = context.getString(R.string.platform_widget_progress_description),
            modifier = GlanceModifier.size(size.dp),
        )

        Image(
            provider = iconProvider,
            contentDescription = context.getString(R.string.platform_widget_category_icon_description),
            modifier = GlanceModifier.size((size / ICON_SIZE_RATIO).dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(iconColor),
        )
    }
}

@Composable
fun CircularProgressBar(
    icon: Painter,
    iconColor: Color,
    progressColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    percentage: Float? = null,
    size: Int = CircularProgressDefaults.DEFAULT_SIZE,
    scaleFactor: Int = CircularProgressDefaults.DEFAULT_SCALE_FACTOR,
    thickness: Float = CircularProgressDefaults.DEFAULT_THICKNESS,
) {
    val bitmapSize = size * scaleFactor
    val bitmapThickness = thickness * scaleFactor
    val bitmap = remember {
        Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    }

    val canvas = remember {
        Canvas(bitmap)
    }

    val progressPaint = remember {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = bitmapThickness
            strokeCap = Paint.Cap.ROUND
            color = progressColor.toArgb()
        }
    }

    val progressRect = remember {
        RectF(
            bitmapThickness / THICKNESS_DIVIDER,
            bitmapThickness / THICKNESS_DIVIDER,
            bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
            bitmapSize - bitmapThickness / THICKNESS_DIVIDER,
        )
    }

    LaunchedEffect(percentage) {
        canvas.drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)
        percentage?.let { percent ->
            canvas.drawArc(
                progressRect,
                PROGRESS_START_ANGLE,
                FULL_CIRCLE_DEGREES * percent,
                false,
                progressPaint,
            )
        }
    }

    androidx.compose.foundation.layout.Box(
        modifier = modifier.size(size.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(size.dp)
                .background(backgroundColor, CircleShape),
        )

        androidx.compose.foundation.Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.platform_widget_progress_description),
            modifier = Modifier.size(size.dp),
        )

        androidx.compose.foundation.Image(
            painter = icon,
            contentDescription = stringResource(R.string.platform_widget_category_icon_description),
            modifier = Modifier.size((size / ICON_SIZE_RATIO).dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconColor),
        )
    }
}
