package au.com.shiftyjelly.pocketcasts.sharing

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.utils.getPackageInfo

enum class SocialPlatform(
    @DrawableRes val logoId: Int,
    @StringRes val nameId: Int,
    val packageId: String?,
) {
    Instagram(
        logoId = R.drawable.ic_share_logo_instagram,
        nameId = R.string.share_label_instagram_stories,
        packageId = "com.instagram.android",
    ),
    WhatsApp(
        logoId = R.drawable.ic_share_logo_whats_app,
        nameId = R.string.share_label_whats_app,
        packageId = "com.whatsapp",
    ),
    Telegram(
        logoId = R.drawable.ic_share_logo_telegram,
        nameId = R.string.share_label_telegram,
        packageId = "org.telegram.messenger",
    ),
    X(
        logoId = R.drawable.ic_share_logo_x,
        nameId = R.string.share_label_x,
        packageId = "com.twitter.android",
    ),
    Tumblr(
        logoId = R.drawable.ic_share_logo_tumblr,
        nameId = R.string.share_label_tumblr,
        packageId = "com.tumblr",
    ),
    PocketCasts(
        logoId = R.drawable.ic_share_logo_pocket_casts,
        nameId = R.string.share_label_copy_link,
        packageId = null,
    ),
    More(
        logoId = R.drawable.ic_share_logo_more,
        nameId = R.string.share_label_more,
        packageId = null,
    ),
    ;

    companion object {
        fun getAvailablePlatforms(context: Context): Set<SocialPlatform> = buildSet {
            SocialPlatform.entries.forEach { platform ->
                if (platform.packageId?.let(context::getPackageInfo) != null) {
                    add(platform)
                }
            }
        }
    }
}
