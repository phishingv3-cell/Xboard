package com.xboard.sinhalakeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

class SinhalaInputMethodService : InputMethodService() {

    private lateinit var liveThemeManager: LiveThemeManager
    private lateinit var vibrationManager: VibrationManager
    private lateinit var emojiManager: EmojiManager
    private lateinit var userHistoryManager: UserHistoryManager

    private var currentComposingText = ""

    override fun onCreate() {
        super.onCreate()
        // Initialize Core Managers
        liveThemeManager = LiveThemeManager(this)
        vibrationManager = VibrationManager(this)
        emojiManager = EmojiManager(this)
        userHistoryManager = UserHistoryManager(this)
    }

    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)

        // 1. Live Theme Background Initialize
        liveThemeManager.init(keyboardView)
        liveThemeManager.applyCurrentTheme()

        // 2. Emoji Manager Setup (EmojiManager හි ඇති toggleEmojiView සහ init නිවැරදිව භාවිත කර ඇත)
        emojiManager.init(keyboardView)

        // 3. Setup Toolbar & Navigation Buttons (if applicable)
        setupToolbarButtons(keyboardView)

        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        // Refresh Theme on Keypad View Open
        liveThemeManager.applyCurrentTheme()
        currentComposingText = ""
    }

    private fun setupToolbarButtons(rootView: View) {
        // Toolbar UI Click Listeners Setup (e.g., Emoji Toggle)
        val btnEmojiToggle = rootView.findViewById<TextView>(R.id.btn_back_to_keyboard)
        btnEmojiToggle?.setOnClickListener {
            emojiManager.toggleEmojiView { emoji ->
                currentInputConnection?.commitText(emoji, 1)
                vibrationManager.vibrateKeyClick()
            }
            vibrationManager.vibrateKeyClick()
        }
    }

    // Key Press Handlers
    fun onKeyInput(code: Int) {
        vibrationManager.vibrateKeyClick()
        val ic = currentInputConnection ?: return

        when (code) {
            -1 -> { // Backspace
                if (currentComposingText.isNotEmpty()) {
                    currentComposingText = currentComposingText.dropLast(1)
                    ic.setComposingText(SinhalaTransliterationEngine.transliterate(currentComposingText), 1)
                } else {
                    ic.deleteSurroundingText(1, 0)
                }
            }
            32 -> { // Space
                if (currentComposingText.isNotEmpty()) {
                    val finalWord = SinhalaTransliterationEngine.transliterate(currentComposingText)
                    ic.commitText("$finalWord ", 1)
                    userHistoryManager.saveWord(finalWord)
                    currentComposingText = ""
                } else {
                    ic.commitText(" ", 1)
                }
            }
            10 -> { // Enter
                if (currentComposingText.isNotEmpty()) {
                    val finalWord = SinhalaTransliterationEngine.transliterate(currentComposingText)
                    ic.commitText(finalWord, 1)
                    userHistoryManager.saveWord(finalWord)
                    currentComposingText = ""
                }
                ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
            }
            else -> { // Character Input
                val char = code.toChar()
                currentComposingText += char
                val transliterated = SinhalaTransliterationEngine.transliterate(currentComposingText)
                ic.setComposingText(transliterated, 1)
            }
        }
    }
}
