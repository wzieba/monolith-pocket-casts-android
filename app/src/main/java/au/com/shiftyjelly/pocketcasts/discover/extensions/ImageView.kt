package au.com.shiftyjelly.pocketcasts.discover.extensions

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.AttrRes
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.ui.extensions.getThemeColor

fun ImageView.updateSubscribeButtonIcon(
    subscribed: Boolean,
    @AttrRes colorSubscribed: Int = R.attr.support_02,
    @AttrRes colorUnsubscribed: Int = R.attr.primary_icon_02,
) {
    val drawableRes = if (subscribed) R.drawable.ic_check_black_24dp else R.drawable.ic_add_black_24dp
    this.setImageResource(drawableRes)
    this.isEnabled = !subscribed
    this.contentDescription = this.context.getString(if (subscribed) R.string.podcast_subscribed else R.string.subscribe)

    val tintColor = context.getThemeColor(if (subscribed) colorSubscribed else colorUnsubscribed)
    this.imageTintList = ColorStateList.valueOf(tintColor)
}
