package au.com.shiftyjelly.pocketcasts.views.helper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.images.R as IR
import au.com.shiftyjelly.pocketcasts.localization.R as LR

sealed class NavigationIcon(@DrawableRes val icon: Int?, @StringRes val contentDescription: Int?) {
    object None : NavigationIcon(icon = null, contentDescription = null)
    object BackArrow : NavigationIcon(icon = R.drawable.ic_arrow_back, contentDescription = R.string.back)
    object Close : NavigationIcon(icon = R.drawable.ic_cancel, contentDescription = R.string.close)
    data class Custom(@DrawableRes val navigationIcon: Int, @StringRes val navigationContentDescription: Int) : NavigationIcon(icon = navigationIcon, contentDescription = navigationContentDescription)
}
