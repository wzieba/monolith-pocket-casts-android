package au.com.shiftyjelly.pocketcasts.compose.toolbars.textselection

import au.com.shiftyjelly.pocketcasts.localization.R as LR

enum class CustomMenuItemOption(val id: Int) {
    Share(1),
    ;

    val titleResource: Int
        get() = when (this) {
            Share -> R.string.share
        }
    val order = id
}
