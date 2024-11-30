package au.com.shiftyjelly.pocketcasts.account.onboarding.upgrade

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Brush
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.account.onboarding.upgrade.OnboardingUpgradeHelper.patronGradientBrush
import au.com.shiftyjelly.pocketcasts.account.onboarding.upgrade.OnboardingUpgradeHelper.plusGradientBrush
import au.com.shiftyjelly.pocketcasts.models.type.Subscription

sealed class UpgradeButton(
    @StringRes val shortNameRes: Int,
    @ColorRes val backgroundColorRes: Int,
    @ColorRes val textColorRes: Int,
    open val gradientBackgroundColor: Brush,
    open val subscription: Subscription,
    open val planType: PlanType,
) {
    data class Plus(
        override val subscription: Subscription,
        override val planType: PlanType,
    ) : UpgradeButton(
        shortNameRes = R.string.pocket_casts_plus_short,
        backgroundColorRes = R.color.plus_gold,
        textColorRes = R.color.black,
        subscription = subscription,
        planType = planType,
        gradientBackgroundColor = plusGradientBrush,
    )

    data class Patron(
        override val subscription: Subscription,
        override val planType: PlanType,
    ) : UpgradeButton(
        shortNameRes = R.string.pocket_casts_patron_short,
        backgroundColorRes = R.color.patron_purple,
        textColorRes = R.color.white,
        subscription = subscription,
        planType = planType,
        gradientBackgroundColor = patronGradientBrush,
    )

    enum class PlanType { RENEW, SUBSCRIBE, UPGRADE }
}

fun Subscription.toUpgradeButton(
    planType: UpgradeButton.PlanType = UpgradeButton.PlanType.SUBSCRIBE,
) = when (this.tier) {
    Subscription.SubscriptionTier.PLUS -> UpgradeButton.Plus(this, planType)
    Subscription.SubscriptionTier.PATRON -> UpgradeButton.Patron(this, planType)
    Subscription.SubscriptionTier.UNKNOWN -> throw IllegalStateException("Unknown subscription tier")
}
