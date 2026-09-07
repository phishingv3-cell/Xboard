package com.xboard.sinhalakeyboard

/**
 * Built-in Sinhala - English Dictionary Engine
 */
object SinhalaDictionary {
    private val dictionaryMap = mapOf(
        "ආයුබෝවන්" to "Ayubowan / Hello",
        "ස්තූතියි" to "Thank you",
        "සුබ උදෑසනක්" to "Good Morning",
        "සුබ රාත්‍රියක්" to "Good Night",
        "යතුරුපුවරුව" to "Keyboard",
        "දුරකථනය" to "Telephone / Phone",
        "ගෙදර" to "Home / House",
        "මිතුරා" to "Friend",
        "වැඩසටහන" to "Program / Application",
        "කරුණාකර" to "Please",
        "ලස්සන" to "Beautiful",
        "වේගවත්" to "Fast / Speedy"
    )

    fun lookup(query: String): String? {
        return dictionaryMap[query.trim()]
    }
}
