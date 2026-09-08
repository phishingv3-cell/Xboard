package com.xboard.sinhalakeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView

class SinhalaInputMethodService : InputMethodService() {

    private lateinit var emojiManager: EmojiManager
    private lateinit var liveThemeManager: LiveThemeManager
    private lateinit var vibrationManager: VibrationManager
    private lateinit var userHistoryManager: UserHistoryManager

    private var currentInputText = StringBuilder()
    private var isEnglishMode = true

    private var normalKeypadLayout: LinearLayout? = null
    private var emojiFullLayout: LinearLayout? = null

    override fun onCreate() {
        super.onCreate()
        vibrationManager = VibrationManager(this)
        userHistoryManager = UserHistoryManager(this)
    }

    override fun onCreateInputView(): View {
        val rootView = layoutInflater.inflate(R.layout.keyboard_view, null)

        // Initialize Managers based on keyboard_view.xml
        emojiManager = EmojiManager(this)
        emojiManager.init(rootView)

        liveThemeManager = LiveThemeManager(this)
        liveThemeManager.init(rootView)
        liveThemeManager.applyCurrentTheme()

        // Bind XML layouts from keyboard_view.xml
        normalKeypadLayout = rootView.findViewById(R.id.normal_keypad_layout)
        emojiFullLayout = rootView.findViewById(R.id.emoji_full_layout)

        return rootView
    }

    private fun handleCharacterInput(charSeq: String) {
        vibrationManager.vibrateKeyClick(25)
        
        if (isEnglishMode) {
            currentInputText.append(charSeq)
            // Transliterate English to Sinhala using SinhalaTransliterationEngine
            val transliterated = SinhalaTransliterationEngine.transliterateInput(currentInputText.toString())
            currentInputText.toString().commitText(transliterated)
        } else {
            currentInputText.append(charSeq)
            currentInputText.toString().commitText(currentInputText.toString())
        }
    }

    private fun String.commitText(textToCommit: String = this) {
        val ic = currentInputConnection
        ic?.commitText(textToCommit, 1)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        liveThemeManager.applyCurrentTheme()
    }
}
