package com.xboard.sinhalakeyboard

import android.content.Context
import android.content.SharedPreferences

class UserHistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_word_history", Context.MODE_PRIVATE)

    fun saveWord(word: String) {
        val trimmed = word.trim()
        if (trimmed.length < 2) return

        val currentSet = prefs.getStringSet("history_words", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(trimmed)
        prefs.edit().putStringSet("history_words", currentSet).apply()
    }

    fun getMatchingHistory(prefix: String, limit: Int = 5): List<String> {
        if (prefix.isBlank()) return emptyList()
        val allWords = prefs.getStringSet("history_words", emptySet()) ?: emptySet()

        return allWords.filter { it.startsWith(prefix, ignoreCase = true) }.take(limit)
    }
}
