package au.com.shiftyjelly.pocketcasts.compose.images

import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.compose.AppTheme
import au.com.shiftyjelly.pocketcasts.compose.theme
import au.com.shiftyjelly.pocketcasts.ui.theme.Theme

@Composable
fun VerticalLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(if (MaterialTheme.theme.isLight) R.drawable.ic_logo_title_ver_light else R.drawable.ic_logo_title_ver_dark),
        contentDescription = stringResource(R.string.pocket_casts),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun VerticalLogoLightPreview() {
    AppTheme(Theme.ThemeType.LIGHT) {
        VerticalLogo()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun VerticalLogoDarkPreview() {
    AppTheme(Theme.ThemeType.DARK) {
        VerticalLogo()
    }
}
