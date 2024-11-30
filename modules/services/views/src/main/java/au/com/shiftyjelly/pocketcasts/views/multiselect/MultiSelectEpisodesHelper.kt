package au.com.shiftyjelly.pocketcasts.views.multiselect

import android.content.res.Resources
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.toLiveData
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsEvent
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsTracker
import au.com.shiftyjelly.pocketcasts.analytics.EpisodeAnalytics
import au.com.shiftyjelly.pocketcasts.analytics.SourceView
import au.com.shiftyjelly.pocketcasts.localization.extensions.getStringPlural
import au.com.shiftyjelly.pocketcasts.models.entity.BaseEpisode
import au.com.shiftyjelly.pocketcasts.models.entity.PodcastEpisode
import au.com.shiftyjelly.pocketcasts.models.entity.UserEpisode
import au.com.shiftyjelly.pocketcasts.preferences.Settings
import au.com.shiftyjelly.pocketcasts.repositories.di.ApplicationScope
import au.com.shiftyjelly.pocketcasts.repositories.download.DownloadManager
import au.com.shiftyjelly.pocketcasts.repositories.playback.PlaybackManager
import au.com.shiftyjelly.pocketcasts.repositories.podcast.EpisodeManager
import au.com.shiftyjelly.pocketcasts.repositories.podcast.PodcastManager
import au.com.shiftyjelly.pocketcasts.repositories.podcast.UserEpisodeManager
import au.com.shiftyjelly.pocketcasts.utils.combineLatest
import au.com.shiftyjelly.pocketcasts.utils.log.LogBuffer
import au.com.shiftyjelly.pocketcasts.views.dialog.ConfirmationDialog
import au.com.shiftyjelly.pocketcasts.views.dialog.ShareDialogFactory
import au.com.shiftyjelly.pocketcasts.views.helper.CloudDeleteHelper
import au.com.shiftyjelly.pocketcasts.views.helper.DeleteState
import com.automattic.android.tracks.crashlogging.CrashLogging
import io.reactivex.BackpressureStrategy
import javax.inject.Inject
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

