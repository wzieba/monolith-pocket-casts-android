package au.com.shiftyjelly.pocketcasts.settings

import android.content.Context
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsEvent
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsTracker
import au.com.shiftyjelly.pocketcasts.analytics.SourceView
import au.com.shiftyjelly.pocketcasts.preferences.Settings
import au.com.shiftyjelly.pocketcasts.utils.Util
import au.com.shiftyjelly.pocketcasts.utils.featureflag.Feature
import au.com.shiftyjelly.pocketcasts.utils.featureflag.FeatureFlag
import au.com.shiftyjelly.pocketcasts.utils.isDeviceRunningOnLowStorage
import au.com.shiftyjelly.pocketcasts.views.dialog.ConfirmationDialog

class LowStorageDialogPresenter(
    val context: Context,
    val analyticsTracker: AnalyticsTracker,
    val settings: Settings,
) {

    suspend fun shouldShow(downloadedFiles: Long): Boolean = isDeviceRunningOnLowStorage() &&
        downloadedFiles != 0L &&
        settings.shouldShowLowStorageModalAfterSnooze() &&
        FeatureFlag.isEnabled(Feature.MANAGE_DOWNLOADED_EPISODES)

    fun getDialog(
        totalDownloadSize: Long,
        sourceView: SourceView,
        onManageDownloadsClick: () -> Unit,
    ): ConfirmationDialog {
        val formattedTotalDownloadSize = Util.formattedBytes(bytes = totalDownloadSize, context = context)

        analyticsTracker.track(AnalyticsEvent.FREE_UP_SPACE_MODAL_SHOWN, mapOf("source" to sourceView.analyticsValue))

        return ConfirmationDialog()
            .setTitle(context.getString(R.string.need_to_free_up_space))
            .setSummary(context.getString(R.string.save_space_by_managing_downloaded_episodes, formattedTotalDownloadSize))
            .setSummaryTextColor(R.attr.primary_text_01)
            .setSummaryTextSize(14f)
            .setButtonType(ConfirmationDialog.ButtonType.Normal(context.getString(R.string.manage_downloads)))
            .setSecondaryButtonType(ConfirmationDialog.ButtonType.Normal(context.getString(R.string.maybe_later)))
            .setSecondaryTextColor(R.attr.primary_text_01)
            .setIconId(R.drawable.pencil_cleanup)
            .setIconTint(R.attr.primary_interactive_01)
            .setDisplayConfirmButtonFirst(true)
            .setRemoveSecondaryButtonBorder(true)
            .setOnConfirm {
                analyticsTracker.track(AnalyticsEvent.FREE_UP_SPACE_MANAGE_DOWNLOADS_TAPPED, mapOf("source" to sourceView.analyticsValue))
                onManageDownloadsClick.invoke()
            }
            .setOnSecondary {
                analyticsTracker.track(AnalyticsEvent.FREE_UP_SPACE_MAYBE_LATER_TAPPED, mapOf("source" to sourceView.analyticsValue))
                settings.setDismissLowStorageModalTime(System.currentTimeMillis())
            }
    }
}
