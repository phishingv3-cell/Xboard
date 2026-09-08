package com.xboard.sinhalakeyboard

/**
 * Phonetic Singlish to Sinhala Unicode Transliteration Engine
 * Compatible with Android IME
 */
object SinhalaTransliterationEngine {

    private val VOWELS = mapOf(
        "aae" to "ඈ", "a" to "අ", "aa" to "ආ", "A" to "ආ",
        "ae" to "ඇ", "Ae" to "ඈ", "i" to "ඉ", "ii" to "ඊ", "I" to "ඊ",
        "u" to "උ", "uu" to "ඌ", "U" to "ඌ", "e" to "එ", "ee" to "ඒ", "E" to "ඒ",
        "ai" to "ඓ", "o" to "ඔ", "oo" to "ඕ", "O" to "ඕ", "au" to "ඖ", "ou" to "ඖ"
    )

    private val CONSONANTS = mapOf(
        "k" to "ක්", "kh" to "ඛ්", "g" to "ග්", "gh" to "ඝ්", "nng" to "ඟ්", "ng" to "ං",
        "ch" to "ච්", "c" to "ච්", "chh" to "ඡ්", "j" to "ජ්", "jh" to "ඣ්", "ny" to "ඤ්", "gn" to "ඥ්",
        "t" to "ට්", "th" to "ත්", "thh" to "ඨ්", "d" to "ඩ්", "dh" to "ද්", "dhh" to "ඪ්",
        "n" to "න්", "N" to "ණ්", "p" to "ප්", "ph" to "ෆ්", "b" to "බ්", "bh" to "භ්",
        "m" to "ම්", "y" to "ය්", "r" to "ර්", "l" to "ල්", "L" to "ළ්",
        "v" to "ව්", "w" to "ව්", "sh" to "ෂ්", "shh" to "ශ්", "s" to "ස්", "h" to "හ්", "f" to "ෆ්"
    )

    private val PILI = mapOf(
        "a" to "", "aa" to "ා", "A" to "ා", "ae" to "ැ", "aae" to "ෑ", "Ae" to "ෑ",
        "i" to "ි", "ii" to "ී", "I" to "ී", "u" to "ු", "uu" to "ූ", "U" to "ූ",
        "e" to "ෙ", "ee" to "ේ", "E" to "ේ", "ai" to "ෛ", "o" to "ො", "oo" to "ෝ", "O" to "ෝ",
        "au" to "ෞ", "ou" to "ෞ"
    )

    private val SPECIAL = mapOf(
        "ksha" to "ක්ෂ", "kra" to "ක්‍ර", "tra" to "ත්‍ර", "pra" to "ප්‍ර",
        "bra" to "බ්‍ර", "shri" to "ශ්‍රී", "shree" to "ශ්‍රී", "nnd" to "ඳ", "nnb" to "ඹ"
    )

    fun transliterate(input: String): String {
        if (input.isEmpty()) return ""
        val sb = StringBuilder()
        var i = 0
        val len = input.length

        while (i < len) {
            // Check special clusters
            var clusterFound = false
            for ((pattern, sinhala) in SPECIAL) {
                if (input.substring(i).lowercase().startsWith(pattern)) {
                    sb.append(sinhala)
                    i += pattern.length
                    clusterFound = true
                    break
                }
            }
            if (clusterFound) continue

            // Check consonants
            val three = if (i + 3 <= len) input.substring(i, i + 3).lowercase() else ""
            val two = if (i + 2 <= len) input.substring(i, i + 2).lowercase() else ""
            val one = input.substring(i, i + 1)

            val consKey = when {
                three.isNotEmpty() && CONSONANTS.containsKey(three) -> three
                two.isNotEmpty() && CONSONANTS.containsKey(two) -> two
                CONSONANTS.containsKey(one) -> one
                CONSONANTS.containsKey(one.lowercase()) -> one.lowercase()
                else -> null
            }

            if (consKey != null) {
                val base = CONSONANTS[consKey]!!.replace("්", "")
                i += consKey.length

                // check modifier
                val p3 = if (i + 3 <= len) input.substring(i, i + 3).lowercase() else ""
                val p2 = if (i + 2 <= len) input.substring(i, i + 2).lowercase() else ""
                val p1 = if (i + 1 <= len) input.substring(i, i + 1) else ""

                when {
                    p3.isNotEmpty() && PILI.containsKey(p3) -> {
                        sb.append(base).append(PILI[p3])
                        i += 3
                    }
                    p2.isNotEmpty() && PILI.containsKey(p2) -> {
                        sb.append(base).append(PILI[p2])
                        i += 2
                    }
                    p1.isNotEmpty() && PILI.containsKey(p1) -> {
                        sb.append(base).append(PILI[p1])
                        i += 1
                    }
                    p1.isNotEmpty() && PILI.containsKey(p1.lowercase()) -> {
                        sb.append(base).append(PILI[p1.lowercase()])
                        i += 1
                    }
                    else -> {
                        // no vowel follows -> append hal lakuna
                        sb.append(CONSONANTS[consKey])
                    }
                }
                continue
            }

            // Independent Vowels
            val v3 = if (i + 3 <= len) input.substring(i, i + 3).lowercase() else ""
            val v2 = if (i + 2 <= len) input.substring(i, i + 2).lowercase() else ""
            val v1 = input.substring(i, i + 1)

            when {
                v3.isNotEmpty() && VOWELS.containsKey(v3) -> {
                    sb.append(VOWELS[v3])
                    i += 3
                }
                v2.isNotEmpty() && VOWELS.containsKey(v2) -> {
                    sb.append(VOWELS[v2])
                    i += 2
                }
                VOWELS.containsKey(v1) -> {
                    sb.append(VOWELS[v1])
                    i += 1
                }
                VOWELS.containsKey(v1.lowercase()) -> {
                    sb.append(VOWELS[v1.lowercase()])
                    i += 1
                }
                else -> {
                    sb.append(input[i])
                    i++
                }
            }
        }

        return sb.toString()
    }
}
