package com.xboard.sinhalakeyboard

/**
 * Transforms standard English letters into fancy stylized Unicode fonts
 */
object EnglishFontTransformer {
    fun toScript(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            if (code in 97..122) { // a-z
                val scriptChars = arrayOf("𝒶","𝒷","𝒸","𝒹","𝑒","𝒻","𝑔","𝒽","𝒾","𝒿","𝓀","𝓁","𝓂","𝓃","𝑜","𝓅","𝓆","𝓇","𝓈","𝓉","𝓊","𝓋","𝓌","𝓍","𝓎","𝓏")
                sb.append(scriptChars[code - 97])
            } else if (code in 65..90) { // A-Z
                val scriptCaps = arrayOf("𝒜","ℬ","𝒞","𝒟","ℰ","ℱ","𝒢","ℋ","ℐ","𝒥","𝒦","ℒ","ℳ","𝒩","𝒪","𝒫","𝒬","ℛ","𝒮","𝒯","𝒰","𝒱","𝒲","𝒳","𝒴","𝒵")
                sb.append(scriptCaps[code - 65])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
