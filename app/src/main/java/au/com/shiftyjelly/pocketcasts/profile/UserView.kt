package au.com.shiftyjelly.pocketcasts.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.account.ProfileCircleView
import au.com.shiftyjelly.pocketcasts.compose.AppTheme
import au.com.shiftyjelly.pocketcasts.compose.extensions.setContentWithViewCompositionStrategy
import au.com.shiftyjelly.pocketcasts.compose.images.SubscriptionBadge
import au.com.shiftyjelly.pocketcasts.localization.extensions.getStringPluralDaysMonthsOrYears
import au.com.shiftyjelly.pocketcasts.localization.extensions.getStringPluralSecondsMinutesHoursDaysOrYears
import au.com.shiftyjelly.pocketcasts.models.to.SignInState
import au.com.shiftyjelly.pocketcasts.models.to.SubscriptionStatus
import au.com.shiftyjelly.pocketcasts.models.type.SubscriptionFrequency
import au.com.shiftyjelly.pocketcasts.models.type.SubscriptionPlatform
import au.com.shiftyjelly.pocketcasts.models.type.SubscriptionTier
import au.com.shiftyjelly.pocketcasts.ui.extensions.getThemeColor
import au.com.shiftyjelly.pocketcasts.ui.theme.Theme
import au.com.shiftyjelly.pocketcasts.utils.Gravatar
import au.com.shiftyjelly.pocketcasts.utils.TimeConstants
import au.com.shiftyjelly.pocketcasts.utils.Util
import au.com.shiftyjelly.pocketcasts.utils.days
import au.com.shiftyjelly.pocketcasts.utils.extensions.toLocalizedFormatLongStyle
import java.util.Date

