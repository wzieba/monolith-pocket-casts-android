package au.com.shiftyjelly.pocketcasts.preferences.model

import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.models.entity.BaseEpisode
import au.com.shiftyjelly.pocketcasts.models.entity.PodcastEpisode
import au.com.shiftyjelly.pocketcasts.models.entity.UserEpisode

interface ShelfRowItem

enum class ShelfItem(
    val id: String,
    val titleId: (BaseEpisode?) -> Int,
    val subtitleId: (BaseEpisode?) -> Int? = { null },
    val iconId: (BaseEpisode?) -> Int,
    val showIf: (BaseEpisode?) -> Boolean = { true },
    val analyticsValue: String,
) : ShelfRowItem {
    Effects(
        id = "effects",
        titleId = { R.string.podcast_playback_effects },
        iconId = { R.drawable.ic_effects_off },
        analyticsValue = "playback_effects",
    ),
    Sleep(
        id = "sleep",
        titleId = { R.string.player_sleep_timer },
        iconId = { R.drawable.ic_sleep },
        analyticsValue = "sleep_timer",
    ),
    Star(
        id = "star",
        titleId = { if (it is PodcastEpisode && it.isStarred) R.string.unstar_episode else R.string.star_episode },
        subtitleId = { episode -> R.string.player_actions_hidden_for_custom.takeIf { episode is UserEpisode } },
        iconId = { if (it is PodcastEpisode && it.isStarred) R.drawable.ic_star_filled else R.drawable.ic_star },
        showIf = { it is PodcastEpisode },
        analyticsValue = "star_episode",
    ),
    Transcript(
        id = "transcript",
        titleId = { R.string.transcript },
        iconId = { R.drawable.ic_transcript_24 },
        showIf = { it is PodcastEpisode },
        analyticsValue = "transcript",
    ),
    Download(
        id = "download",
        titleId = {
            when {
                it is PodcastEpisode && (it.isDownloading || it.isQueued) -> R.string.episode_downloading
                it is PodcastEpisode && it.isDownloaded -> R.string.remove_downloaded_file
                else -> R.string.download
            }
        },
        iconId = {
            when {
                it is PodcastEpisode && (it.isDownloading || it.isQueued) -> R.drawable.ic_download
                it is PodcastEpisode && it.isDownloaded -> R.drawable.ic_downloaded_24dp
                else -> R.drawable.ic_download
            }
        },
        subtitleId = { episode -> R.string.player_actions_hidden_for_custom.takeIf { episode is UserEpisode } },
        showIf = { it is PodcastEpisode },
        analyticsValue = "download",
    ),
    Share(
        id = "share",
        titleId = { R.string.podcast_share_episode },
        subtitleId = { episode -> R.string.player_actions_hidden_for_custom.takeIf { episode is UserEpisode } },
        iconId = { R.drawable.ic_share },
        showIf = { it is PodcastEpisode },
        analyticsValue = "share_episode",
    ),
    Podcast(
        id = "podcast",
        titleId = { if (it is UserEpisode) R.string.go_to_files else R.string.go_to_podcast },
        iconId = { R.drawable.ic_arrow_goto },
        analyticsValue = "go_to_podcast",
    ),
    Cast(
        id = "cast",
        titleId = { R.string.chromecast },
        iconId = { com.google.android.gms.cast.framework.R.drawable.quantum_ic_cast_connected_white_24 },
        analyticsValue = "chromecast",
    ),
    Played(
        id = "played",
        titleId = { R.string.mark_as_played },
        iconId = { R.drawable.ic_markasplayed },
        analyticsValue = "mark_as_played",
    ),
    Bookmark(
        id = "bookmark",
        titleId = { R.string.add_bookmark },
        iconId = { R.drawable.ic_bookmark },
        analyticsValue = "add_bookmark",
    ),
    Archive(
        id = "archive",
        titleId = { if (it is UserEpisode) R.string.delete else R.string.archive },
        subtitleId = { episode -> R.string.player_actions_show_as_delete_for_custom.takeIf { episode is UserEpisode } },
        iconId = { if (it is UserEpisode) R.drawable.ic_delete else R.drawable.ic_archive },
        analyticsValue = "archive",
    ),
    Report(
        id = "report",
        titleId = { R.string.report },
        subtitleId = { if (it is PodcastEpisode) R.string.report_subtitle else R.string.player_actions_hidden_for_custom },
        iconId = { R.drawable.ic_flag },
        showIf = { it is PodcastEpisode },
        analyticsValue = "report",
    ),
    ;

    // We can safely use the ID as server ID. Keeping it if need to make changes in the future.
    val serverId get() = id

    companion object {
        fun fromId(id: String) = entries.find { it.id == id }

        fun fromServerId(id: String) = entries.find { it.serverId == id }
    }
}

data class ShelfTitle(@StringRes val title: Int) : ShelfRowItem