private const val WARNING_LIMIT = 3
class MultiSelectEpisodesHelper @Inject constructor(
    val episodeManager: EpisodeManager,
    val userEpisodeManager: UserEpisodeManager,
    val podcastManager: PodcastManager,
    val playbackManager: PlaybackManager,
    val downloadManager: DownloadManager,
    val analyticsTracker: AnalyticsTracker,
    val settings: Settings,
    private val episodeAnalytics: EpisodeAnalytics,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val crashLogging: CrashLogging,
    private val shareDialogFactory: ShareDialogFactory,
) : MultiSelectHelper<BaseEpisode>() {
    override val maxToolbarIcons = 4

    override val toolbarActions: LiveData<List<MultiSelectAction>> = settings.multiSelectItemsObservable
        .toFlowable(BackpressureStrategy.LATEST)
        .map { MultiSelectEpisodeAction.listFromIds(it) }
        .toLiveData()
        .combineLatest(_selectedListLive)
        .map { (actions, selectedEpisodes) ->
            crashLogging.recordEvent("MultiSelectEpisodesHelper toolbarActions updated (${actions.size}): ${actions.map { it::class.java.simpleName }}, ${selectedEpisodes.size} selectedEpisodes from $source")
            actions.mapNotNull {
                MultiSelectEpisodeAction.actionForGroup(it.groupId, selectedEpisodes)
            }
        }

    override fun isSelected(multiSelectable: BaseEpisode) =
        selectedList.count { it.uuid == multiSelectable.uuid } > 0

    override fun onMenuItemSelected(itemId: Int, resources: Resources, activity: FragmentActivity): Boolean {
        val fragmentManager = activity.supportFragmentManager
        return when (itemId) {
            R.id.menu_archive -> {
                archive(resources = resources, fragmentManager = fragmentManager)
                true
            }
            R.id.menu_unarchive -> {
                unarchive(resources = resources)
                true
            }
            R.id.menu_delete -> {
                delete(resources, fragmentManager)
                true
            }
            R.id.menu_share -> {
                share(fragmentManager)
                true
            }
            R.id.menu_download -> {
                download(resources, fragmentManager)
                true
            }
            R.id.menu_undownload -> {
                deleteDownload()
                true
            }
            R.id.menu_mark_played -> {
                markAsPlayed(resources = resources, fragmentManager = fragmentManager)
                true
            }
            R.id.menu_markasunplayed -> {
                markAsUnplayed(resources = resources)
                true
            }
            R.id.menu_playnext -> {
                playNext(resources = resources)
                true
            }
            R.id.menu_playlast -> {
                playLast(resources = resources)
                true
            }
            R.id.menu_select_all -> {
                selectAll()
                true
            }
            R.id.menu_unselect_all -> {
                unselectAll()
                true
            }
            R.id.menu_removefromupnext -> {
                removeFromUpNext(resources = resources)
                true
            }
            R.id.menu_movetotop -> {
                moveToTop()
                true
            }
            R.id.menu_movetobottom -> {
                moveToBottom()
                true
            }
            R.id.menu_star -> {
                star(resources = resources)
                true
            }
            R.id.menu_unstar -> {
                unstar(resources = resources)
                true
            }
            else -> false
        }
    }

    override fun deselect(multiSelectable: BaseEpisode) {
        if (isSelected(multiSelectable)) {
            val selectedItem = selectedList.firstOrNull { it.uuid == multiSelectable.uuid }
            selectedItem?.let { selectedList.remove(it) }
        }

        _selectedListLive.value = selectedList

        if (selectedList.isEmpty()) {
            closeMultiSelect()
        }
    }

    fun markAsPlayed(shownWarning: Boolean = false, resources: Resources, fragmentManager: FragmentManager) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        launch {
            val list = selectedList.toList()
            if (!shownWarning && list.size > WARNING_LIMIT) {
                playedWarning(list.size, resources = resources, fragmentManager = fragmentManager)
                return@launch
            }

            episodeManager.markAllAsPlayed(list, playbackManager, podcastManager)
            episodeAnalytics.trackBulkEvent(AnalyticsEvent.EPISODE_BULK_MARKED_AS_PLAYED, source, list.size)
            launch(Dispatchers.Main) {
                val snackText = resources.getStringPlural(selectedList.size, R.string.marked_as_played_singular, R.string.marked_as_played_plural)
                showSnackBar(snackText)
                closeMultiSelect()
            }
        }
    }

    private fun markAsUnplayed(resources: Resources) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        launch {
            val list = selectedList.toList()

            episodeManager.markAsUnplayed(list)
            episodeAnalytics.trackBulkEvent(AnalyticsEvent.EPISODE_BULK_MARKED_AS_UNPLAYED, source, list.size)
            launch(Dispatchers.Main) {
                val snackText = resources.getStringPlural(selectedList.size, R.string.marked_as_unplayed_singular, R.string.marked_as_unplayed_plural)
                showSnackBar(snackText)
                closeMultiSelect()
            }
        }
    }

    fun archive(shownWarning: Boolean = false, resources: Resources, fragmentManager: FragmentManager) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        launch {
            val list = selectedList.filterIsInstance<PodcastEpisode>().toList()
            if (!shownWarning && list.size > WARNING_LIMIT) {
                archiveWarning(list.size, resources = resources, fragmentManager = fragmentManager)
                return@launch
            }

            episodeManager.archiveAllInList(list, playbackManager)
            episodeAnalytics.trackBulkEvent(AnalyticsEvent.EPISODE_BULK_ARCHIVED, source, list.size)
            withContext(Dispatchers.Main) {
                val snackText = resources.getStringPlural(selectedList.size, R.string.archived_episodes_singular, R.string.archived_episodes_plural)
                showSnackBar(snackText)
                closeMultiSelect()
            }
        }
    }

    private fun unarchive(resources: Resources) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        launch {
            val list = selectedList.filterIsInstance<PodcastEpisode>().toList()

            episodeManager.unarchiveAllInListBlocking(episodes = list)
            episodeAnalytics.trackBulkEvent(AnalyticsEvent.EPISODE_BULK_UNARCHIVED, source, list.size)
            withContext(Dispatchers.Main) {
                val snackText = resources.getStringPlural(selectedList.size, R.string.unarchived_episodes_singular, R.string.unarchived_episodes_plural)
                showSnackBar(snackText)
                closeMultiSelect()
            }
        }
    }

    fun star(resources: Resources) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        launch {
            val list = selectedList.filterIsInstance<PodcastEpisode>().toList()
            episodeManager.updateAllStarred(list, starred = true)
            episodeAnalytics.trackBulkEvent(AnalyticsEvent.EPISODE_BULK_STARRED, source, list.size)
            withContext(Dispatchers.Main) {
                val snackText = resources.getStringPlural(selectedList.size, R.string.starred_episodes_singular, R.string.starred_episodes_plural)
                showSnackBar(snackText)
                closeMultiSelect()
            }
        }
    }

    private fun unstar(resources: Resources) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        launch {
            val list = selectedList.filterIsInstance<PodcastEpisode>().toList()
            episodeManager.updateAllStarred(list, starred = false)
            episodeAnalytics.trackBulkEvent(AnalyticsEvent.EPISODE_BULK_UNSTARRED, source, list.size)
            withContext(Dispatchers.Main) {
                val snackText = resources.getStringPlural(selectedList.size, R.string.unstarred_episodes_singular, R.string.unstarred_episodes_plural)
                showSnackBar(snackText)
                closeMultiSelect()
            }
        }
    }

    fun playedWarning(count: Int, resources: Resources, fragmentManager: FragmentManager) {
        val buttonString = resources.getStringPlural(count = count, singular = R.string.mark_as_played_singular, plural = R.string.mark_as_played_plural)

        ConfirmationDialog()
            .setTitle(resources.getString(R.string.mark_as_played_title))
            .setIconId(R.drawable.ic_markasplayed)
            .setButtonType(ConfirmationDialog.ButtonType.Danger(buttonString))
            .setOnConfirm { markAsPlayed(shownWarning = true, resources = resources, fragmentManager = fragmentManager) }
            .show(fragmentManager, "confirm_played_all_")
    }

    private fun archiveWarning(count: Int, resources: Resources, fragmentManager: FragmentManager) {
        val buttonString = resources.getStringPlural(count = count, singular = R.string.archive_episodes_singular, plural = R.string.archive_episodes_plural)

        ConfirmationDialog()
            .setTitle(resources.getString(R.string.archive_title))
            .setSummary(resources.getString(R.string.archive_summary))
            .setIconId(R.drawable.ic_archive)
            .setButtonType(ConfirmationDialog.ButtonType.Danger(buttonString))
            .setOnConfirm { archive(shownWarning = true, resources = resources, fragmentManager = fragmentManager) }
            .show(fragmentManager, "confirm_archive_all_")
    }

    fun download(resources: Resources, fragmentManager: FragmentManager) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        val list = selectedList.toList()
        val trimmedList = list.subList(0, min(Settings.MAX_DOWNLOAD, selectedList.count())).toList()
        ConfirmationDialog.downloadWarningDialog(list.count(), resources) {
            trimmedList.forEach {
                downloadManager.addEpisodeToQueue(it, "podcast download all", fireEvent = false, source = source)
            }
            episodeAnalytics.trackBulkEvent(AnalyticsEvent.EPISODE_BULK_DOWNLOAD_QUEUED, source, trimmedList)
            val snackText = resources.getStringPlural(trimmedList.size, R.string.download_queued_singular, R.string.download_queued_plural)
            showSnackBar(snackText)
            closeMultiSelect()
        }?.show(fragmentManager, "multiselect_download")
    }

    private fun deleteDownload() {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        val list = selectedList.toList()
        launch {
            val episodes = list.filterIsInstance<PodcastEpisode>()
            episodeManager.deleteEpisodeFiles(episodes, playbackManager)

            val userEpisodes = list.filterIsInstance<UserEpisode>()
            userEpisodeManager.deleteAll(userEpisodes, playbackManager)

            if (episodes.isNotEmpty()) {
                episodeAnalytics.trackBulkEvent(
                    AnalyticsEvent.EPISODE_BULK_DOWNLOAD_DELETED,
                    source = source,
                    count = if (episodes.isNotEmpty()) episodes.size else userEpisodes.size,
                )
            }

            withContext(Dispatchers.Main) {
                closeMultiSelect()
            }
        }
    }

    private fun playNext(resources: Resources) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        val size = min(settings.getMaxUpNextEpisodes(), selectedList.count())
        val trimmedList = selectedList.subList(0, size).toList()
        launch {
            playbackManager.playEpisodesNext(episodes = trimmedList, source = source)
            withContext(Dispatchers.Main) {
                val snackText = resources.getStringPlural(size, R.string.added_to_up_next_singular, R.string.added_to_up_next_plural)
                showSnackBar(snackText)
                closeMultiSelect()
            }
        }
    }

    private fun playLast(resources: Resources) {
        if (selectedList.isEmpty()) {
            closeMultiSelect()
            return
        }

        val size = min(settings.getMaxUpNextEpisodes(), selectedList.count())
        val trimmedList = selectedList.subList(0, size).toList()
        launch {
            playbackManager.playEpisodesLast(episodes = trimmedList, source = source)
            withContext(Dispatchers.Main) {
                val snackText = resources.getStringPlural(size, R.string.added_to_up_next_singular, R.string.added_to_up_next_plural)
                showSnackBar(snackText)
                closeMultiSelect()
            }
        }
    }

    fun delete(resources: Resources, fragmentManager: FragmentManager) {
        val episodes = selectedList.filterIsInstance<UserEpisode>()
        if (episodes.isEmpty()) return

        val onServer = episodes.count { it.isUploaded }
        val onDevice = episodes.count { it.isDownloaded }

        val deleteState = CloudDeleteHelper.getDeleteState(isDownloaded = onDevice > 0, isBoth = onServer > 0 && onDevice > 0)
        val deleteFunction: (List<UserEpisode>, DeleteState) -> Unit = { episodesToDelete, state ->
            episodesToDelete.forEach {
                Timber.d("Deleting $it")
                CloudDeleteHelper.deleteEpisode(
                    episode = it,
                    deleteState = state,
                    playbackManager = playbackManager,
                    episodeManager = episodeManager,
                    userEpisodeManager = userEpisodeManager,
                    applicationScope = applicationScope,
                )
            }
            episodeAnalytics.trackBulkEvent(AnalyticsEvent.EPISODE_BULK_DOWNLOAD_DELETED, source, episodesToDelete.size)

            val snackText = resources.getStringPlural(episodesToDelete.size, R.string.episodes_deleted_singular, R.string.episodes_deleted_plural)
            showSnackBar(snackText)
            closeMultiSelect()
        }
        CloudDeleteHelper.getDeleteDialog(episodes, deleteState, deleteFunction, resources).show(fragmentManager, "delete_warning")
    }

    fun share(fragmentManager: FragmentManager) {
        val episode = selectedList.let { list ->
            if (list.size != 1) {
                LogBuffer.e(LogBuffer.TAG_INVALID_STATE, "Can only share one episode, but trying to share ${selectedList.size} episodes when multi selecting")
                return
            } else {
                list.first()
            }
        }

        if (episode !is PodcastEpisode) {
            LogBuffer.e(LogBuffer.TAG_INVALID_STATE, "Can only share a ${PodcastEpisode::class.java.simpleName}")
            Toast.makeText(context, R.string.podcasts_share_failed, Toast.LENGTH_SHORT).show()
            return
        }

        launch {
            val podcast = podcastManager.findPodcastByUuidSuspend(episode.podcastUuid) ?: run {
                LogBuffer.e(LogBuffer.TAG_INVALID_STATE, "Share failed because unable to find podcast from uuid")
                return@launch
            }

            shareDialogFactory
                .shareEpisode(podcast, episode, SourceView.MULTI_SELECT)
                .show(fragmentManager, "share_dialog")
        }

        closeMultiSelect()
    }

    private fun removeFromUpNext(resources: Resources) {
        val list = selectedList.toList()
        launch {
            list.forEach {
                playbackManager.upNextQueue.removeEpisode(it)
            }

            withContext(Dispatchers.Main) {
                val snackText = resources.getStringPlural(list.size, R.string.removed_from_up_next_singular, R.string.removed_from_up_next_plural)
                showSnackBar(snackText)
            }
        }

        closeMultiSelect()
    }

    private fun moveToTop() {
        val list = selectedList.toList()
        playbackManager.playEpisodesNext(episodes = list, source = source)
        closeMultiSelect()
    }

    private fun moveToBottom() {
        val list = selectedList.toList()
        playbackManager.playEpisodesLast(episodes = list, source = source)
        closeMultiSelect()
    }
}