open class UserView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    open val layoutResource = R.layout.view_user

    var signedInState: SignInState? = null
        set(value) {
            field = value
            update(value)
        }

    var accountStartDate: Date = Date()
    val maxSubscriptionExpiryMs = 30L * 24L * 60L * 60L * 1000L
    val lblUserEmail: TextView
    val lblSignInStatus: TextView?
    val imgProfilePicture: ProfileCircleView
    val btnAccount: Button?
    private val subscriptionBadge: ComposeView?
    private val isDarkTheme: Boolean
        get() = Theme.isDark(context)

    init {
        LayoutInflater.from(context).inflate(layoutResource, this, true)
        lblUserEmail = findViewById(R.id.lblUserEmail)
        lblSignInStatus = findViewById(R.id.lblSignInStatus)
        imgProfilePicture = findViewById(R.id.imgProfilePicture)
        btnAccount = findViewById(R.id.btnAccount)
        subscriptionBadge = findViewById(R.id.subscriptionBadge)
    }

    open fun update(signInState: SignInState?) {
        updateProfileImageAndDaysRemaining(signInState)
        updateEmail(signInState)
        updateSubscriptionBadge(signInState)
        updateAccountButton(signInState)
    }

    private fun updateProfileImageAndDaysRemaining(
        signInState: SignInState?,
    ) {
        when (signInState) {
            is SignInState.SignedIn -> {
                val gravatarUrl = Gravatar.getUrl(signInState.email)
                var percent = 1.0f
                val daysLeft = daysLeft(signInState, 30)
                if (daysLeft != null && daysLeft > 0 && daysLeft <= 30) {
                    percent = daysLeft / 30f
                }
                imgProfilePicture.setup(
                    percent = percent,
                    plusOnly = signInState.isSignedInAsPlus,
                    isPatron = signInState.isSignedInAsPatron,
                    gravatarUrl = gravatarUrl,
                )
            }
            is SignInState.SignedOut -> imgProfilePicture.setup(
                percent = 0.0f,
                plusOnly = false,
                isPatron = false,
            )
            else -> imgProfilePicture.setup(
                percent = 0.0f,
                plusOnly = false,
                isPatron = false,
            )
        }
    }

    private fun daysLeft(signInState: SignInState.SignedIn, maxDays: Int): Int? {
        val timeInXDays = Date(Date().time + maxDays.days())
        val paidStatus = signInState.subscriptionStatus as? SubscriptionStatus.Paid
        if (paidStatus != null && paidStatus.expiryDate.before(timeInXDays)) {
            // probably shouldn't be do straight millisecond maths because of day light savings
            return ((paidStatus.expiryDate.time - Date().time) / TimeConstants.MILLISECONDS_IN_ONE_DAY).toInt()
        }
        return null
    }

    private fun setDaysRemainingTextIfNeeded(signInState: SignInState.SignedIn) {
        val status = ((signInState as? SignInState.SignedIn)?.subscriptionStatus as? SubscriptionStatus.Paid) ?: return
        if (status.autoRenew) {
            return
        }

        val timeLeftMs = status.expiryDate.time - Date().time
        if (timeLeftMs <= 0) {
            return
        }

        if (timeLeftMs <= maxSubscriptionExpiryMs) {
            val expiresIn = resources.getStringPluralSecondsMinutesHoursDaysOrYears(timeLeftMs)
            val messagesRes = if (signInState.isSignedInAsPatron) R.string.profile_patron_expires_in else R.string.profile_plus_expires_in
            lblUserEmail.text = context.getString(messagesRes, expiresIn).uppercase()
            lblUserEmail.setTextColor(lblUserEmail.context.getThemeColor(R.attr.support_05))
        }
    }

    private fun updateEmail(signInState: SignInState?) {
        when (signInState) {
            is SignInState.SignedIn -> {
                lblUserEmail.text = signInState.email
                lblUserEmail.visibility = View.VISIBLE
                lblUserEmail.setTextColor(context.getThemeColor(R.attr.primary_text_01))

                if (this !is ExpandedUserView) setDaysRemainingTextIfNeeded(signInState)
            }
            is SignInState.SignedOut -> {
                lblUserEmail.text = context.getString(R.string.profile_set_up_account)
                lblUserEmail.visibility = View.GONE
            }
            null -> lblUserEmail.text = null
        }
    }

    private fun updateSubscriptionBadge(signInState: SignInState?) {
        val fontSize = if (Util.isAutomotive(context)) 20.sp else 14.sp
        val iconSize = if (Util.isAutomotive(context)) 20.dp else 14.dp
        val padding = if (Util.isAutomotive(context)) 6.dp else 4.dp
        subscriptionBadge?.setContentWithViewCompositionStrategy {
            AppTheme(if (isDarkTheme) Theme.ThemeType.DARK else Theme.ThemeType.LIGHT) {
                if (signInState is SignInState.SignedIn) {
                    val isExpandedUserView = this@UserView is ExpandedUserView
                    val modifier = Modifier.padding(top = 16.dp)
                    if (signInState.isSignedInAsPatron) {
                        SubscriptionBadge(
                            iconRes = R.drawable.ic_patron,
                            shortNameRes = R.string.pocket_casts_patron_short,
                            iconColor = if (!isExpandedUserView) Color.White else Color.Unspecified,
                            backgroundColor = if (!isExpandedUserView) colorResource(R.color.patron_purple) else null,
                            textColor = if (!isExpandedUserView) colorResource(R.color.patron_purple_light) else null,
                            modifier = if (isExpandedUserView) modifier else Modifier,
                            iconSize = iconSize,
                            fontSize = fontSize,
                            padding = padding,
                        )
                    } else if (signInState.isSignedInAsPlus && isExpandedUserView) {
                        SubscriptionBadge(
                            iconRes = R.drawable.ic_plus,
                            shortNameRes = R.string.pocket_casts_plus_short,
                            iconColor = colorResource(R.color.plus_gold),
                            iconSize = iconSize,
                            fontSize = fontSize,
                            padding = padding,
                            modifier = modifier,
                        )
                    }
                }
            }
        }
    }

    private fun updateAccountButton(signInState: SignInState?) {
        btnAccount?.text = when (signInState) {
            is SignInState.SignedIn -> context.getString(R.string.profile_account)
            is SignInState.SignedOut -> context.getString(R.string.profile_set_up_account)
            else -> null
        }
    }
}

class ExpandedUserView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : UserView(context, attrs, defStyleAttr) {
    override val layoutResource: Int
        get() = R.layout.view_expanded_user

    val lblPaymentStatus: TextView
        get() = findViewById(R.id.lblPaymentStatus)

