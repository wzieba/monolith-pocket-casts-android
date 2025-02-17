package au.com.shiftyjelly.pocketcasts.podcasts.view.podcasts

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.models.entity.Folder
import au.com.shiftyjelly.pocketcasts.models.type.PodcastsSortType
import au.com.shiftyjelly.pocketcasts.preferences.Settings
import au.com.shiftyjelly.pocketcasts.views.dialog.OptionsDialog

class FolderOptionsDialog(
    val folder: Folder,
    val onOpenSortOptions: () -> Unit,
    val onSortTypeChanged: (PodcastsSortType) -> Unit,
    val onEditFolder: () -> Unit,
    val onAddOrRemovePodcast: () -> Unit,
    val fragment: Fragment,
    val settings: Settings,
) {

    private var showDialog: OptionsDialog? = null
    private var sortDialog: OptionsDialog? = null
    private val fragmentManager: FragmentManager?
        get() = fragment.activity?.supportFragmentManager

    fun show() {
        val fragmentManager = fragmentManager ?: return
        val dialog = OptionsDialog()
            .addTextOption(
                titleId = R.string.podcasts_menu_sort_by,
                imageId = R.drawable.ic_sort,
                valueId = folder.podcastsSortType.labelId,
                click = {
                    onOpenSortOptions()
                    openSortOptions()
                },
            )
            .addTextOption(
                titleId = R.string.edit_folder,
                imageId = R.drawable.ic_pencil_edit,
                click = {
                    onEditFolder()
                    dismiss()
                },
            )
            .addTextOption(
                titleId = R.string.add_or_remove_podcasts,
                imageId = R.drawable.ic_podcasts,
                click = {
                    onAddOrRemovePodcast()
                },
            )
        dialog.show(fragmentManager, "podcasts_options_dialog")
        showDialog = dialog
    }

    private fun openSortOptions() {
        val fragmentManager = fragmentManager ?: return
        val podcastsSortType = folder.podcastsSortType
        val title = fragment.getString(R.string.sort_by)
        val dialog = OptionsDialog().setTitle(title)
        for (sortType in PodcastsSortType.values()) {
            dialog.addCheckedOption(
                titleId = sortType.labelId,
                checked = sortType == podcastsSortType,
                click = {
                    onSortTypeChanged(sortType)
                },
            )
        }
        dialog.show(fragmentManager, "podcasts_sort_dialog")
        sortDialog = dialog
    }

    fun dismiss() {
        showDialog?.dismiss()
        sortDialog?.dismiss()
    }
}
