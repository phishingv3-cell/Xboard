package com.xboard.sinhalakeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo

class SinhalaInputMethodService : InputMethodService() {

    private lateinit var liveThemeManager: LiveThemeManager
    private lateinit var vibrationManager: VibrationManager
    private lateinit var emojiManager: EmojiManager
    private lateinit var userHistoryManager: UserHistoryManager

    private var currentComposingText = ""

    override fun onCreate() {
        super.onCreate()
        liveThemeManager = LiveThemeManager(this)
        vibrationManager = VibrationManager(this)
        emojiManager = EmojiManager(this)
        userHistoryManager = UserHistoryManager(this)
    }

    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)

        liveThemeManager.init(keyboardView)
        liveThemeManager.applyCurrentTheme()

        emojiManager.init(keyboardView)

        setupToolbarButtons(keyboardView)

        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        liveThemeManager.applyCurrentTheme()
        currentComposingText = ""
    }

    private fun setupToolbarButtons(rootView: View) {
        // keyboard_view.xml හි ඇති btn_back_to_keyboard ID එක මෙහි යොදා ඇත
        val btnEmojiToggle = rootView.findViewById<View>(R.id.btn_back_to_keyboard)
        btnEmojiToggle?.setOnClickListener {
            emojiManager.toggleEmojiView { emoji ->
                currentInputConnection?.commitText(emoji, 1)
                vibrationManager.vibrateKeyClick()
            }
            vibrationManager.vibrateKeyClick()
        }
    }

    fun onKeyInput(code: Int) {
        vibrationManager.vibrateKeyClick()
        val ic = currentInputConnection ?: return

        when (code) {
            -1 -> { 
                if (currentComposingText.isNotEmpty()) {
                    currentComposingText = currentComposingText.dropLast(1)
                    ic.setComposingText(SinhalaTransliterationEngine.transliterate(currentComposingText), 1)
                } else {
                    ic.deleteSurroundingText(1, 0)
                }
            }
            32 -> { 
                if (currentComposingText.isNotEmpty()) {
                    val finalWord = SinhalaTransliterationEngine.transliterate(currentComposingText)
                    ic.commitText("$finalWord ", 1)
                    userHistoryManager.saveWord(finalWord)
                    currentComposingText = ""
                } else {
                    ic.commitText(" ", 1)
                }
            }
            10 -> { 
                if (currentComposingText.isNotEmpty()) {
                    val finalWord = SinhalaTransliterationEngine.transliterate(currentComposingText)
                    ic.commitText(finalWord, 1)
                    userHistoryManager.saveWord(finalWord)
                    currentComposingText = ""
                }
                ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
            }
            else -> { 
                val char = code.toChar()
                currentComposingText += char
                val transliterated = SinhalaTransliterationEngine.transliterate(currentComposingText)
                ic.setComposingText(transliterated, 1)
            }
        }
    }
}
