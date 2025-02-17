package au.com.shiftyjelly.pocketcasts.player.view.bookmark.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.analytics.SourceView
import au.com.shiftyjelly.pocketcasts.compose.AppTheme
import au.com.shiftyjelly.pocketcasts.compose.buttons.UpsellButtonTitle
import au.com.shiftyjelly.pocketcasts.compose.images.HorizontalLogoText
import au.com.shiftyjelly.pocketcasts.compose.images.SubscriptionBadgeDisplayMode
import au.com.shiftyjelly.pocketcasts.compose.images.SubscriptionBadgeForTier
import au.com.shiftyjelly.pocketcasts.compose.preview.ThemePreviewParameterProvider
import au.com.shiftyjelly.pocketcasts.models.type.Subscription.SubscriptionTier
import au.com.shiftyjelly.pocketcasts.player.view.bookmark.components.UpsellViewModel.UiState
import au.com.shiftyjelly.pocketcasts.repositories.subscription.FreeTrial
import au.com.shiftyjelly.pocketcasts.ui.theme.Theme
import com.airbnb.android.showkase.annotation.ShowkaseComposable

@Composable
fun UpsellView(
    style: MessageViewColors,
    onClick: () -> Unit,
    sourceView: SourceView,
    modifier: Modifier = Modifier,
    viewModel: UpsellViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    when (state) {
        UiState.Loading -> Unit
        is UiState.Loaded -> {
            UpsellViewContent(
                style = style,
                state = state as UiState.Loaded,
                onClick = {
                    viewModel.onClick(sourceView)
                    onClick()
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun UpsellViewContent(
    style: MessageViewColors,
    state: UiState.Loaded,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(state.freeTrial.subscriptionTier.getContentDescription())
    MessageView(
        titleView = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clearAndSetSemantics { contentDescription = description },
            ) {
                HorizontalLogoText()
                Spacer(modifier = Modifier.width(8.dp))
                SubscriptionBadgeForTier(
                    tier = state.freeTrial.subscriptionTier,
                    displayMode = SubscriptionBadgeDisplayMode.Colored,
                )
            }
        },
        message = getMessage(
            state.showEarlyAccessMessage,
        ),
        buttonTitle = UpsellButtonTitle(
            state.freeTrial.subscriptionTier,
            state.freeTrial.exists,
        ),
        buttonAction = onClick,
        style = style,
        modifier = modifier,
    )
}

private fun SubscriptionTier.getContentDescription() = when (this) {
    SubscriptionTier.PATRON -> R.string.pocket_casts_patron
    SubscriptionTier.PLUS -> R.string.pocket_casts_plus
    SubscriptionTier.UNKNOWN -> throw IllegalStateException("Unknown subscription tier")
}

@Composable
private fun getMessage(
    showEarlyAccessMessage: Boolean,
) = if (showEarlyAccessMessage) {
    stringResource(R.string.bookmarks_upsell_instructions_early_access)
} else {
    stringResource(R.string.bookmarks_upsell_instructions)
}

@ShowkaseComposable(name = "UpsellView", group = "Subscriptions")
@Preview
@Composable
private fun UpsellViewPreview(
    @PreviewParameter(ThemePreviewParameterProvider::class) themeType: Theme.ThemeType,
) {
    AppTheme(themeType) {
        UpsellViewContent(
            style = MessageViewColors.Default,
            state = UiState.Loaded(
                freeTrial = FreeTrial(
                    exists = false,
                    subscriptionTier = SubscriptionTier.PLUS,
                ),
                showEarlyAccessMessage = false,
            ),
            onClick = {},
        )
    }
}
