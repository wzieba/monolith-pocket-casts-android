package au.com.shiftyjelly.pocketcasts.podcasts.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.compose.AppThemeWithBackground
import au.com.shiftyjelly.pocketcasts.compose.components.TextH40
import au.com.shiftyjelly.pocketcasts.compose.components.TextH50
import au.com.shiftyjelly.pocketcasts.compose.components.TextP50
import au.com.shiftyjelly.pocketcasts.compose.preview.ThemePreviewParameterProvider
import au.com.shiftyjelly.pocketcasts.compose.theme
import au.com.shiftyjelly.pocketcasts.ui.theme.Theme
import au.com.shiftyjelly.pocketcasts.utils.Util

@Composable
fun ManageDownloadsCard(
    totalDownloadSize: Long,
    onManageDownloadsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formattedTotalDownloadSize = Util.formattedBytes(bytes = totalDownloadSize, context = LocalContext.current)

    Row(
        modifier = modifier
            .background(color = MaterialTheme.theme.colors.primaryUi06, RoundedCornerShape(8.dp))
            .border(
                width = 0.5.dp,
                color = MaterialTheme.theme.colors.primaryField03,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 16.dp).padding(top = 16.dp),

    ) {
        Icon(
            painter = painterResource(id = R.drawable.pencil_cleanup),
            contentDescription = stringResource(R.string.pencil_clean_up_icon_content_description),
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp),
            // The icon isn't clickable so the design matches the title color
            tint = MaterialTheme.theme.colors.primaryText01,
        )

        Column(
            modifier = Modifier.padding(end = 16.dp),
        ) {
            TextH40(
                text = stringResource(R.string.need_to_free_up_space),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.theme.colors.primaryText01,
            )

            Spacer(modifier = Modifier.height(2.dp))

            TextP50(
                text = stringResource(R.string.save_space_by_managing_downloaded_episodes, formattedTotalDownloadSize),
                color = MaterialTheme.theme.colors.primaryText02,
            )

            TextButton(onClick = { onManageDownloadsClick.invoke() }, contentPadding = PaddingValues()) {
                TextH50(text = stringResource(R.string.manage_downloads), color = MaterialTheme.theme.colors.primaryIcon01)
            }
        }
    }
}

@Preview
@Composable
fun ManageDownloadsCardPreview(
    @PreviewParameter(ThemePreviewParameterProvider::class) themeType: Theme.ThemeType,
) {
    AppThemeWithBackground(themeType) {
        ManageDownloadsCard(totalDownloadSize = 15023232, onManageDownloadsClick = {})
    }
}
