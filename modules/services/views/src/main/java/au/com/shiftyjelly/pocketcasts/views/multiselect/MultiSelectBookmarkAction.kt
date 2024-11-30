package au.com.shiftyjelly.pocketcasts.views.multiselect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.R

sealed class MultiSelectBookmarkAction(
    override val groupId: String,
    override val actionId: Int,
    @StringRes override val title: Int,
    @DrawableRes override val iconRes: Int,
    override val analyticsValue: String,
    override val isVisible: Boolean = true,
) : MultiSelectAction(
    groupId,
    actionId,
    title,
    iconRes,
    analyticsValue,
    isVisible,
) {
    data object DeleteBookmark : MultiSelectBookmarkAction(
        groupId = "delete",
        actionId = R.id.menu_delete,
        title = R.string.delete,
        iconRes = R.drawable.ic_delete,
        analyticsValue = "delete",
    )

    data class EditBookmark(override val isVisible: Boolean) : MultiSelectBookmarkAction(
        groupId = "edit",
        actionId = R.id.menu_edit,
        title = R.string.edit,
        iconRes = R.drawable.ic_edit,
        analyticsValue = "edit",
        isVisible = isVisible,
    )

    data class ShareBookmark(override val isVisible: Boolean) : MultiSelectBookmarkAction(
        groupId = "share",
        actionId = R.id.menu_share,
        title = R.string.share,
        iconRes = R.drawable.ic_share,
        analyticsValue = "share",
    )
}
