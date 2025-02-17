package au.com.shiftyjelly.pocketcasts.discover.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import au.com.shiftyjelly.pocketcasts.R
import au.com.shiftyjelly.pocketcasts.discover.extensions.updateSubscribeButtonIcon
import au.com.shiftyjelly.pocketcasts.repositories.images.PocketCastsImageRequestFactory
import au.com.shiftyjelly.pocketcasts.repositories.images.loadInto
import au.com.shiftyjelly.pocketcasts.servers.model.DiscoverPodcast
import au.com.shiftyjelly.pocketcasts.ui.extensions.themed
import au.com.shiftyjelly.pocketcasts.views.extensions.setRippleBackground

class PodcastGridRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var imageRequestFactory = PocketCastsImageRequestFactory(context).themed()
    private val lblTitle: TextView
    private val lblSubtitle: TextView
    private val btnSubscribe: ImageButton
    private val imagePodcast: ImageView
    private var imageSize: Int? = null

    var onSubscribeClickedListener: ((String) -> Unit)? = null
        set(value) {
            field = value
            val listener = if (value != null) {
                OnClickListener {
                    val uuid = podcast?.uuid ?: return@OnClickListener
                    btnSubscribe.updateSubscribeButtonIcon(subscribed = true, colorSubscribed = R.attr.contrast_01, colorUnsubscribed = R.attr.contrast_01)
                    value(uuid)
                }
            } else {
                null
            }
            btnSubscribe.setOnClickListener(listener)
        }

    var onPodcastClickedListener: ((DiscoverPodcast) -> Unit)? = null
        set(value) {
            field = value
            val listener = if (value != null) {
                OnClickListener {
                    podcast?.let(value::invoke)
                }
            } else {
                null
            }
            setOnClickListener(listener)
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_grid, this, true)
        lblTitle = findViewById(R.id.lblTitle)
        lblSubtitle = findViewById(R.id.lblSubtitle)
        btnSubscribe = findViewById(R.id.btnSubscribe)
        imagePodcast = findViewById(R.id.imagePodcast)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = HORIZONTAL
        clipToPadding = false
        clipChildren = false

        setRippleBackground()
    }

    var podcast: DiscoverPodcast? = null
        set(value) {
            field = value

            if (value != null) {
                lblTitle.text = value.title
                lblSubtitle.text = value.author
                loadImage()
                isVisible = true
                btnSubscribe.updateSubscribeButtonIcon(subscribed = value.isSubscribed, colorSubscribed = R.attr.contrast_01, colorUnsubscribed = R.attr.contrast_01)
            } else {
                clear()
            }
        }

    fun updateImageSize(imageSize: Int) {
        this.imageSize = imageSize
        this.imageRequestFactory = imageRequestFactory.copy(size = imageSize)
        loadImage()
    }

    private fun loadImage() {
        val podcast = podcast
        val imageSize = imageSize
        if (podcast != null && imageSize != null) {
            imageRequestFactory.createForPodcast(podcast.uuid).loadInto(imagePodcast)
        }
    }

    fun clear() {
        lblTitle.text = null
        lblSubtitle.text = null
        imagePodcast.setImageBitmap(null)
        isVisible = false
    }
}
