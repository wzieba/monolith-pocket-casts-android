package au.com.shiftyjelly.pocketcasts.preferences.model

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.R

enum class NewEpisodeNotificationAction(
    val id: Int,
    val serverId: String,
    @StringRes val labelId: Int,
    @DrawableRes val drawableId: Int,
    @DrawableRes val largeDrawableId: Int,
) {
    Play(
        id = 1,
        serverId = "play",
        labelId = R.string.play,
        drawableId = R.drawable.notification_action_play,
        largeDrawableId = R.drawable.notification_action_play_large,
    ),
    PlayNext(
        id = 2,
        serverId = "play_next",
        labelId = R.string.play_next,
        drawableId = R.drawable.notification_action_playnext,
        largeDrawableId = R.drawable.notification_action_playnext_large,
    ),
    PlayLast(
        id = 3,
        serverId = "play_last",
        labelId = R.string.play_last,
        drawableId = R.drawable.notification_action_playlast,
        largeDrawableId = R.drawable.notification_action_playlast_large,
    ),
    Archive(
        id = 4,
        serverId = "archive",
        labelId = R.string.archive,
        drawableId = R.drawable.notification_action_archive,
        largeDrawableId = R.drawable.notification_action_archive_large,
    ),
    Download(
        id = 5,
        serverId = "download",
        labelId = R.string.download,
        drawableId = R.drawable.notification_action_download,
        largeDrawableId = R.drawable.notification_action_download_large,
    ),
    ;

    companion object {
        val DefaultValues = listOf(Play, PlayNext, Download)

        fun labels(resources: Resources) = entries.map { resources.getString(it.labelId) }

        fun fromLabels(labels: List<String>, resources: Resources) = labels.mapNotNull { fromLabel(it, resources) }

        private fun fromLabel(label: String, resources: Resources) = entries.find { action ->
            val actionLabel = resources.getString(action.labelId)
            actionLabel == label
        }

        fun fromServerId(id: String) = entries.find { it.serverId == id }
    }
}
