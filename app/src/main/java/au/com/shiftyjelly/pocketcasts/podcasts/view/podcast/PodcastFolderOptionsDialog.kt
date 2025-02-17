package au.com.shiftyjelly.pocketcasts.podcasts.view.podcast

import androidx.fragment.app.FragmentActivity
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.models.entity.Folder
import au.com.shiftyjelly.pocketcasts.views.dialog.OptionsDialog

class PodcastFolderOptionsDialog(
    val folder: Folder,
    val onRemoveFolder: () -> Unit,
    val onChangeFolder: () -> Unit,
    val onOpenFolder: () -> Unit,
    val activity: FragmentActivity?,
) {

    private var showDialog: OptionsDialog? = null

    fun show() {
        val dialog = OptionsDialog()
            .setTitle(folder.name)
            .addTextOption(
                titleId = R.string.remove_from_folder,
                imageId = R.drawable.folder_remove,
                click = {
                    onRemoveFolder()
                    dismiss()
                },
            )
            .addTextOption(
                titleId = R.string.change_folder,
                imageId = R.drawable.folder_change,
                click = {
                    onChangeFolder()
                    dismiss()
                },
            )
            .addTextOption(
                titleId = R.string.go_to_folder,
                imageId = R.drawable.go_to,
                click = {
                    onOpenFolder()
                    dismiss()
                },
            )
        activity?.supportFragmentManager?.let {
            dialog.show(it, "podcast_folder_options_dialog")
            showDialog = dialog
        }
    }

    fun dismiss() {
        showDialog?.dismiss()
    }
}
