package au.com.shiftyjelly.pocketcasts.podcasts.view.folders

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.fragment.compose.content
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsEvent
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsTracker
import au.com.shiftyjelly.pocketcasts.compose.AppThemeWithBackground
import au.com.shiftyjelly.pocketcasts.ui.helper.FragmentHostListener
import au.com.shiftyjelly.pocketcasts.views.dialog.ConfirmationDialog
import au.com.shiftyjelly.pocketcasts.views.fragments.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FolderEditFragment : BaseDialogFragment() {

    @Inject lateinit var analyticsTracker: AnalyticsTracker
    private val viewModel: FolderEditViewModel by viewModels()

    companion object {
        private const val ARG_FOLDER_UUID = "ARG_FOLDER_UUID"
        private const val DID_CHANGE_COLOR_KEY = "did_change_color"
        private const val DID_CHANGE_NAME_KEY = "did_change_name"

        fun newInstance(folderUuid: String): FolderEditFragment {
            return FolderEditFragment().apply {
                arguments = bundleOf(
                    ARG_FOLDER_UUID to folderUuid,
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString(FolderEditPodcastsFragment.ARG_FOLDER_UUID)?.let { folderUuid ->
            viewModel.setFolderUuid(folderUuid)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        AppThemeWithBackground(theme.activeTheme) {
            FolderEditPage(
                viewModel = viewModel,
                onDeleteClick = { confirmFolderDelete() },
                onBackClick = { dismiss() },
            )
        }
    }

    private fun confirmFolderDelete() {
        analyticsTracker.track(AnalyticsEvent.FOLDER_EDIT_DELETE_BUTTON_TAPPED)
        ConfirmationDialog()
            .setButtonType(ConfirmationDialog.ButtonType.Danger(getString(R.string.delete_folder)))
            .setTitle(getString(R.string.are_you_sure))
            .setSummary(getString(R.string.delete_folder_question))
            .setOnConfirm {
                analyticsTracker.track(AnalyticsEvent.FOLDER_DELETED)
                viewModel.deleteFolder {
                    (activity as FragmentHostListener).closePodcastsToRoot()
                    dismiss()
                }
            }
            .setIconId(R.drawable.ic_delete)
            .setIconTint(R.attr.support_05)
            .show(childFragmentManager, "delete_folder_warning")
    }

    override fun onDismiss(dialog: DialogInterface) {
        analyticsTracker.track(
            AnalyticsEvent.FOLDER_EDIT_DISMISSED,
            mapOf(
                DID_CHANGE_NAME_KEY to viewModel.isNameChanged,
                DID_CHANGE_COLOR_KEY to viewModel.isColorIdChanged,
            ),
        )
        super.onDismiss(dialog)
    }
}
