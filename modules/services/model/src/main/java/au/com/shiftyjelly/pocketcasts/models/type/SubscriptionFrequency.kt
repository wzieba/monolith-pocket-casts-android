package au.com.shiftyjelly.pocketcasts.models.type

import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.R
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class SubscriptionFrequency(val label: String, @StringRes val localisedLabelRes: Int) {
    NONE("none", R.string.none),
    MONTHLY("monthly", R.string.plus_monthly),
    YEARLY("yearly", R.string.plus_yearly),
    ;
    override fun toString() = label
}
