package com.xboard.sinhalakeyboard

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

class LiveThemeManager(private val context: Context) {
    private var bgImageView: ImageView? = null
    private var rootLayout: View? = null

    fun init(rootView: View) {
        // Keyboard එකේ Main Layout එක සහ Background ImageView එක ලබා ගැනීම
        rootLayout = rootView
        bgImageView = rootView.findViewById(R.id.live_theme_bg)
    }

    fun applyCurrentTheme() {
        val prefs = context.getSharedPreferences("keyboard_settings", Context.MODE_PRIVATE)
        val gifFileName = prefs.getString("live_theme_gif", "none")

        if (bgImageView != null && !gifFileName.isNullOrEmpty() && gifFileName != "none") {
            // Live GIF Theme එකක් Select කර තිබේ නම්
            Glide.with(context)
                .asGif()
                .load("file:///android_asset/$gifFileName")
                .into(bgImageView!!)
        } else {
            // Live Theme නැති විට Default Dark Background එක ලබා දීම
            bgImageView?.setImageDrawable(null)
            rootLayout?.setBackgroundColor(Color.parseColor("#000000"))
        }
    }
}
