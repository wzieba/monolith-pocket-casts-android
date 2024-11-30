package au.com.shiftyjelly.pocketcasts.search.searchhistory

import android.content.Context
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.views.dialog.ConfirmationDialog

class SearchHistoryClearAllConfirmationDialog(
    context: Context,
    onConfirm: () -> Unit,
) : ConfirmationDialog() {
    init {
        setTitle(context.getString(R.string.clear_all))
        setSummary(context.getString(R.string.search_history_clear_all_confirmation_message))
        setIconId(R.drawable.ic_delete)
        setButtonType(ButtonType.Danger(context.getString(R.string.search_history_clear_all_confirm_button_title)))
        setOnConfirm { onConfirm() }
    }
}
