package au.com.shiftyjelly.pocketcasts.settings

import android.content.Context
import au.com.shiftyjelly.pocketcasts.views.dialog.ConfirmationDialog
import au.com.shiftyjelly.pocketcasts.localization.R as LR

/**
 * A dialog that shows a Downloads - Cleanup confirmation dialog.
 */
class ManualCleanupConfirmationDialog(context: Context, onConfirm: () -> Unit) : ConfirmationDialog() {
    init {
        setTitle(context.getString(R.string.settings_downloads_clean_up))
        setSummary(context.getString(R.string.settings_downloads_clean_up_summary))
        setIconId(au.com.shiftyjelly.pocketcasts.views.R.drawable.ic_delete)
        setButtonType(ButtonType.Danger(context.getString(R.string.delete)))
        setOnConfirm { onConfirm() }
    }
}
