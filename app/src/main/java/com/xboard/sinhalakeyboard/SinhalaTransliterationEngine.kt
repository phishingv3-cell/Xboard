package com.xboard.sinhalakeyboard

object SinhalaTransliterationEngine {

    // 1. ස්වර (Independent Vowels)
    private val VOWELS = mapOf(
        "aae" to "ආ", "a" to "අ", "aa" to "ආ", "A" to "ඇ", "Aa" to "ඈ",
        "ae" to "ඇ", "Ae" to "ඈ", "i" to "ඉ", "ii" to "ඊ", "I" to "ඊ",
        "u" to "උ", "uu" to "ඌ", "U" to "ඌ", "e" to "එ", "ee" to "ඒ", "E" to "ඒ",
        "ai" to "ඓ", "o" to "ඔ", "oo" to "ඕ", "O" to "ඕ", "au" to "ඖ", "ou" to "ඖ",
        "ruu" to "ෲ", "ru" to "ඍ"
    )

    // 2. ව්‍යංජන (අල්පප්‍රාණ, මහප්‍රාණ සහ සඤ්ඤක අක්ෂර)
    private val CONSONANTS = mapOf(
        "k" to "ක්", "kh" to "ඛ්", "g" to "ග්", "gh" to "ඝ්", "ng" to "ඞ්", "nng" to "ඟ්",
        "ch" to "ච්", "chh" to "ඡ්", "j" to "ජ්", "jh" to "ඣ්", "gn" to "ඤ්", "ny" to "ඥ්",
        "t" to "ට්", "th" to "ඨ්", "d" to "ඩ්", "dh" to "ඪ්", "n" to "ණ්", "nnd" to "ඬ්",
        "thh" to "ත්", "thhh" to "ථ්", "dhh" to "ද්", "dhhh" to "ධ්", "nh" to "න්", "nd" to "ඳ්",
        "p" to "ප්", "ph" to "ඵ්", "b" to "බ්", "bh" to "භ්", "m" to "ම්", "nnb" to "ඹ්",
        "y" to "ය්", "r" to "ර්", "l" to "ල්", "v" to "ව්", "w" to "ව්",
        "sh" to "ශ්", "shh" to "ෂ්", "s" to "ස්", "h" to "හ්", "L" to "ළ්", "f" to "ෆ්"
    )

    // 3. පිල්ලම් (Diacritics)
    private val PILI = mapOf(
        "a" to "", "aa" to "ා", "A" to "ැ", "Aa" to "ෑ", "ae" to "ැ", "aae" to "ෑ",
        "i" to "ි", "ii" to "ී", "I" to "ී", "u" to "ු", "uu" to "ූ", "U" to "ූ",
        "e" to "ෙ", "ee" to "ේ", "E" to "ේ", "ai" to "ෛ", "o" to "ො", "oo" to "ෝ",
        "O" to "ෝ", "au" to "ෞ", "ou" to "ෞ", "ru" to "ෘ", "ruu" to "ෲ"
    )

    // 4. සංයෝග සහ රේඵය
    private val SPECIAL = mapOf(
        "ksha" to "ක්‍ෂ", "kra" to "ක්‍ර", "tra" to "ත්‍ර", "pra" to "ප්‍ර",
        "bra" to "බ්‍ර", "shri" to "ශ්‍රී", "shree" to "ශ්‍රී", "dra" to "ද්‍ර",
        "gra" to "ග්‍ර", "m" to "ං"
    )

    fun transliterate(input: String): String {
        if (input.isEmpty()) return ""
        val sb = StringBuilder()
        var i = 0
        val len = input.length

        while (i < len) {
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

            val p4 = if (i + 4 <= len) input.substring(i, i + 4).lowercase() else ""
            val p3 = if (i + 3 <= len) input.substring(i, i + 3).lowercase() else ""
            val p2 = if (i + 2 <= len) input.substring(i, i + 2).lowercase() else ""
            val p1 = input.substring(i, i + 1)

            val consKey = when {
                p4.isNotEmpty() && CONSONANTS.containsKey(p4) -> p4
                p3.isNotEmpty() && CONSONANTS.containsKey(p3) -> p3
                p2.isNotEmpty() && CONSONANTS.containsKey(p2) -> p2
                CONSONANTS.containsKey(p1) -> p1
                CONSONANTS.containsKey(p1.lowercase()) -> p1.lowercase()
                else -> null
            }

            if (consKey != null) {
                val baseConsonant = CONSONANTS[consKey]!!.replace("්", "")
                i += consKey.length

                val v3 = if (i + 3 <= len) input.substring(i, i + 3).lowercase() else ""
                val v2 = if (i + 2 <= len) input.substring(i, i + 2).lowercase() else ""
                val v1 = if (i + 1 <= len) input.substring(i, i + 1) else ""

                when {
                    v3.isNotEmpty() && PILI.containsKey(v3) -> { sb.append(baseConsonant).append(PILI[v3]); i += 3 }
                    v2.isNotEmpty() && PILI.containsKey(v2) -> { sb.append(baseConsonant).append(PILI[v2]); i += 2 }
                    v1.isNotEmpty() && PILI.containsKey(v1) -> { sb.append(baseConsonant).append(PILI[v1]); i += 1 }
                    v1.isNotEmpty() && PILI.containsKey(v1.lowercase()) -> { sb.append(baseConsonant).append(PILI[v1.lowercase()]); i += 1 }
                    else -> sb.append(CONSONANTS[consKey])
                }
                continue
            }

            val iv3 = if (i + 3 <= len) input.substring(i, i + 3).lowercase() else ""
            val iv2 = if (i + 2 <= len) input.substring(i, i + 2).lowercase() else ""
            val iv1 = input.substring(i, i + 1)

            when {
                iv3.isNotEmpty() && VOWELS.containsKey(iv3) -> { sb.append(VOWELS[iv3]); i += 3 }
                iv2.isNotEmpty() && VOWELS.containsKey(iv2) -> { sb.append(VOWELS[iv2]); i += 2 }
                VOWELS.containsKey(iv1) -> { sb.append(VOWELS[iv1]); i += 1 }
                VOWELS.containsKey(iv1.lowercase()) -> { sb.append(VOWELS[iv1.lowercase()]); i += 1 }
                else -> { sb.append(input[i]); i++ }
            }
        }
        return sb.toString()
    }
}
