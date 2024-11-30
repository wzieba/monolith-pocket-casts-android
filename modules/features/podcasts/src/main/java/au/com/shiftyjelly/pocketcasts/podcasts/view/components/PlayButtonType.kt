package au.com.shiftyjelly.pocketcasts.podcasts.view.components

import au.com.shiftyjelly.pocketcasts.R

enum class PlayButtonType(val drawableId: Int, val label: String) {
    DOWNLOAD(R.drawable.button_download, "Download"),
    PLAY(R.drawable.button_play, "Play"),
    PAUSE(R.drawable.button_pause, "Pause"),
    PLAYED(R.drawable.button_played, "Mark unplayed"),
    PLAYBACK_FAILED(R.drawable.button_retry, "Playback failed"),
    STOP_DOWNLOAD(R.drawable.ic_downloading, "Stop Downloading"),
}
