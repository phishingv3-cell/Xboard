package com.xboard.sinhalakeyboard

/**
 * Transforms standard English letters into fancy stylized Unicode fonts
 */
object EnglishFontTransformer {

    fun transform(text: String, styleIndex: Int): String {
        return when (styleIndex) {
            1 -> toBold(text)
            2 -> toItalic(text)
            3 -> toScript(text)
            4 -> toCircled(text)
            5 -> toGothic(text)
            6 -> toDoubleStruck(text)
            7 -> toSansBold(text)
            8 -> toMonospace(text)
            9 -> toSmallCaps(text)
            else -> text
        }
    }

    fun toScript(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            if (code in 97..122) {
                val scriptChars = arrayOf("𝒶","𝒷","𝒸","𝒹","𝑒","𝒻","𝑔","𝒽","𝒾","𝒿","𝓀","𝓁","𝓂","𝓃","𝑜","𝓅","𝓆","𝓇","𝓈","𝓉","𝓊","𝓋","𝓌","𝓍","𝓎","𝓏")
                sb.append(scriptChars[code - 97])
            } else if (code in 65..90) {
                val scriptCaps = arrayOf("𝒜","ℬ","𝒞","𝒟","ℰ","ℱ","𝒢","ℋ","ℐ","𝒥","𝒦","ℒ","ℳ","𝒩","𝒪","𝒫","𝒬","ℛ","𝒮","𝒯","𝒰","𝒱","𝒲","𝒳","𝒴","𝒵")
                sb.append(scriptCaps[code - 65])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun toBold(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            sb.append(when (code) {
                in 97..122 -> Char(0x1D41A + (code - 97))
                in 65..90 -> Char(0x1D400 + (code - 65))
                in 48..57 -> Char(0x1D7CE + (code - 48))
                else -> ch
            })
        }
        return sb.toString()
    }

    private fun toItalic(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            sb.append(when (code) {
                in 97..122 -> Char(0x1D44E + (code - 97))
                in 65..90 -> Char(0x1D434 + (code - 65))
                else -> ch
            })
        }
        return sb.toString()
    }

    private fun toCircled(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            sb.append(when (code) {
                in 97..122 -> Char(0x24D0 + (code - 97))
                in 65..90 -> Char(0x24B6 + (code - 65))
                in 49..57 -> Char(0x2460 + (code - 49))
                48 -> "⓪"
                else -> ch
            })
        }
        return sb.toString()
    }

    private fun toGothic(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            sb.append(when (code) {
                in 97..122 -> Char(0x1D51E + (code - 97))
                in 65..90 -> Char(0x1D504 + (code - 65))
                else -> ch
            })
        }
        return sb.toString()
    }

    private fun toDoubleStruck(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            sb.append(when (code) {
                in 97..122 -> Char(0x1D552 + (code - 97))
                in 65..90 -> Char(0x1D538 + (code - 65))
                else -> ch
            })
        }
        return sb.toString()
    }

    private fun toSansBold(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            sb.append(when (code) {
                in 97..122 -> Char(0x1D5BA + (code - 97))
                in 65..90 -> Char(0x1D5A0 + (code - 65))
                else -> ch
            })
        }
        return sb.toString()
    }

    private fun toMonospace(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            sb.append(when (code) {
                in 97..122 -> Char(0x1D68A + (code - 97))
                in 65..90 -> Char(0x1D670 + (code - 65))
                else -> ch
            })
        }
        return sb.toString()
    }

    private fun toSmallCaps(text: String): String {
        val smallCapsMap = mapOf(
            'a' to "ᴀ", 'b' to "ʙ", 'c' to "ᴄ", 'd' to "ᴅ", 'e' to "ᴇ",
            'f' to "ꜰ", 'g' to "ɢ", 'h' to "ʜ", 'i' to "ɪ", 'j' to "ᴊ",
            'k' to "ᴋ", 'l' to "ʟ", 'm' to "ᴍ", 'n' to "ɴ", 'o' to "ᴏ",
            'p' to "ᴘ", 'q' to "𝚀", 'r' to "ʀ", 's' to "ꜱ", 't' to "ᴛ",
            'u' to "ᴜ", 'v' to "ᴠ", 'w' to "ᴡ", 'x' to "x", 'y' to "ʏ", 'z' to "ᴢ"
        )
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(smallCapsMap[ch.lowercaseChar()] ?: ch)
        }
        return sb.toString()
    }
}
