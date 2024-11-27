package au.com.shiftyjelly.pocketcasts.models.type

import androidx.annotation.StringRes
import com.squareup.moshi.JsonClass
import au.com.shiftyjelly.pocketcasts.localization.R as LR

@JsonClass(generateAdapter = false)
enum class SubscriptionFrequency(val label: String, @StringRes val localisedLabelRes: Int) {
    NONE("none", R.string.none),
    MONTHLY("monthly", R.string.plus_monthly),
    YEARLY("yearly", R.string.plus_yearly),
    ;
    override fun toString() = label
}
