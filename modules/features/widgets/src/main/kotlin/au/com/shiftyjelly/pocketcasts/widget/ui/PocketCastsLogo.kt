package au.com.shiftyjelly.pocketcasts.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Box
import androidx.glance.layout.size
import au.com.shiftyjelly.pocketcasts.R

@Composable
internal fun PocketCastsLogo(
    size: Dp = 28.dp,
) {
    Box(
        modifier = GlanceModifier.size(size),
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_circle),
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalWidgetTheme.current.logoBackground),
            modifier = GlanceModifier.size(size),
        )
        Image(
            provider = ImageProvider(R.drawable.ic_logo_foreground),
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalWidgetTheme.current.logoLines),
            modifier = GlanceModifier.size(size),
        )
    }
}
