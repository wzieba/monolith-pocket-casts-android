package au.com.shiftyjelly.pocketcasts.settings.viewmodel

import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsTracker
import au.com.shiftyjelly.pocketcasts.models.entity.PodcastEpisode
import au.com.shiftyjelly.pocketcasts.repositories.playback.PlaybackManager
import au.com.shiftyjelly.pocketcasts.repositories.podcast.EpisodeManager
import au.com.shiftyjelly.pocketcasts.sharedtest.MainCoroutineRule
import io.reactivex.Flowable
import java.util.Date
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ManualCleanupViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()
    private lateinit var episodeManager: EpisodeManager
    private lateinit var playbackManager: PlaybackManager
    private lateinit var viewModel: ManualCleanupViewModel

    private val episode: PodcastEpisode = PodcastEpisode(uuid = "1", publishedDate = Date())
    private val episodes = listOf(episode)
    private val diskSpaceView =
        ManualCleanupViewModel.State.DiskSpaceView(title = R.string.unplayed, episodes = episodes)

    @Before
    fun setUp() {
        episodeManager = mock()
        playbackManager = mock()
        whenever(episodeManager.findDownloadedEpisodesRxFlowable())
            .thenReturn(Flowable.generate { listOf(episodes) })
        viewModel = ManualCleanupViewModel(episodeManager, playbackManager, AnalyticsTracker.test())
    }

    @Test
    fun `given episodes present, when disk space size checked, then delete button is enabled`() {
        viewModel.onDiskSpaceCheckedChanged(isChecked = true, diskSpaceView = diskSpaceView)

        assertTrue(viewModel.state.value.deleteButton.isEnabled)
    }

    @Test
    fun `given episodes present, when disk space size unchecked, then delete button is disabled`() {
        viewModel.onDiskSpaceCheckedChanged(isChecked = false, diskSpaceView = diskSpaceView)

        assertFalse(viewModel.state.value.deleteButton.isEnabled)
    }

    @Test
    fun `given episodes not present, when disk space size checked, then delete button is disabled`() {
        viewModel.onDiskSpaceCheckedChanged(
            isChecked = true,
            diskSpaceView = diskSpaceView.copy(episodes = emptyList()),
        )

        assertFalse(viewModel.state.value.deleteButton.isEnabled)
    }

    @Test
    fun `given episodes selected, when delete button clicked, then delete action invoked`() {
        whenever(episodeManager.findDownloadedEpisodesRxFlowable())
            .thenReturn(Flowable.generate { listOf(episode) })
        val deleteButtonClickAction = mock<() -> Unit>()
        viewModel.setup(deleteButtonClickAction)
        viewModel.onDiskSpaceCheckedChanged(isChecked = true, diskSpaceView = diskSpaceView)

        viewModel.onDeleteButtonClicked()

        verify(deleteButtonClickAction).invoke()
    }

    @Test
    fun `given episodes not selected, when delete button clicked, then episodes are not deleted`() {
        whenever(episodeManager.findDownloadedEpisodesRxFlowable())
            .thenReturn(Flowable.generate { listOf(episode) })
        viewModel.onDiskSpaceCheckedChanged(isChecked = false, diskSpaceView = diskSpaceView)

        viewModel.onDeleteButtonClicked()

        verifyBlocking(episodeManager, never()) {
            deleteEpisodeFiles(episodes, playbackManager)
        }
    }
}
