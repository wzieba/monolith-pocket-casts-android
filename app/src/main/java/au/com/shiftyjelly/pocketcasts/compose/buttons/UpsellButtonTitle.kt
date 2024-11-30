package au.com.shiftyjelly.pocketcasts.compose.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.models.type.Subscription.SubscriptionTier
import au.com.shiftyjelly.pocketcasts.utils.featureflag.Feature
import au.com.shiftyjelly.pocketcasts.utils.featureflag.FeatureFlag

@Composable
fun UpsellButtonTitle(
    tier: SubscriptionTier,
    hasFreeTrial: Boolean,
) = if (hasFreeTrial && !FeatureFlag.isEnabled(Feature.INTRO_PLUS_OFFER_ENABLED)) {
    stringResource(R.string.profile_start_free_trial)
} else {
    stringResource(
        R.string.upgrade_to,
        when (tier) {
            SubscriptionTier.PATRON -> stringResource(R.string.pocket_casts_patron_short)
            SubscriptionTier.PLUS -> stringResource(R.string.pocket_casts_plus_short)
            SubscriptionTier.UNKNOWN -> stringResource(R.string.pocket_casts_plus_short)
        },
    )
}
