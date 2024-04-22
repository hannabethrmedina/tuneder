package edu.utap.tuneder3.glide

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import edu.utap.tuneder3.R
import edu.utap.tuneder3.MainActivity


@GlideModule
class AppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // You can change this to make Glide more verbose
        builder.setLogLevel(Log.ERROR)
    }
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

// Calling glideapp.with with the most specific Activity/Fragment
// context allows it to track lifecycles for your fetch
// https://stackoverflow.com/questions/31964737/glide-image-loading-with-application-context
object GlideHelper {
    private val width = Resources.getSystem().displayMetrics.widthPixels
    private val height = Resources.getSystem().displayMetrics.heightPixels
    private var glideOptions = RequestOptions ()
        // Options like CenterCrop are possible, but I like this one best
        // Evidently you need fitCenter or dontTransform.  If you use centerCrop, your
        // list disappears.  I think that was an old bug.
        .fitCenter()
        // Rounded corners are so lovely.
        .transform(RoundedCorners (20))

    private fun fromHtml(source: String): String {
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY).toString()
    }
    // Please ignore, this is for our testing
    private fun assetFetch(urlString: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(urlString)
            .apply(glideOptions)
            .override(width, height)
            .into(imageView)
        if (urlString.endsWith("bigcat0.jpg")) {
            imageView.tag = "bigcat0.jpg"
        } else if (urlString.endsWith("bigcat1.jpg")) {
            imageView.tag = "bigcat1.jpg"
        } else if (urlString.endsWith("bigcat2.jpg")) {
            imageView.tag = "bigcat2.jpg"
        } else if (urlString.endsWith("bigdog0.jpg")) {
            imageView.tag = "bigdog0.jpg"
        }
    }
    fun glideFetch(urlString: String, thumbnailURL: String, imageView: ImageView) {
        if (MainActivity.globalDebug) {
           assetFetch(urlString, imageView)
        } else {
            Glide.with(imageView.context)
                .asBitmap() // Try to display animated Gifs and video still
                .load(fromHtml(urlString))
                .apply(glideOptions)
                .error(R.color.teal_200)
                .override(width, height)
                .error(
                    Glide.with(imageView.context)
                        .asBitmap()
                        .load(fromHtml(thumbnailURL))
                        .apply(glideOptions)
                        .error(R.color.teal_200)
                        .override(500, 500)
                )
                .into(imageView)
        }
    }
}
