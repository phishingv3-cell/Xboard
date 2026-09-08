package com.xboard.sinhalakeyboard

import android.content.Context
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView

class EmojiManager(private val context: Context) {
    private var normalLayout: LinearLayout? = null
    private var emojiLayout: LinearLayout? = null
    private var emojiGrid: GridLayout? = null

    // Emoji ලැයිස්තුව කෙලින්ම මෙතනට එකතු කළා
    private val allEmojis = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🥲", "🥹",
        "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗",
        "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥳",
        "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "😣", "😖", "😫",
        "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬", "🤯", "😳",
        "👍", "👎", "👏", "🙌", "🫶", "❤️", "🧡", "💛", "💚", "💙"
    )

    fun init(rootView: View) {
        normalLayout = rootView.findViewById(R.id.normal_keypad_layout)
        emojiLayout = rootView.findViewById(R.id.emoji_full_layout)
        emojiGrid = rootView.findViewById(R.id.emoji_grid_list)
    }

    fun toggleEmojiView(onEmojiClicked: (String) -> Unit) {
        if (emojiLayout?.visibility == View.VISIBLE) {
            emojiLayout?.visibility = View.GONE
            normalLayout?.visibility = View.VISIBLE
        } else {
            populateEmojis(onEmojiClicked)
            normalLayout?.visibility = View.GONE
            emojiLayout?.visibility = View.VISIBLE
        }
    }

    private fun populateEmojis(onEmojiClicked: (String) -> Unit) {
        emojiGrid?.removeAllViews()
        for (emoji in allEmojis) {
            val tv = TextView(context).apply {
                text = emoji
                textSize = 22f
                setPadding(6, 6, 6, 6)
                setOnClickListener { onEmojiClicked(emoji) }
            }
            emojiGrid?.addView(tv)
        }
    }
}
