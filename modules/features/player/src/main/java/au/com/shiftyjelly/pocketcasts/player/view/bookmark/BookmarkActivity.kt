package au.com.shiftyjelly.pocketcasts.player.view.bookmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import au.com.shiftyjelly.pocketcasts.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_blank_fragment)

        val arguments = BookmarkArguments.createFromIntent(intent)

        // tint the status bar color
        window.statusBarColor = arguments.backgroundColor
        // set the status bar icons to white
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        if (savedInstanceState == null) {
            val fragment = arguments.buildFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }
}
