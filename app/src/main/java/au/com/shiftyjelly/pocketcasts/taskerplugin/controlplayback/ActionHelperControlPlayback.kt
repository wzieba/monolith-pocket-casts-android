package au.com.shiftyjelly.pocketcasts.taskerplugin.controlplayback

import androidx.annotation.StringRes
import au.com.shiftyjelly.pocketcasts.R
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput

class ActionHelperControlPlayback(config: TaskerPluginConfig<InputControlPlayback>) : TaskerPluginConfigHelperNoOutput<InputControlPlayback, ActionRunnerControlPlayback>(config) {
    override val runnerClass get() = ActionRunnerControlPlayback::class.java
    override fun addToStringBlurb(input: TaskerInput<InputControlPlayback>, blurbBuilder: StringBuilder) {
        val inputControlPlayback = input.regular
        val commandEnum = inputControlPlayback.commandEnum ?: return

        fun addField(@StringRes name: Int, value: String?) {
            if (value.isNullOrEmpty()) return

            blurbBuilder.append("${context.getString(name)}: $value")
        }
        when (commandEnum) {
            InputControlPlayback.PlaybackCommand.SkipToChapter -> addField(R.string.chapter_to_skip_to, inputControlPlayback.chapterToSkipTo)
            InputControlPlayback.PlaybackCommand.SkipToTime -> addField(R.string.time_to_skip_to_seconds, inputControlPlayback.skipToSeconds)
            InputControlPlayback.PlaybackCommand.SkipForward -> addField(R.string.skip_forward, inputControlPlayback.skipSeconds)
            InputControlPlayback.PlaybackCommand.SkipBack -> addField(R.string.skip_back, inputControlPlayback.skipSeconds)
            InputControlPlayback.PlaybackCommand.SetPlaybackSpeed -> addField(R.string.set_playback_speed, inputControlPlayback.playbackSpeed)
            InputControlPlayback.PlaybackCommand.SetTrimSilenceMode -> addField(R.string.set_trim_silence_mode, inputControlPlayback.trimSilenceMode)
            InputControlPlayback.PlaybackCommand.SetVolumeBoost -> addField(R.string.set_volume_boost, inputControlPlayback.volumeBoostEnabled)
            InputControlPlayback.PlaybackCommand.PlayNextInQueue,
            InputControlPlayback.PlaybackCommand.SkipToNextChapter,
            InputControlPlayback.PlaybackCommand.SkipToPreviousChapter,
            -> blurbBuilder.append(commandEnum.getDescription(context)) // these don't have extra arguments so just write the command name
        }
    }

    override val addDefaultStringBlurb get() = false
    override val inputClass get() = InputControlPlayback::class.java
}
