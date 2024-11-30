package au.com.shiftyjelly.pocketcasts.account.viewmodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.account.AccountActivity.AccountUpdatedSource
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsEvent
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class DoneViewModel @Inject constructor(
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val mutableState = MutableStateFlow<State>(State.Empty)
    val state: StateFlow<State> = mutableState

    fun setChangedEmailState(detail: String) {
        mutableState.value = State.SuccessFullChangedEmail(detail = detail)
    }

    fun setChangedPasswordState(detail: String) {
        mutableState.value = State.SuccessFullChangedPassword(detail = detail)
    }

    fun trackShown(source: AccountUpdatedSource) {
        analyticsTracker.track(AnalyticsEvent.ACCOUNT_UPDATED_SHOWN, mapOf(SOURCE_KEY to source.analyticsValue))
    }

    fun trackDismissed() {
        analyticsTracker.track(AnalyticsEvent.ACCOUNT_UPDATED_DISMISSED)
    }

    sealed class State {
        data object Empty : State()
        data class SuccessFullChangedEmail(
            val detail: String = "",
            @StringRes val titleResourceId: Int = R.string.profile_email_address_changed,
            @DrawableRes val imageResourceId: Int = R.drawable.ic_email_address_changed,
        ) : State()
        data class SuccessFullChangedPassword(
            val detail: String = "",
            @StringRes val titleResourceId: Int = R.string.profile_password_changed,
            @DrawableRes val imageResourceId: Int = R.drawable.ic_password_changed,
        ) : State()
    }

    companion object {
        private const val SOURCE_KEY = "source"
    }
}
