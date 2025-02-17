package au.com.shiftyjelly.pocketcasts.views.multiselect

import android.content.res.Resources
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsEvent
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsTracker
import au.com.shiftyjelly.pocketcasts.analytics.SourceView
import au.com.shiftyjelly.pocketcasts.localization.extensions.getStringPlural
import au.com.shiftyjelly.pocketcasts.models.entity.Bookmark
import au.com.shiftyjelly.pocketcasts.repositories.bookmark.BookmarkManager
import au.com.shiftyjelly.pocketcasts.views.dialog.ConfirmationDialog
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MultiSelectBookmarksHelper @Inject constructor(
    private val bookmarkManager: BookmarkManager,
    private val analyticsTracker: AnalyticsTracker,
) : MultiSelectHelper<Bookmark>() {
    override val maxToolbarIcons = 3

    private val _navigationState = MutableSharedFlow<NavigationState>()
    val navigationState = _navigationState.asSharedFlow()

    override var source by bookmarkManager::sourceView

    override val toolbarActions: LiveData<List<MultiSelectAction>> = _selectedListLive
        .map {
            Timber.d("MultiSelectBookmarksHelper toolbarActions updated, ${it.size} bookmarks from $source")
            listOf(
                MultiSelectBookmarkAction.ShareBookmark(
                    isVisible = source != SourceView.FILES &&
                        it.count() == 1,
                ),
                MultiSelectBookmarkAction.EditBookmark(isVisible = it.count() == 1),
                MultiSelectBookmarkAction.DeleteBookmark,
                MultiSelectAction.SelectAll,
            )
        }

    override fun isSelected(multiSelectable: Bookmark) =
        selectedList.count { it.uuid == multiSelectable.uuid } > 0

    override fun onMenuItemSelected(
        itemId: Int,
        resources: Resources,
        activity: FragmentActivity,
    ): Boolean {
        return when (itemId) {
            R.id.menu_share -> {
                share()
                true
            }

            R.id.menu_edit -> {
                edit()
                true
            }

            R.id.menu_delete -> {
                delete(resources, activity.supportFragmentManager)
                true
            }

            R.id.menu_select_all -> {
                selectAll()
                true
            }

            else -> false
        }
    }

    override fun deselect(multiSelectable: Bookmark) {
        if (isSelected(multiSelectable)) {
            val selectedItem = selectedList.firstOrNull { it.uuid == multiSelectable.uuid }
            selectedItem?.let { selectedList.remove(it) }
        }

        _selectedListLive.value = selectedList

        if (selectedList.isEmpty()) {
            closeMultiSelect()
        }
    }

    private fun share() {
        launch { _navigationState.emit(NavigationState.ShareBookmark) }
    }

    private fun edit() {
        launch { _navigationState.emit(NavigationState.EditBookmark) }
    }

    fun delete(resources: Resources, fragmentManager: FragmentManager) {
        val bookmarks = selectedList.toList()
        if (bookmarks.isEmpty()) {
            closeMultiSelect()
            return
        }

        val count = bookmarks.size
        ConfirmationDialog()
            .setForceDarkTheme(source == SourceView.PLAYER)
            .setButtonType(
                ConfirmationDialog.ButtonType.Danger(
                    resources.getStringPlural(
                        count = count,
                        singular = R.string.bookmarks_delete_singular,
                        plural = R.string.bookmarks_delete_plural,
                    ),
                ),
            )
            .setTitle(resources.getString(R.string.are_you_sure))
            .setSummary(
                resources.getStringPlural(
                    count = count,
                    singular = R.string.bookmarks_delete_summary_singular,
                    plural = R.string.bookmarks_delete_summary_plural,
                ),
            )
            .setIconId(R.drawable.ic_delete)
            .setIconTint(R.attr.support_05)
            .setOnConfirm {
                launch {
                    bookmarks.forEach {
                        bookmarkManager.deleteToSync(it.uuid)
                        analyticsTracker.track(
                            AnalyticsEvent.BOOKMARK_DELETED,
                            mapOf("source" to source.analyticsValue),
                        )
                    }

                    withContext(Dispatchers.Main) {
                        val snackText = resources.getStringPlural(
                            count,
                            R.string.bookmarks_deleted_singular,
                            R.string.bookmarks_deleted_plural,
                        )
                        showSnackBar(snackText)
                    }
                }
                closeMultiSelect()
            }
            .show(fragmentManager, "delete_bookmarks_warning")
    }

    sealed class NavigationState {
        data object EditBookmark : NavigationState()
        data object ShareBookmark : NavigationState()
    }
}
