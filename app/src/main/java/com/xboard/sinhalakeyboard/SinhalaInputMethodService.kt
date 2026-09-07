package com.xboard.sinhalakeyboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class SinhalaInputMethodService : InputMethodService() {

    private var currentComposingText = StringBuilder()
    private lateinit var keyboardToolbar: LinearLayout
    private lateinit var candidateContainer: LinearLayout
    private lateinit var keyboardRowsContainer: LinearLayout
    private var isSinhalaMode = true
    private var isShifted = false
    private var isNumericMode = false
    private var isEmojiMode = false
    private var isSoundMuted = false
    private var fontStyleIndex = 0 // 0: Normal, 1: Bold, 2: Italic, 3: Script, 4: Circled
    private val fontStyleLabels = listOf("Aa", "𝗕", "𝘐", "𝓐", "Ⓐ")

    private fun dp(value: Float): Int = (value * resources.displayMetrics.density + 0.5f).toInt()

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.keyboard_view, null)
        keyboardToolbar = root.findViewById(R.id.keyboardToolbar)
        candidateContainer = root.findViewById(R.id.candidateContainer)
        keyboardRowsContainer = root.findViewById(R.id.keyboardRowsContainer)

        setupToolbar()
        buildKeyboard()
        return root
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentComposingText.clear()
        if (::candidateContainer.isInitialized) {
            candidateContainer.removeAllViews()
        }
        isShifted = false
        if (::keyboardRowsContainer.isInitialized) {
            buildKeyboard()
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        commitComposing()
    }

    private fun setupToolbar() {
        keyboardToolbar.removeAllViews()

        // 1. Language Toggle Button (සිං / EN)
        addToolbarCircleButton(if (isSinhalaMode) "සිං" else "EN", isSinhalaMode) {
            commitComposing()
            isSinhalaMode = !isSinhalaMode
            isEmojiMode = false
            setupToolbar()
            buildKeyboard()
        }

        // 2. Emoji Button (😄)
        addToolbarCircleButton("😄", isEmojiMode) {
            commitComposing()
            isEmojiMode = !isEmojiMode
            setupToolbar()
            buildKeyboard()
        }

        // 3. Fancy Fonts Button (Aa / 𝗕 / 𝘐 / 𝓐 / Ⓐ)
        addToolbarCircleButton(fontStyleLabels[fontStyleIndex], fontStyleIndex > 0) {
            fontStyleIndex = (fontStyleIndex + 1) % fontStyleLabels.size
            setupToolbar()
        }

        // 4. Clipboard Button (📋)
        addToolbarCircleButton("📋", false) {
            showClipboardOptions()
        }

        // 5. Text Designer / Bios (✨)
        addToolbarCircleButton("✨", false) {
            showTextDesignerOptions()
        }

        // 6. Dictionary & Quick Phrases (📖)
        addToolbarCircleButton("📖", false) {
            showDictionaryOptions()
        }

        // 7. Sound / Mute Toggle (🔊 / 🔇)
        addToolbarCircleButton(if (isSoundMuted) "🔇" else "🔊", !isSoundMuted) {
            isSoundMuted = !isSoundMuted
            setupToolbar()
            vibratePhone()
        }
    }

    private fun addToolbarCircleButton(text: String, isActive: Boolean, onClick: () -> Unit) {
        val btn = TextView(this).apply {
            this.text = text
            textSize = 12.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            gravity = Gravity.CENTER
            setTextColor(if (isActive) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            includeFontPadding = false

            val bg = GradientDrawable().apply {
                cornerRadius = dp(16f).toFloat()
                if (isActive) {
                    setColor(0xFF07F57E.toInt())
                } else {
                    setColor(0xFF18181C.toInt())
                    setStroke(dp(1f), 0xFF2A2A30.toInt())
                }
            }
            background = bg

            val lp = LinearLayout.LayoutParams(dp(36f), dp(30f)).apply {
                setMargins(dp(2.5f), 0, dp(2.5f), 0)
            }
            layoutParams = lp

            isClickable = true
            isFocusable = true
            setOnClickListener {
                vibratePhone()
                onClick()
            }
        }
        keyboardToolbar.addView(btn)
    }

    private fun showClipboardOptions() {
        candidateContainer.removeAllViews()
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipItem = cm?.primaryClip?.getItemAt(0)?.text?.toString()

        val items = mutableListOf<String>()
        if (!clipItem.isNullOrBlank()) {
            items.add("📋 " + clipItem.take(20))
        }
        items.addAll(listOf("ස්තූතියි!", "ආයුබෝවන්!", "Good Morning!", "ඔව්", "නැහැ", "කරුණාකර"))

        for (item in items) {
            val actualText = if (item.startsWith("📋 ") && !clipItem.isNullOrBlank()) clipItem else item
            addQuickCandidateChip(item) {
                currentInputConnection?.commitText(actualText + " ", 1)
                candidateContainer.removeAllViews()
            }
        }
    }

    private fun showTextDesignerOptions() {
        candidateContainer.removeAllViews()
        val designs = listOf(
            "꧁༺ 𝓧 𝓑𝓸𝓪𝓻𝓭 ༻꧂",
            "─── ⋆⋅☆⋅⋆ ───",
            "★·.·´¯'·.·★",
            "✿◕ ‿ ◕✿",
            "༺═─ ─═༻",
            "•´¯'•. [ 𝑿 𝑩𝒐𝒂𝒓𝒅 ] .•´¯'•",
            "★彡 𝐒𝐢𝐧𝐡𝐚𝐥𝐚 彡★"
        )
        for (design in designs) {
            addQuickCandidateChip(design) {
                currentInputConnection?.commitText(design + " ", 1)
                candidateContainer.removeAllViews()
            }
        }
    }

    private fun showDictionaryOptions() {
        candidateContainer.removeAllViews()
        val phrases = listOf(
            "ආයුබෝවන්",
            "ස්තූතියි",
            "සුබ දවසක්",
            "කොහොමද",
            "කරුණාකර",
            "බොහොම ස්තූතියි",
            "සුබ උදෑසනක්",
            "සුබ රාත්‍රියක්",
            "වරදක් නෑ",
            "හමුවෙමු"
        )
        for (phrase in phrases) {
            addQuickCandidateChip(phrase) {
                currentInputConnection?.commitText(phrase + " ", 1)
                candidateContainer.removeAllViews()
            }
        }
    }

    private fun addQuickCandidateChip(text: String, onClick: () -> Unit) {
        val tv = TextView(this).apply {
            this.text = text
            textSize = 13.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(0xFF07F57E.toInt())
            setPadding(dp(12f), dp(5f), dp(12f), dp(5f))
            val pillBg = GradientDrawable().apply {
                cornerRadius = dp(14f).toFloat()
                setColor(0xFF141414.toInt())
                setStroke(dp(1f), 0xFF2A2A2A.toInt())
            }
            background = pillBg
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dp(3f), dp(3f), dp(3f), dp(3f))
            }
            layoutParams = lp
            isClickable = true
            setOnClickListener {
                vibratePhone()
                onClick()
            }
        }
        candidateContainer.addView(tv)
    }

    private fun applyFontStyle(c: Char): String {
        return when (fontStyleIndex) {
            1 -> { // Bold
                when (c) {
                    in 'a'..'z' -> String(Character.toChars(0x1D41A + (c - 'a')))
                    in 'A'..'Z' -> String(Character.toChars(0x1D400 + (c - 'A')))
                    in '0'..'9' -> String(Character.toChars(0x1D7CE + (c - '0')))
                    else -> c.toString()
                }
            }
            2 -> { // Italic
                when (c) {
                    in 'a'..'z' -> String(Character.toChars(0x1D44E + (c - 'a')))
                    in 'A'..'Z' -> String(Character.toChars(0x1D434 + (c - 'A')))
                    else -> c.toString()
                }
            }
            3 -> { // Script
                when (c) {
                    in 'a'..'z' -> String(Character.toChars(0x1D4EA + (c - 'a')))
                    in 'A'..'Z' -> String(Character.toChars(0x1D4D0 + (c - 'A')))
                    else -> c.toString()
                }
            }
            4 -> { // Circled
                when (c) {
                    in 'a'..'z' -> String(Character.toChars(0x24D0 + (c - 'a')))
                    in 'A'..'Z' -> String(Character.toChars(0x24B6 + (c - 'A')))
                    in '1'..'9' -> String(Character.toChars(0x2460 + (c - '1')))
                    '0' -> "⓪"
                    else -> c.toString()
                }
            }
            else -> c.toString()
        }
    }

    private fun getSinhalaHint(key: String): String {
        return when (key.lowercase()) {
            "q" -> "ඤ"
            "w" -> "උ"
            "e" -> "එ"
            "r" -> "ර"
            "t" -> "ත"
            "y" -> "ය"
            "u" -> "උ"
            "i" -> "ඉ"
            "o" -> "ඔ"
            "p" -> "ප"
            "a" -> "අ"
            "s" -> "ස"
            "d" -> "ද"
            "f" -> "ෆ"
            "g" -> "ග"
            "h" -> "හ"
            "j" -> "ජ"
            "k" -> "ක"
            "l" -> "ල"
            "z" -> "ඥ"
            "x" -> "ඞ"
            "c" -> "ච"
            "v" -> "ව"
            "b" -> "බ"
            "n" -> "න"
            "m" -> "ම"
            else -> ""
        }
    }

    private fun buildKeyboard() {
        keyboardRowsContainer.removeAllViews()

        val rows = if (isEmojiMode) {
            listOf(
                listOf("😀", "😂", "🤣", "😍", "🥰", "😘", "😭", "🥺", "😎", "🔥"),
                listOf("👍", "👎", "🙏", "👏", "❤️", "💖", "✨", "🎉", "💯", "🇱🇰"),
                listOf("🌸", "☕", "🍕", "🚗", "✈️", "⭐", "💡", "⚡", "DEL"),
                listOf("ABC", "123", "SPACE", "ENTER")
            )
        } else if (isNumericMode) {
            listOf(
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/"),
                listOf("*", "\"", "'", ":", ";", "!", "?", ",", ".", "DEL"),
                listOf("ABC", "LANG", "SPACE", "ENTER")
            )
        } else {
            listOf(
                listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
                listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
                listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "DEL"),
                listOf("123", "LANG", "SPACE", "ENTER")
            )
        }

        for (rowKeys in rows) {
            val rowLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            for (key in rowKeys) {
                val weight = when (key) {
                    "SPACE" -> if (isEmojiMode) 3.5f else 4.2f
                    "ENTER" -> 1.4f
                    "SHIFT", "DEL" -> 1.35f
                    "123", "ABC", "LANG" -> 1.25f
                    else -> 1.0f
                }

                val keyContainer = FrameLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, dp(54f), weight).apply {
                        setMargins(dp(2f), dp(3f), dp(2f), dp(3f))
                    }

                    // Background rounded drawable
                    val normalDrawable = GradientDrawable().apply {
                        cornerRadius = dp(8f).toFloat()
                        when (key) {
                            "ENTER" -> {
                                setColor(0xFF07F57E.toInt())
                            }
                            "SPACE" -> {
                                setColor(0xFF141414.toInt())
                                setStroke(dp(1f), 0xFF2A2A2A.toInt())
                            }
                            "SHIFT" -> {
                                setColor(if (isShifted) 0xFF07F57E.toInt() else 0xFF1C1C20.toInt())
                                setStroke(dp(1f), 0xFF2E2E32.toInt())
                            }
                            "DEL", "LANG", "123", "ABC" -> {
                                setColor(0xFF1C1C20.toInt())
                                setStroke(dp(1f), 0xFF2E2E32.toInt())
                            }
                            else -> {
                                setColor(0xFF121212.toInt())
                                setStroke(dp(1f), 0xFF222222.toInt())
                            }
                        }
                    }
                    background = normalDrawable

                    val displayText = when (key) {
                        "LANG" -> if (isSinhalaMode) "සිං" else "EN"
                        "SHIFT" -> if (isShifted) "▲" else "⇧"
                        "DEL" -> "⌫"
                        "SPACE" -> if (isEmojiMode) "Space" else if (isSinhalaMode) "X Board (සිංහල)" else "X Board"
                        "ENTER" -> "↵"
                        "ABC" -> "ABC"
                        "123" -> "?123"
                        else -> if (isShifted) key.uppercase() else key.lowercase()
                    }

                    // Main Text View - Unclipped & Full-width!
                    val mainTv = TextView(this@SinhalaInputMethodService).apply {
                        text = displayText
                        textSize = when (key) {
                            "SPACE" -> 13f
                            "LANG", "123", "ABC" -> 13.5f
                            "SHIFT", "DEL" -> 18f
                            "ENTER" -> 20f
                            else -> if (isEmojiMode) 22f else 19f
                        }
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        setTextColor(when (key) {
                            "ENTER" -> 0xFF000000.toInt()
                            "SPACE" -> 0xFF07F57E.toInt()
                            "SHIFT" -> if (isShifted) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                            else -> 0xFFFFFFFF.toInt()
                        })
                        gravity = Gravity.CENTER
                        includeFontPadding = false
                        setPadding(0, 0, 0, 0)
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }
                    addView(mainTv)

                    // Secondary Sinhala hint glyph (top right) in Singlish mode
                    if (!isEmojiMode && !isNumericMode && isSinhalaMode && key.length == 1 && key[0].lowercaseChar() in 'a'..'z') {
                        val hint = getSinhalaHint(key)
                        if (hint.isNotEmpty()) {
                            val hintTv = TextView(this@SinhalaInputMethodService).apply {
                                text = hint
                                textSize = 9.5f
                                setTextColor(0xFF07F57E.toInt())
                                includeFontPadding = false
                                setPadding(0, 0, 0, 0)
                                layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    gravity = Gravity.TOP or Gravity.END
                                    setMargins(0, dp(2f), dp(4f), 0)
                                }
                            }
                            addView(hintTv)
                        }
                    }

                    // Interactive touch listener with scale and color feedback
                    isClickable = true
                    isFocusable = true
                    setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                v.scaleX = 0.93f
                                v.scaleY = 0.93f
                                val pressedDrawable = GradientDrawable().apply {
                                    cornerRadius = dp(8f).toFloat()
                                    setColor(if (key == "ENTER") 0xFF05BE5F.toInt() else 0xFF07F57E.toInt())
                                }
                                v.background = pressedDrawable
                                mainTv.setTextColor(0xFF000000.toInt())
                                vibratePhone()
                            }
                            MotionEvent.ACTION_UP -> {
                                v.scaleX = 1.0f
                                v.scaleY = 1.0f
                                v.background = normalDrawable
                                mainTv.setTextColor(when (key) {
                                    "ENTER" -> 0xFF000000.toInt()
                                    "SPACE" -> 0xFF07F57E.toInt()
                                    "SHIFT" -> if (isShifted) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                                    else -> 0xFFFFFFFF.toInt()
                                })
                                handleKeyPress(key)
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                v.scaleX = 1.0f
                                v.scaleY = 1.0f
                                v.background = normalDrawable
                                mainTv.setTextColor(when (key) {
                                    "ENTER" -> 0xFF000000.toInt()
                                    "SPACE" -> 0xFF07F57E.toInt()
                                    "SHIFT" -> if (isShifted) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                                    else -> 0xFFFFFFFF.toInt()
                                })
                            }
                        }
                        true
                    }
                }
                rowLayout.addView(keyContainer)
            }
            keyboardRowsContainer.addView(rowLayout)
        }
    }

    private fun handleKeyPress(key: String) {
        val ic = currentInputConnection ?: return

        when (key) {
            "DEL" -> {
                if (currentComposingText.isNotEmpty()) {
                    currentComposingText.deleteCharAt(currentComposingText.length - 1)
                    updateCandidates()
                } else {
                    ic.deleteSurroundingText(1, 0)
                }
            }
            "SPACE" -> {
                commitComposing()
                ic.commitText(" ", 1)
            }
            "ENTER" -> {
                commitComposing()
                ic.performEditorAction(EditorInfo.IME_ACTION_DONE)
            }
            "SHIFT" -> {
                isShifted = !isShifted
                buildKeyboard()
            }
            "LANG" -> {
                commitComposing()
                isSinhalaMode = !isSinhalaMode
                isEmojiMode = false
                setupToolbar()
                buildKeyboard()
            }
            "123" -> {
                commitComposing()
                isNumericMode = true
                isEmojiMode = false
                buildKeyboard()
            }
            "ABC" -> {
                isNumericMode = false
                isEmojiMode = false
                setupToolbar()
                buildKeyboard()
            }
            else -> {
                if (isEmojiMode) {
                    ic.commitText(key, 1)
                } else if (isNumericMode) {
                    val styled = if (key.length == 1 && fontStyleIndex > 0) applyFontStyle(key[0]) else key
                    ic.commitText(styled, 1)
                } else {
                    val charToAppend = if (isShifted) key.uppercase() else key.lowercase()
                    if (isSinhalaMode) {
                        currentComposingText.append(charToAppend)
                        updateCandidates()
                    } else {
                        val styled = if (charToAppend.length == 1 && fontStyleIndex > 0) applyFontStyle(charToAppend[0]) else charToAppend
                        ic.commitText(styled, 1)
                    }
                }
            }
        }
    }

    private fun updateCandidates() {
        candidateContainer.removeAllViews()
        if (currentComposingText.isEmpty()) return

        val phonetic = currentComposingText.toString()
        val sinhalaWord = SinhalaTransliterationEngine.transliterate(phonetic)

        val suggestions = listOf(
            sinhalaWord,
            sinhalaWord + "ක්",
            sinhalaWord + "ට",
            sinhalaWord + "ගේ",
            phonetic
        )

        for (word in suggestions) {
            val tv = TextView(this).apply {
                text = word
                textSize = 15f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                setTextColor(0xFF07F57E.toInt())
                setPadding(dp(14f), dp(6f), dp(14f), dp(6f))
                val pillBg = GradientDrawable().apply {
                    cornerRadius = dp(14f).toFloat()
                    setColor(0xFF141414.toInt())
                    setStroke(dp(1f), 0xFF262626.toInt())
                }
                background = pillBg
                val lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dp(4f), dp(4f), dp(4f), dp(4f))
                }
                layoutParams = lp
                isClickable = true
                setOnClickListener {
                    currentInputConnection?.commitText(word + " ", 1)
                    currentComposingText.clear()
                    updateCandidates()
                }
            }
            candidateContainer.addView(tv)
        }
    }

    private fun commitComposing() {
        if (currentComposingText.isNotEmpty()) {
            val sinhala = SinhalaTransliterationEngine.transliterate(currentComposingText.toString())
            currentInputConnection?.commitText(sinhala, 1)
            currentComposingText.clear()
            candidateContainer.removeAllViews()
        }
    }

    private fun vibratePhone() {
        if (isSoundMuted) return
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(15)
        }
    }
}
