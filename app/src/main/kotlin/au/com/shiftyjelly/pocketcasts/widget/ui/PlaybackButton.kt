package au.com.shiftyjelly.pocketcasts.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.widget.action.controlPlaybackAction
import au.com.shiftyjelly.pocketcasts.widget.data.LocalSource

@Composable
internal fun PlaybackButton(
    isPlaying: Boolean,
    iconPadding: Dp,
    modifier: GlanceModifier = GlanceModifier,
    isClickable: Boolean = true,
) {
    val contentDescription = LocalContext.current.getString(if (isPlaying) R.string.play_episode else R.string.pause_episode)
    val source = LocalSource.current

    RounderCornerBox(
        contentAlignment = Alignment.Center,
        backgroundTint = LocalWidgetTheme.current.buttonBackground,
        modifier = modifier.applyIf(isClickable) { ifModifier ->
            ifModifier
                .clickable(controlPlaybackAction(isPlaying, source))
                .semantics { this.contentDescription = contentDescription }
        },
    ) {
        Image(
            provider = ImageProvider(if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play),
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalWidgetTheme.current.icon),
            modifier = GlanceModifier.fillMaxSize().padding(vertical = iconPadding),
        )
    }
}