    private var onUserViewClickListener: OnUserViewClickListener? = null

    override fun update(signInState: SignInState?) {
        super.update(signInState)

        val status = (signInState as? SignInState.SignedIn)?.subscriptionStatus ?: return
        when (status) {
            is SubscriptionStatus.Free -> {
                lblPaymentStatus.text = context.getString(R.string.profile_free_account)
                lblSignInStatus?.text = ""
            }
            is SubscriptionStatus.Paid -> {
                val activeSubscription = status.subscriptions.getOrNull(status.index)
                if (activeSubscription == null ||
                    activeSubscription.tier in listOf(
                        SubscriptionTier.PATRON,
                        SubscriptionTier.PLUS,
                    )
                ) {
                    setupLabelsForPaidUser(status, signInState)
                } else {
                    setupLabelsForSupporter(activeSubscription)
                }
            }
        }
    }

    fun setOnUserViewClick(onUserViewClickListener: OnUserViewClickListener?) {
        this.onUserViewClickListener = onUserViewClickListener
    }

    private fun setupLabelsForPaidUser(status: SubscriptionStatus.Paid, signInState: SignInState) {
        if (status.autoRenew) {
            val strMonthly = context.getString(R.string.profile_monthly)
            val strYearly = context.getString(R.string.profile_yearly)
            lblPaymentStatus.text = context.getString(R.string.profile_next_payment, status.expiryDate.toLocalizedFormatLongStyle())
            lblSignInStatus?.text = when (status.frequency) {
                SubscriptionFrequency.MONTHLY -> strMonthly
                SubscriptionFrequency.YEARLY -> strYearly
                else -> null
            }
            lblSignInStatus?.setTextColor(context.getThemeColor(R.attr.primary_text_02))
        } else {
            if (status.platform == SubscriptionPlatform.GIFT) {
                if (signInState.isPocketCastsChampion) {
                    lblPaymentStatus.text = context.resources.getString(R.string.plus_thanks_for_your_support_bang)
                } else {
                    val giftDaysString = context.resources.getStringPluralDaysMonthsOrYears(status.giftDays)
                    lblPaymentStatus.text = context.resources.getString(R.string.profile_time_free, giftDaysString)
                }
            } else {
                lblPaymentStatus.text = context.getString(R.string.profile_payment_cancelled)
            }

            if (signInState.isPocketCastsChampion) {
                lblSignInStatus?.text = context.resources.getString(R.string.pocket_casts_champion)
                lblSignInStatus?.setTextColor(lblSignInStatus.context.getThemeColor(R.attr.support_02))
                lblSignInStatus?.setOnClickListener {
                    onUserViewClickListener?.onPocketCastsChampionClick()
                }
            } else {
                lblSignInStatus?.text = context.getString(R.string.profile_plus_expires, status.expiryDate.toLocalizedFormatLongStyle())
                lblSignInStatus?.setTextColor(lblSignInStatus.context.getThemeColor(R.attr.primary_text_02))
            }
        }
    }

    private fun setupLabelsForSupporter(subscription: SubscriptionStatus.Subscription) {
        if (subscription.autoRenewing) {
            lblPaymentStatus.text = context.getString(R.string.supporter)
            lblPaymentStatus.setTextColor(lblPaymentStatus.context.getThemeColor(R.attr.support_02))

            lblSignInStatus?.text = context.getString(R.string.supporter_check_contributions)
            lblSignInStatus?.setTextColor(context.getThemeColor(R.attr.primary_text_02))
        } else {
            lblPaymentStatus.text = context.getString(R.string.supporter_payment_cancelled)
            lblPaymentStatus.setTextColor(lblPaymentStatus.context.getThemeColor(R.attr.support_05))

            val expiryDate = subscription.expiryDate?.let { it.toLocalizedFormatLongStyle() } ?: context.getString(R.string.profile_expiry_date_unknown)
            lblSignInStatus?.text = context.getString(R.string.supporter_subscription_ends, expiryDate)
            lblSignInStatus?.setTextColor(context.getThemeColor(R.attr.primary_text_02))
        }
    }
}

interface OnUserViewClickListener {
    fun onPocketCastsChampionClick()
}
