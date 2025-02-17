package au.com.shiftyjelly.pocketcasts.account.onboarding.upgrade.paywallreviews

import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.R

data class ReviewData(
    @StringRes val titleResourceId: Int,
    @StringRes val messageResourceId: Int,
    @StringRes val dateResourceId: Int,
)

internal val reviews: List<ReviewData> = listOf(
    ReviewData(
        titleResourceId = R.string.paywall_layout_reviews_review_one_title,
        messageResourceId = R.string.paywall_layout_reviews_review_one_message,
        dateResourceId = R.string.paywall_layout_reviews_review_one_date,
    ),
    ReviewData(
        titleResourceId = R.string.paywall_layout_reviews_review_two_title,
        messageResourceId = R.string.paywall_layout_reviews_review_two_message,
        dateResourceId = R.string.paywall_layout_reviews_review_two_date,
    ),
    ReviewData(
        titleResourceId = R.string.paywall_layout_reviews_review_three_title,
        messageResourceId = R.string.paywall_layout_reviews_review_three_message,
        dateResourceId = R.string.paywall_layout_reviews_review_three_date,
    ),
    ReviewData(
        titleResourceId = R.string.paywall_layout_reviews_review_four_title,
        messageResourceId = R.string.paywall_layout_reviews_review_four_message,
        dateResourceId = R.string.paywall_layout_reviews_review_four_date,
    ),
    ReviewData(
        titleResourceId = R.string.paywall_layout_reviews_review_five_title,
        messageResourceId = R.string.paywall_layout_reviews_review_five_message,
        dateResourceId = R.string.paywall_layout_reviews_review_five_date,
    ),
    ReviewData(
        titleResourceId = R.string.paywall_layout_reviews_review_six_title,
        messageResourceId = R.string.paywall_layout_reviews_review_six_message,
        dateResourceId = R.string.paywall_layout_reviews_review_six_date,
    ),
)
