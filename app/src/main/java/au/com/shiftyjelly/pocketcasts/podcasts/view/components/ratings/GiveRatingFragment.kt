package au.com.shiftyjelly.pocketcasts.podcasts.view.components.ratings

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.fragment.compose.content
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsEvent
import au.com.shiftyjelly.pocketcasts.compose.AppThemeWithBackground
import au.com.shiftyjelly.pocketcasts.podcasts.viewmodel.GiveRatingViewModel
import au.com.shiftyjelly.pocketcasts.settings.onboarding.OnboardingFlow
import au.com.shiftyjelly.pocketcasts.settings.onboarding.OnboardingLauncher
import au.com.shiftyjelly.pocketcasts.ui.helper.FragmentHostListener
import au.com.shiftyjelly.pocketcasts.utils.log.LogBuffer
import au.com.shiftyjelly.pocketcasts.views.fragments.BaseDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ARG_PODCAST_UUID = "podcastUuid"

class GiveRatingFragment : BaseDialogFragment() {

    private val viewModel: GiveRatingViewModel by viewModels()

    companion object {
        fun newInstance(podcastUuid: String) = GiveRatingFragment().apply {
            arguments = bundleOf(
                ARG_PODCAST_UUID to podcastUuid,
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        val podcastUuid = remember { arguments?.getString(ARG_PODCAST_UUID) }
        if (podcastUuid == null) {
            exitWithError("${this@GiveRatingFragment::class.simpleName} is missing podcastUuid argument")
            return@content
        }

        AppThemeWithBackground(theme.activeTheme) {
            val coroutineScope = rememberCoroutineScope()
            val context = requireContext()

            GiveRatingPage(
                podcastUuid = podcastUuid,
                viewModel = viewModel,
                submitRating = {
                    coroutineScope.launch {
                        viewModel.submitRating(
                            podcastUuid = podcastUuid,
                            context = context,
                            onSuccess = {
                                displayMessage(getString(R.string.thank_you_for_rating))
                                dismiss()
                            },
                            onError = {
                                displayMessage(getString(R.string.something_went_wrong_to_rate_this_podcast))
                                dismiss()
                            },
                        )
                    }
                },
                onDismiss = ::dismiss,
                onUserSignedOut = {
                    OnboardingLauncher.openOnboardingFlow(requireActivity(), OnboardingFlow.LoggedOut)
                    Toast.makeText(context, context.getString(R.string.podcast_log_in_to_rate), Toast.LENGTH_LONG)
                        .show()
                    coroutineScope.launch {
                        // a short delay prevents the screen from flashing before the onboarding flow is shown
                        delay(1.seconds)
                        dismiss()
                    }
                },
            )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val state = viewModel.state.value

        if (state is GiveRatingViewModel.State.Loaded) {
            viewModel.trackOnDismissed(AnalyticsEvent.RATING_SCREEN_DISMISSED)
        } else if (state is GiveRatingViewModel.State.NotAllowedToRate) {
            viewModel.trackOnDismissed(AnalyticsEvent.NOT_ALLOWED_TO_RATE_SCREEN_DISMISSED)
        }
    }

    private fun displayMessage(message: String) {
        (activity as? FragmentHostListener)?.snackBarView()?.let { snackBarView ->
            Snackbar.make(snackBarView, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun exitWithError(message: String) {
        LogBuffer.e(LogBuffer.TAG_INVALID_STATE, message)
        dismiss()
    }
}
