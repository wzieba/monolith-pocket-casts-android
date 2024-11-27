package au.com.shiftyjelly.pocketcasts.views.multiselect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.R

sealed class MultiSelectAction(
    open val groupId: String,
    open val actionId: Int,
    @StringRes open val title: Int,
    @DrawableRes open val iconRes: Int,
    open val analyticsValue: String,
    open val isVisible: Boolean = true,
) {
    object SelectAll : MultiSelectAction(
        groupId = "select_all",
        actionId = R.id.menu_select_all,
        title = R.string.select_all,
        iconRes = R.drawable.ic_selectall_up,
        analyticsValue = "select_all",
    )
}
