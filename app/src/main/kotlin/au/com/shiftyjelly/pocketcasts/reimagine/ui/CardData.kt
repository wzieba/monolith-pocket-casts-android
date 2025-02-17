package au.com.shiftyjelly.pocketcasts.reimagine.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.compose.components.EpisodeImage
import au.com.shiftyjelly.pocketcasts.compose.components.PodcastImage
import au.com.shiftyjelly.pocketcasts.models.entity.Podcast
import au.com.shiftyjelly.pocketcasts.models.entity.PodcastEpisode
import au.com.shiftyjelly.pocketcasts.repositories.images.PocketCastsImageRequestFactory.PlaceholderType
import au.com.shiftyjelly.pocketcasts.utils.extensions.toLocalizedFormatMediumStyle

internal sealed interface CardData {
    @Composable
    fun topText(): String

    @Composable
    fun middleText(): String

    @Composable
    fun bottomText(): String

    @Composable
    fun Image(modifier: Modifier)
}

internal data class PodcastCardData(
    val podcast: Podcast,
    val episodeCount: Int,
) : CardData {
    @Composable
    override fun topText() = podcast.title

    @Composable
    override fun middleText() = podcast.title

    @Composable
    override fun bottomText() = listOfNotNull(
        pluralStringResource(R.plurals.episode_count, count = episodeCount, episodeCount),
        podcast.displayableFrequency(LocalContext.current.resources),
    ).joinToString(" · ")

    @Composable
    override fun Image(modifier: Modifier) = PodcastImage(
        uuid = podcast.uuid,
        title = stringResource(R.string.podcast_cover_description, podcast.title),
        placeholderType = if (LocalInspectionMode.current) PlaceholderType.Large else PlaceholderType.None,
        modifier = modifier,
    )
}

internal data class EpisodeCardData(
    val episode: PodcastEpisode,
    val podcast: Podcast,
    val useEpisodeArtwork: Boolean,
) : CardData {
    @Composable
    override fun topText() = episode.publishedDate.toLocalizedFormatMediumStyle()

    @Composable
    override fun middleText() = episode.title

    @Composable
    override fun bottomText() = podcast.title

    @Composable
    override fun Image(modifier: Modifier) = EpisodeImage(
        episode = episode,
        useEpisodeArtwork = useEpisodeArtwork,
        placeholderType = if (LocalInspectionMode.current) PlaceholderType.Large else PlaceholderType.None,
        modifier = modifier,
    )
}
