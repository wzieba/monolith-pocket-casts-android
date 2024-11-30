package au.com.shiftyjelly.pocketcasts.endofyear.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.compose.components.DialogButtonState
import au.com.shiftyjelly.pocketcasts.compose.components.DialogFrame
import au.com.shiftyjelly.pocketcasts.compose.components.DialogText

@Composable
internal fun ScreenshotDetectedDialog(
    onNotNow: () -> Unit,
    onShare: () -> Unit,
) {
    DialogFrame(
        onDismissRequest = onNotNow,
        title = stringResource(R.string.end_of_year_share_dialog_title),
        content = {
            DialogText(stringResource(R.string.end_of_year_share_dialog_message))
        },
        buttons = listOf(
            DialogButtonState(
                text = stringResource(R.string.not_now),
                onClick = onNotNow,
            ),
            DialogButtonState(
                text = stringResource(R.string.share),
                onClick = onShare,
            ),
        ),
    )
}

@Preview
@Composable
private fun CrashMessageDialogPreview() {
    ScreenshotDetectedDialog(
        onNotNow = {},
        onShare = {},
    )
}
