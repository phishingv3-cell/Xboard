package com.xboard.sinhalakeyboard

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class SinhalaInputMethodService : InputMethodService() {

    private var currentComposingText = StringBuilder()
    private lateinit var keyboardToolbar: LinearLayout
    private lateinit var candidateContainer: LinearLayout
    private lateinit var keyboardRowsContainer: LinearLayout

    private var isSinhalaMode = true
    private var isShifted = false
    private var isNumericMode = false
    private var isSoundMuted = false
    private var selectedDecoratorIndex = 0

    // Font Transformer Function Reference
    private var selectedFontTransform: (String) -> String = { it }

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

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentComposingText.clear()
        if (::candidateContainer.isInitialized) candidateContainer.removeAllViews()
        isShifted = false
        if (::keyboardRowsContainer.isInitialized) buildKeyboard()
    }

    // ==========================================
    // 1. TOOLBAR SETUP (8 Tool Buttons Order)
    // ==========================================
    private fun setupToolbar() {
        keyboardToolbar.removeAllViews()

        addToolbarCircleButton(if (isSinhalaMode) "සිං" else "EN", isSinhalaMode) {
            commitComposing()
            isSinhalaMode = !isSinhalaMode
            setupToolbar()
            buildKeyboard()
        }

        addToolbarCircleButton("😀", false) {
            commitComposing()
            showFullEmojiPanel()
        }

        addToolbarCircleButton("Aa", false) {
            showFontPickerPanel()
        }

        addToolbarCircleButton("📋", false) {
            showFullClipboardPanel()
        }

        addToolbarCircleButton("🪄", false) {
            showDecoratorPanel()
        }

        addToolbarCircleButton("📖", false) {
            showGuidePanel()
        }

        addToolbarCircleButton("🎨", false) {
            // Theme Switcher Action
        }

        addToolbarCircleButton(if (isSoundMuted) "🔇" else "🔊", !isSoundMuted) {
            isSoundMuted = !isSoundMuted
            setupToolbar()
            vibratePhone()
        }
    }

    private fun addToolbarCircleButton(text: String, isActive: Boolean, onClick: () -> Unit) {
        val btn = TextView(this).apply {
            this.text = text
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            gravity = Gravity.CENTER
            setTextColor(if (isActive) 0xFF000000.toInt() else 0xFF07F57E.toInt())

            background = GradientDrawable().apply {
                cornerRadius = dp(18f).toFloat()
                if (isActive) {
                    setColor(0xFF07F57E.toInt())
                } else {
                    setColor(0xFF101014.toInt())
                    setStroke(dp(1.2f), 0xFF07F57E.toInt())
                }
            }
            layoutParams = LinearLayout.LayoutParams(dp(42f), dp(38f)).apply {
                setMargins(dp(3f), 0, dp(3f), 0)
            }
            isClickable = true
            setOnClickListener {
                vibratePhone()
                onClick()
            }
        }
        keyboardToolbar.addView(btn)
    }

    // ==========================================
    // 2. MAIN KEYBOARD BUILDER
    // ==========================================
    private fun buildKeyboard() {
        keyboardRowsContainer.removeAllViews()

        val rows = if (isNumericMode) {
            listOf(
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/"),
                listOf("*", "\"", "'", ":", ";", "!", "?", ",", ".", "DEL"),
                listOf("ABC", "LANG", ",", "SPACE", ".", "ENTER")
            )
        } else {
            listOf(
                listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
                listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
                listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "DEL"),
                listOf("123", "LANG", ",", "SPACE", ".", "ENTER")
            )
        }

        for (rowKeys in rows) {
            val rowLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            for (key in rowKeys) {
                val weight = when (key) {
                    "SPACE" -> 3.8f
                    "ENTER" -> 1.3f
                    "SHIFT", "DEL" -> 1.25f
                    "123", "ABC" -> 1.1f
                    "LANG", ",", "." -> 0.9f
                    else -> 1.0f
                }

                val keyContainer = FrameLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, dp(52f), weight).apply {
                        setMargins(dp(2f), dp(3f), dp(2f), dp(3f))
                    }

                    background = GradientDrawable().apply {
                        cornerRadius = dp(10f).toFloat()
                        when (key) {
                            "ENTER" -> setColor(0xFF07F57E.toInt())
                            "SPACE" -> {
                                setColor(0xFF0D0D10.toInt())
                                setStroke(dp(1.2f), 0xFF07F57E.toInt())
                            }
                            else -> {
                                setColor(0xFF0A0A0D.toInt())
                                setStroke(dp(1.2f), 0xFF07F57E.toInt())
                            }
                        }
                    }

                    val displayText = when (key) {
                        "LANG" -> "🌐"
                        "SHIFT" -> if (isShifted) "⇪" else "⇧"
                        "DEL" -> "⌫"
                        "SPACE" -> if (isSinhalaMode) "X Board" else "Space"
                        "ENTER" -> "↵"
                        "ABC" -> "ABC"
                        "123" -> "?123"
                        else -> if (isShifted) key.uppercase() else key.lowercase()
                    }

                    val mainTv = TextView(this@SinhalaInputMethodService).apply {
                        text = displayText
                        textSize = when (key) {
                            "SPACE" -> 13f
                            "123", "ABC" -> 12f
                            "SHIFT", "DEL", "ENTER" -> 18f
                            else -> 18f
                        }
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        setTextColor(if (key == "ENTER") 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                        gravity = Gravity.CENTER
                        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    }
                    addView(mainTv)

                    // Sub-hint Sinhala Character Glyphs
                    if (!isNumericMode && isSinhalaMode && key.length == 1 && key[0].lowercaseChar() in 'a'..'z') {
                        val hint = getSinhalaHint(key)
                        if (hint.isNotEmpty()) {
                            val hintTv = TextView(this@SinhalaInputMethodService).apply {
                                text = hint
                                textSize = 9.5f
                                setTextColor(0xFF07F57E.toInt())
                                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                                    gravity = Gravity.TOP or Gravity.END
                                    setMargins(0, dp(2f), dp(4f), 0)
                                }
                            }
                            addView(hintTv)
                        }
                    }

                    isClickable = true
                    setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                v.scaleX = 0.94f
                                v.scaleY = 0.94f
                                vibratePhone()
                            }
                            MotionEvent.ACTION_UP -> {
                                v.scaleX = 1.0f
                                v.scaleY = 1.0f
                                handleKeyPress(key)
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                v.scaleX = 1.0f
                                v.scaleY = 1.0f
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

    // ==========================================
    // 3. KEY PRESS & NEW LINE LOGIC
    // ==========================================
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
                // Force Enter to always insert a Newline
                ic.commitText("\n", 1)
            }
            "SHIFT" -> {
                isShifted = !isShifted
                buildKeyboard()
            }
            "LANG" -> {
                commitComposing()
                isSinhalaMode = !isSinhalaMode
                setupToolbar()
                buildKeyboard()
            }
            "123" -> {
                commitComposing()
                isNumericMode = true
                buildKeyboard()
            }
            "ABC" -> {
                isNumericMode = false
                buildKeyboard()
            }
            else -> {
                val charToAppend = if (isShifted) key.uppercase() else key.lowercase()
                if (isSinhalaMode && charToAppend.length == 1 && charToAppend[0] in 'a'..'z') {
                    currentComposingText.append(charToAppend)
                    updateCandidates()
                } else {
                    val styledText = selectedFontTransform(charToAppend)
                    ic.commitText(styledText, 1)
                }
            }
        }
    }

    // ==========================================
    // 4. EMOJI PANEL SYSTEM
    // ==========================================
    private fun showFullEmojiPanel() {
        keyboardRowsContainer.removeAllViews()

        val emojiCategories = mapOf(
            "😀" to listOf("😀","😃","😄","😁","😆","😅","🤣","😂","🙂","🙃","🫠","😉","😊","😇","🥰","😍","🤩","😘","😗","😚","😋","😛","😜","🤪","😝","🤑","🤗","🫣","🤭","🫡","🤔","🤐","🤨","😐","😑","😶","😏","😒","🙄","😬","🤥","😌","😔","😪","🤤","😴","😷","🤒","🤕","🤢","🤮","🤧","🥵","🥶","🥴","😵","🤯","🤠","🥳","😎","🤓","🧐","😕","😟","🙁","😮","😲","😳","🥺","🥹","😦","😧","😤","😥","😢","😭","😱","😖","😣","😞","😓","😩","😫","🥱","😡","😠","🤬","😈","👿","💀","💩","🤡","👹","👺","👻","👽","👾","🤖","😺","😸","😹","😻"),
            "🐶" to listOf("🙈","🙉","🙊","💥","💫","💦","💨","🐵","🐒","🦍","🦧","🐶","🐕","🦮","🐩","🐺","🦊","🦝","🐱","🐈","🦁","🐯","🐅","🐆","🐴","🫏","🐎","🦄","🦓"," deer","🐮","🐂","🐃","🐄","🐷","🐖","🐗","👃","🐽","🐏","🐑","🐐","🐪","🐫","🦙","🦒","🐘","🦣","🦏","🦛","🐭","🐁","🐀","🐹","🐰","🐇","🐿️","🦫","🦔","🦇","🐻","🐨","🐼","🦥","🦦","🦨","🦘","🦡","🐾","🦃","🐔","🐓","🐣","🐤","🐥","🐦","🐧","🕊️","🦅","🦆","🦢","🦉","🦩","🦚","🦜","🐸","🐊","🐢","🦎","🐍","🐲","🐉","🐳","🐋","🐬","🦭","🐟","🐠","🐡","🦈","🐙","🐚","🦀","🦞","🦐","🦑","🦋","🐛","🐜","🐝","🪲","🐞","🦗","🕷️","🦂"),
            "🍕" to listOf("🍇","🍈","🍉","🍊","🍋","🍌","🍍","🥭","🍎","🍏","🍐","🍑","🍒","🍓","🫐","🥝","🍅","🫒","🥥","🥑","🍆","🥔","🥕","🌽","🌶️","🫑","🥒","🥬","🥦","🧄","🧅","🥜","🌰","🍞","🥐","🥖","🫓","🥨","🥯","🥞","🧇","🧀","🍖","🍗","🥩","🥓","🍔","🍟","🍕","🌭","🥪","🌮","🌯","🥙","🧆","🥚","🍳","🥘","🍲","🥣","🥗","🍿","バター","🧂","🥫","🍱","🍘","🍙","🍚","🍛","🍜","🍝","🍠","🍢","🍣","🍤","🍥","🍡","🥟","🥠","🥡","🍦","🍧","🍨","🍩","🍪","🎂","🍰","🧁","🥧","🍫","🍬","🍭","🍮","🍯","🍼","🥛","☕","🫖","🍵","🍶","🍾","🍷","🍸","🍹","🍺","🍻","🥂","🥃","🥤","🧋","🧃","🧊"),
            "⚽" to listOf("⚽","🏀","🏈","⚾","🥎","🎾","🏐","🏉","🥏","🎱","🪀","🏓","🏸","🏒","🏑","🥍","🏏","🪃","🥅","⛳","kite","🏹","🎣","🪝","🎽","🎿","🛷","🥌","🎯","🪂","🏋️","🤼","🤸","⛹️","🤺","🤾","🏌️","🏇","🧘","🏄","🏊","🤽","🚣","🧗","🚵","🚴","🏆","🥇","🥈","🥉","🏅","🎖️","🎗️","🎫","🎟️","🎪","🤹","🎭","🎨","🎬","🎤","🎧","🎼","🎵","🎶","🥁","🎷","🎺","🎸","🪕","🎻","🎲","♟️","🎯"," bowling","🎮","🎰")
        )

        val panelLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(220f))
            setBackgroundColor(0xFF0D0D0F.toInt())
        }

        val categoryScrollView = HorizontalScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(40f))
            setBackgroundColor(0xFF141418.toInt())
        }
        val categoryContainer = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }

        val emojiScrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f)
        }
        val gridContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        var currentCat = "😀"
        fun loadCategory(cat: String) {
            gridContainer.removeAllViews()
            val emojiList = emojiCategories[cat] ?: emptyList()
            val columns = 8
            var currentRow: LinearLayout? = null

            for ((index, emoji) in emojiList.withIndex()) {
                if (index % columns == 0) {
                    currentRow = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    }
                    gridContainer.addView(currentRow)
                }

                val emojiTv = TextView(this).apply {
                    text = emoji
                    textSize = 22f
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, dp(42f), 1.0f)
                    setOnClickListener {
                        vibratePhone()
                        currentInputConnection?.commitText(emoji, 1)
                    }
                }
                currentRow?.addView(emojiTv)
            }
        }

        for ((catIcon, _) in emojiCategories) {
            val catBtn = TextView(this).apply {
                text = catIcon
                textSize = 18f
                setPadding(dp(12f), dp(6f), dp(12f), dp(6f))
                setOnClickListener {
                    vibratePhone()
                    loadCategory(catIcon)
                }
            }
            categoryContainer.addView(catBtn)
        }
        categoryScrollView.addView(categoryContainer)
        panelLayout.addView(categoryScrollView)

        loadCategory(currentCat)
        emojiScrollView.addView(gridContainer)
        panelLayout.addView(emojiScrollView)

        // Bottom Controls
        val bottomNav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(8f), dp(4f), dp(8f), dp(4f))
            setBackgroundColor(0xFF141418.toInt())
        }
        val backBtn = Button(this).apply {
            text = "⬅ Back to Keyboard"
            setTextColor(0xFF000000.toInt())
            backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF07F57E.toInt())
            layoutParams = LinearLayout.LayoutParams(0, dp(38f), 1.0f)
            setOnClickListener { buildKeyboard() }
        }
        bottomNav.addView(backBtn)
        panelLayout.addView(bottomNav)

        keyboardRowsContainer.addView(panelLayout)
    }

    // ==========================================
    // 5. SUB-PANELS (Clipboard, Fonts, Decorator, Guide)
    // ==========================================
    private fun showFullClipboardPanel() {
        keyboardRowsContainer.removeAllViews()

        val scroll = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(210f)) }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10f), dp(10f), dp(10f), dp(10f))
        }

        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipItem = cm?.primaryClip?.getItemAt(0)?.text?.toString() ?: ""

        val history = listOfNotNull(
            if (clipItem.isNotEmpty()) clipItem else null,
            "ඔයාට කොහොමද?",
            "Good Morning!",
            "Thank you so much!",
            "X Board Sinhala Keyboard"
        )

        for (text in history) {
            val itemTv = TextView(this).apply {
                this.text = text
                textSize = 14f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(dp(12f), dp(10f), dp(12f), dp(10f))
                background = GradientDrawable().apply {
                    setColor(0xFF1E1E24.toInt())
                    cornerRadius = dp(8f).toFloat()
                }
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 0, 0, dp(6f))
                }
                setOnClickListener {
                    currentInputConnection?.commitText(text, 1)
                    buildKeyboard()
                }
            }
            container.addView(itemTv)
        }

        val backBtn = Button(this).apply {
            text = "⬅ Back to Keyboard"
            setTextColor(0xFF000000.toInt())
            backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF07F57E.toInt())
            setOnClickListener { buildKeyboard() }
        }
        container.addView(backBtn)

        scroll.addView(container)
        keyboardRowsContainer.addView(scroll)
    }

    private fun showFontPickerPanel() {
        keyboardRowsContainer.removeAllViews()

        val scroll = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(210f)) }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10f), dp(10f), dp(10f), dp(10f))
        }

        val sampleText = "X Board Style"
        val fontStyles = listOf(
            "Normal" to { s: String -> s },
            "Bold" to { s: String -> applyUnicodeOffset(s, 0x1D400, 0x1D41A) },
            "Italic" to { s: String -> applyUnicodeOffset(s, 0x1D434, 0x1D44E) },
            "Monospace" to { s: String -> applyUnicodeOffset(s, 0x1D670, 0x1D68A) }
        )

        for ((name, transform) in fontStyles) {
            val tv = TextView(this).apply {
                text = "$name: ${transform(sampleText)}"
                textSize = 15f
                setTextColor(0xFF07F57E.toInt())
                setPadding(dp(10f), dp(10f), dp(10f), dp(10f))
                setOnClickListener {
                    selectedFontTransform = transform
                    buildKeyboard()
                }
            }
            container.addView(tv)
        }

        val backBtn = Button(this).apply {
            text = "⬅ Back to Keyboard"
            setTextColor(0xFF000000.toInt())
            backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF07F57E.toInt())
            setOnClickListener { buildKeyboard() }
        }
        container.addView(backBtn)

        scroll.addView(container)
        keyboardRowsContainer.addView(scroll)
    }

    private fun showDecoratorPanel() {
        keyboardRowsContainer.removeAllViews()

        val scroll = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(210f)) }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10f), dp(10f), dp(10f), dp(10f))
        }

        val decorators = listOf(
            "✧･ﾟ: * [Text] *:･ﾟ✧",
            "꧁༺ [Text] ༻꧂",
            "★彡 [Text] 彡★",
            "⚡ [Text] ⚡",
            "▌│█║ [Text] ║█│▌"
        )

        for ((index, dec) in decorators.withIndex()) {
            val tv = TextView(this).apply {
                text = dec
                textSize = 15f
                setTextColor(0xFF07F57E.toInt())
                setPadding(dp(10f), dp(8f), dp(10f), dp(8f))
                setOnClickListener {
                    selectedDecoratorIndex = index
                    buildKeyboard()
                }
            }
            container.addView(tv)
        }

        val backBtn = Button(this).apply {
            text = "⬅ Back to Keyboard"
            setTextColor(0xFF000000.toInt())
            backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF07F57E.toInt())
            setOnClickListener { buildKeyboard() }
        }
        container.addView(backBtn)

        scroll.addView(container)
        keyboardRowsContainer.addView(scroll)
    }

    private fun showGuidePanel() {
        keyboardRowsContainer.removeAllViews()

        val scroll = ScrollView(this).apply { layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(210f)) }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12f), dp(12f), dp(12f), dp(12f))
        }

        val guideTv = TextView(this).apply {
            text = """
                📖 Singlish Guide:
                • k = ක, kh = ඛ, g = ග, gh = ඝ
                • ch = ච, chh = ඡ, j = ජ, jh = ඣ
                • thh = ත, thhh = ථ, dhh = ද, dhhh = ධ
                • p = ප, ph = ඵ, b = බ, bh = භ
                • nd = ඳ, nnd = ඬ, nng = ඟ, nnb = ඹ
                • kra = ක්‍ර්, pra = ප්‍ර, ksha = ක්‍ෂ
            """.trimIndent()
            textSize = 13.5f
            setTextColor(0xFFFFFFFF.toInt())
        }
        container.addView(guideTv)

        val backBtn = Button(this).apply {
            text = "⬅ Back to Keyboard"
            setTextColor(0xFF000000.toInt())
            backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF07F57E.toInt())
            setOnClickListener { buildKeyboard() }
        }
        container.addView(backBtn)

        scroll.addView(container)
        keyboardRowsContainer.addView(scroll)
    }

    // Helper functions
    private fun applyUnicodeOffset(text: String, upperBase: Int, lowerBase: Int): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                in 'A'..'Z' -> sb.append(String(Character.toChars(upperBase + (ch - 'A'))))
                in 'a'..'z' -> sb.append(String(Character.toChars(lowerBase + (ch - 'a'))))
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun getSinhalaHint(key: String): String {
        return when (key.lowercase()) {
            "q" -> "ඳ"
            "w" -> "අ"
            "e" -> "ඇ"
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
            "z" -> "ඡ"
            "x" -> "ං"
            "c" -> "ච"
            "v" -> "ව"
            "b" -> "බ"
            "n" -> "න"
            "m" -> "ම"
            else -> ""
        }
    }

    private fun updateCandidates() {
        candidateContainer.removeAllViews()
        if (currentComposingText.isEmpty()) return

        val phonetic = currentComposingText.toString()
        val sinhalaWord = SinhalaTransliterationEngine.transliterate(phonetic)
        val suggestions = listOf(sinhalaWord, phonetic)

        for (word in suggestions) {
            val tv = TextView(this).apply {
                text = word
                textSize = 15f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                setTextColor(0xFF07F57E.toInt())
                setPadding(dp(14f), dp(6f), dp(14f), dp(6f))
                background = GradientDrawable().apply {
                    cornerRadius = dp(14f).toFloat()
                    setColor(0xFF141414.toInt())
                    setStroke(dp(1f), 0xFF07F57E.toInt())
                }
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
