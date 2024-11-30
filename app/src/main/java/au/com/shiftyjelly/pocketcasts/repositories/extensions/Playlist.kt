package au.com.shiftyjelly.pocketcasts.repositories.extensions

import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.models.entity.Playlist

private const val FILTER_COLOR_SIZE = 5

private val ICON_DRAWABLES = listOf(
    R.drawable.ic_filters_list,
    R.drawable.ic_filters_headphones,
    R.drawable.ic_filters_clock,
    R.drawable.ic_filters_download,
    R.drawable.ic_filters_play,
    R.drawable.ic_filters_volume,
    R.drawable.ic_filters_video,
    R.drawable.ic_filters_star,
)

private val SHORTCUT_DRAWABLES = listOf(
    R.drawable.shortcut_list,
    R.drawable.shortcut_headphones,
    R.drawable.shortcut_clock,
    R.drawable.shortcut_download,
    R.drawable.shortcut_play,
    R.drawable.shortcut_volume,
    R.drawable.shortcut_video,
    R.drawable.shortcut_star,
)

private val AUTO_DRAWABLES = arrayOf(
    R.drawable.auto_filter_list,
    R.drawable.auto_filter_headphones,
    R.drawable.auto_filter_clock,
    R.drawable.auto_filter_downloaded,
    R.drawable.auto_filter_play,
    R.drawable.auto_filter_volume,
    R.drawable.auto_filter_video,
    R.drawable.auto_filter_star,
)

private val AUTOMOTIVE_DRAWABLES = arrayOf(
    R.drawable.automotive_filter_list,
    R.drawable.automotive_filter_headphones,
    R.drawable.automotive_filter_clock,
    R.drawable.automotive_filter_downloaded,
    R.drawable.automotive_filter_play,
    R.drawable.automotive_filter_volume,
    R.drawable.automotive_filter_video,
    R.drawable.automotive_filter_star,
)

val Playlist.drawableIndex: Int
    get() = iconId / FILTER_COLOR_SIZE % ICON_DRAWABLES.size

val Playlist.shortcutDrawableId: Int
    get() = SHORTCUT_DRAWABLES.getOrNull(drawableIndex) ?: SHORTCUT_DRAWABLES.first()

val Playlist.drawableId: Int
    get() = ICON_DRAWABLES.getOrNull(drawableIndex) ?: ICON_DRAWABLES.first()

val Playlist.autoDrawableId: Int
    get() = AUTO_DRAWABLES.getOrNull(drawableIndex) ?: AUTO_DRAWABLES.first()

val Playlist.automotiveDrawableId: Int
    get() = AUTOMOTIVE_DRAWABLES.getOrNull(drawableIndex) ?: AUTOMOTIVE_DRAWABLES.first()

val Playlist.colorIndex: Int
    get() = iconId % FILTER_COLOR_SIZE

val Playlist.Companion.iconDrawables: List<Int>
    get() = ICON_DRAWABLES

fun Playlist.Companion.calculateCombinedIconId(colorIndex: Int, iconIndex: Int): Int {
    return iconIndex * FILTER_COLOR_SIZE + colorIndex
}
