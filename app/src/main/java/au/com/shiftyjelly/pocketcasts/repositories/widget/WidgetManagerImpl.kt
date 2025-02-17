package au.com.shiftyjelly.pocketcasts.repositories.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.core.ui.widget.PodcastWidget
import au.com.shiftyjelly.pocketcasts.models.entity.BaseEpisode
import au.com.shiftyjelly.pocketcasts.models.entity.Podcast
import au.com.shiftyjelly.pocketcasts.models.entity.PodcastEpisode
import au.com.shiftyjelly.pocketcasts.models.entity.UserEpisode
import au.com.shiftyjelly.pocketcasts.preferences.Settings
import au.com.shiftyjelly.pocketcasts.repositories.images.PocketCastsImageRequestFactory
import au.com.shiftyjelly.pocketcasts.repositories.images.PocketCastsImageRequestFactory.PlaceholderType
import au.com.shiftyjelly.pocketcasts.repositories.images.loadInto
import au.com.shiftyjelly.pocketcasts.repositories.playback.PlaybackManager
import au.com.shiftyjelly.pocketcasts.repositories.podcast.PodcastManager
import au.com.shiftyjelly.pocketcasts.utils.AppPlatform
import au.com.shiftyjelly.pocketcasts.utils.Util
import au.com.shiftyjelly.pocketcasts.utils.extensions.getLaunchActivityPendingIntent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class WidgetManagerImpl @Inject constructor(
    private val settings: Settings,
    private val podcastManager: PodcastManager,
    @ApplicationContext private val context: Context,
) : WidgetManager, CoroutineScope {

    override val coroutineContext: CoroutineContext get() = Dispatchers.IO

    private var remoteViewsLayoutId: Int = getRemoteViewsLayoutId()

    private val imageRequestFactory = PocketCastsImageRequestFactory(context, isDarkTheme = true, placeholderType = PlaceholderType.Small).smallSize()

    override fun updateWidget(podcast: Podcast?, playing: Boolean, playingEpisode: BaseEpisode?) {
        when (Util.getAppPlatform(context)) {
            AppPlatform.Automotive,
            AppPlatform.WearOs,
            -> { /* do nothing */ }
            AppPlatform.Phone -> {
                try {
                    val appWidgetManager = AppWidgetManager.getInstance(context)

                    val views = RemoteViews(context.packageName, remoteViewsLayoutId)
                    val widgetName = ComponentName(context, PodcastWidget::class.java)
                    if (playingEpisode == null) {
                        showPlayingControls(false, views)
                    } else {
                        showPlayingControls(true, views)
                        updateArtWork(podcast, playingEpisode, views, widgetName, context)
                        showPlayButton(playing, views)
                        updateSkipAmounts(views, settings)
                    }
                    updateOnClicks(views, context)
                    appWidgetManager.updateAppWidget(widgetName, views)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    override fun updateWidgetFromSettings(playbackManager: PlaybackManager?) {
        try {
            /* We cannot apply a theme dynamically to an app widget.
            As a workaround, multiple layouts are provided, and the correct theme/layout is picked
            when remote views layout needs to be updated.
            https://stackoverflow.com/a/4501902/193545 */
            remoteViewsLayoutId = getRemoteViewsLayoutId()

            val views = RemoteViews(context.packageName, remoteViewsLayoutId)
            val widgetName = ComponentName(context, PodcastWidget::class.java)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(widgetName, views)

            updateWidgetFromPlaybackState(playbackManager)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun updateWidgetEpisodeArtwork(playbackManager: PlaybackManager) {
        val currentEpisode = playbackManager.getCurrentEpisode() ?: return
        val target = RemoteViewsTarget(
            context,
            ComponentName(context, PodcastWidget::class.java),
            RemoteViews(context.packageName, remoteViewsLayoutId),
            R.id.widget_artwork,
        )
        imageRequestFactory.create(currentEpisode, settings.artworkConfiguration.value.useEpisodeArtwork).loadInto(target)
    }

    override fun updateWidgetFromPlaybackState(playbackManager: PlaybackManager?) {
        val episode = playbackManager?.getCurrentEpisode() ?: return
        val podcast = findPodcastByEpisode(episode)
        updateWidget(podcast, playbackManager.isPlaying(), playbackManager.getCurrentEpisode())
    }

    private fun findPodcastByEpisode(episode: BaseEpisode): Podcast? {
        return when (episode) {
            is PodcastEpisode -> podcastManager.findPodcastByUuid(episode.podcastUuid)
            is UserEpisode -> podcastManager.buildUserEpisodePodcast(episode)
            else -> null
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    override fun updateWidgetFromProvider(context: Context, manager: AppWidgetManager, widgetIds: IntArray, playbackManager: PlaybackManager?) {
        if (context == null || Util.isAutomotive(context)) {
            return
        }

        val isPlaying = playbackManager != null && playbackManager.isPlaying()

        val widgetName = ComponentName(context, PodcastWidget::class.java)

        for (i in widgetIds.indices) {
            val widgetId = widgetIds[i]

            val views = RemoteViews(context.packageName, remoteViewsLayoutId)
            updateOnClicks(views, context)
            updateSkipAmounts(views, settings)
            setupView(views, isPlaying, playbackManager, widgetName, context)

            try {
                manager.updateAppWidget(widgetId, views)
            } catch (e: Exception) {
                // sometimes widgets are not able to be updated, ignore this one and move on to the next one
                Timber.e(e)
            }
        }
    }

    private fun showPlayingControls(visible: Boolean, views: RemoteViews) {
        views.setViewVisibility(R.id.widget_empty_player, if (visible) View.GONE else View.VISIBLE)
        views.setViewVisibility(R.id.widget_podcast_playing, if (visible) View.VISIBLE else View.GONE)
    }

    private fun setupView(views: RemoteViews, isPlaying: Boolean, playbackManager: PlaybackManager?, widgetName: ComponentName, context: Context) {
        val episode = playbackManager?.getCurrentEpisode()
        if (episode == null) {
            showPlayingControls(false, views)
        } else {
            showPlayingControls(true, views)
            showPlayButton(isPlaying, views)
            val podcast = findPodcastByEpisode(episode)
            updateArtWork(podcast, playbackManager.getCurrentEpisode(), views, widgetName, context)
        }
    }

    private fun updateOnClicks(views: RemoteViews, context: Context) {
        with(views) {
            getSkipBackIntent()?.let { intent -> setOnClickPendingIntent(R.id.widget_skip_back, intent) }
            getSkipForwardIntent()?.let { intent -> setOnClickPendingIntent(R.id.widget_skip_forward, intent) }
            getPlayIntent()?.let { intent -> setOnClickPendingIntent(R.id.widget_play_button, intent) }
            getPauseIntent()?.let { intent -> setOnClickPendingIntent(R.id.widget_pause_button, intent) }
            val openAppIntent = getOpenAppIntent(context)
            setOnClickPendingIntent(R.id.widget_artwork, openAppIntent)
            setOnClickPendingIntent(R.id.widget_empty_player, openAppIntent)
        }
    }

    private fun updateSkipAmounts(views: RemoteViews, settings: Settings) {
        val jumpFwdAmount = settings.skipForwardInSecs.value
        val jumpBackAmount = settings.skipBackInSecs.value

        views.setTextViewText(R.id.widget_skip_back_text, "$jumpBackAmount")
        views.setContentDescription(R.id.widget_skip_back_text, "Skip back $jumpBackAmount seconds")
        views.setTextViewText(R.id.widget_skip_forward_text, "$jumpFwdAmount")
        views.setContentDescription(R.id.widget_skip_forward_text, "Skip forward $jumpFwdAmount seconds")
    }

    private fun updateArtWork(podcast: Podcast?, playingEpisode: BaseEpisode?, views: RemoteViews, widgetName: ComponentName, context: Context) {
        if (playingEpisode == null) {
            views.setImageViewResource(R.id.widget_artwork, R.drawable.defaultartwork)
            views.setContentDescription(R.id.widget_artwork, "Open Pocket Casts")
            return
        }

        val podcastTitle = podcast?.title ?: Podcast.userPodcast.title
        views.setContentDescription(R.id.widget_artwork, "$podcastTitle. Open Pocket Casts")
        views.setImageViewResource(R.id.widget_artwork, R.drawable.defaultartwork_small_dark)

        val target = RemoteViewsTarget(
            context,
            widgetName,
            views,
            R.id.widget_artwork,
        )
        imageRequestFactory.create(playingEpisode, settings.artworkConfiguration.value.useEpisodeArtwork).loadInto(target)
    }

    private fun showPlayButton(playing: Boolean, views: RemoteViews) {
        views.setViewVisibility(R.id.widget_play_button, if (playing) View.GONE else View.VISIBLE)
        views.setViewVisibility(R.id.widget_pause_button, if (playing) View.VISIBLE else View.GONE)
    }

    override fun updateWidgetNotPlaying() {
        updateWidget(null, false, playingEpisode = null)
    }

    private fun getOpenAppIntent(context: Context): PendingIntent {
        return context.getLaunchActivityPendingIntent()
    }

    private fun getPlayIntent(): PendingIntent? = PendingIntent.getBroadcast(
        context,
        PodcastWidget.PLAY_REQUEST_CODE,
        Intent(context, PodcastWidget::class.java).apply {
            action = PodcastWidget.PLAY_ACTION
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun getPauseIntent(): PendingIntent? = PendingIntent.getBroadcast(
        context,
        PodcastWidget.PAUSE_REQUEST_CODE,
        Intent(context, PodcastWidget::class.java).apply {
            action = PodcastWidget.PAUSE_ACTION
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun getSkipBackIntent(): PendingIntent? = PendingIntent.getBroadcast(
        context,
        PodcastWidget.SKIP_BACKWARD_REQUEST_CODE,
        Intent(context, PodcastWidget::class.java).apply {
            action = PodcastWidget.SKIP_BACKWARD_ACTION
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun getSkipForwardIntent(): PendingIntent? = PendingIntent.getBroadcast(
        context,
        PodcastWidget.SKIP_FORWARD_REQUEST_CODE,
        Intent(context, PodcastWidget::class.java).apply {
            action = PodcastWidget.SKIP_FORWARD_ACTION
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun getRemoteViewsLayoutId() = if (settings.useDynamicColorsForWidget.value) {
        R.layout.widget_dynamic_colors_theme
    } else {
        R.layout.widget_default_theme
    }
}
