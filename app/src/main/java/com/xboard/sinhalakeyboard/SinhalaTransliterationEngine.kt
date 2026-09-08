package com.xboard.sinhalakeyboard

object SinhalaTransliterationEngine {

    private val standaloneVowels = linkedMapOf(
        "aa" to "ආ", "AA" to "ඇ", "Aa" to "ඈ", "au" to "ඖ",
        "ruu" to "ෲ", "ru" to "ඍ", "ii" to "ඊ", "uu" to "ඌ",
        "ee" to "ඒ", "ei" to "ඒ", "oo" to "ඕ", "oe" to "ඕ",
        "a" to "අ", "A" to "ඇ", "i" to "ඉ", "u" to "උ",
        "e" to "එ", "E" to "ඒ", "o" to "ඔ"
    )

    private val vowelModifiers = linkedMapOf(
        "kAa" to "ෲ", "kruu" to "ෲ", "kru" to "ඍ", "kAA" to "ෑ", "kAa" to "ෑ", "kA" to "ැ",
        "kaa" to "ා", "kii" to "ී", "kuu" to "ූ", "kee" to "ේ", "kei" to "ේ", "kE" to "ේ",
        "koo" to "ෝ", "koe" to "ෝ", "kau" to "ෞ", "kH" to "ඃ", "kM" to "ං", "kaX" to "ඞ",
        "kazn" to "ඤ", "kya" to "්‍ය", "kra" to "්‍ර", "ka" to "", "ki" to "ි", "ku" to "ු",
        "ke" to "ෙ", "ko" to "ො"
    )

    private val consonants = linkedMapOf(
        "zdz" to "ඳ", "zqh" to "ඳ", "zdh" to "ඳ", "zda" to "ඬ", "zka" to "ඟ", "zha" to "ඥ", 
        "zja" to "ඣ", "zb" to "ඹ", "Lu" to "ළු", "kha" to "ඛ", "gha" to "ඝ", "Cha" to "ඡ", 
        "cHa" to "ඡ", "Tha" to "ඨ", "Dha" to "ඪ", "tha" to "ථ", "dha" to "ධ", "pha" to "ඵ", 
        "Pha" to "ඵ", "bha" to "භ", "sha" to "ශ", "Sha" to "ෂ", "cha" to "ච", "ka" to "ක", 
        "ga" to "ග", "ja" to "ජ", "ta" to "ට", "da" to "ඩ", "na" to "න", "Na" to "ණ", 
        "pa" to "ප", "ba" to "බ", "ma" to "ම", "ya" to "ය", "ra" to "ර", "la" to "ල", 
        "La" to "ළ", "wa" to "ව", "va" to "ව", "sa" to "ස", "ha" to "හ", "fa" to "ෆ"
    )

    fun transliterate(input: String): String {
        if (input.isEmpty()) return ""
        var result = StringBuilder()
        var i = 0
        val len = input.length

        while (i < len) {
            var matched = false
            if (i == 0 || input[i - 1] == ' ') {
                for ((key, value) in standaloneVowels) {
                    if (input.startsWith(key, i)) {
                        result.append(value)
                        i += key.length
                        matched = true
                        break
                    }
                }
                if (matched) continue
            }

            for ((cKey, cVal) in consonants) {
                val cBase = cKey.dropLast(1)
                if (input.startsWith(cBase, i)) {
                    var vMatched = false
                    val sub = input.substring(i + cBase.length)

                    for ((vKey, vVal) in vowelModifiers) {
                        val vSuffix = vKey.drop(1)
                        if (sub.startsWith(vSuffix)) {
                            result.append(cVal).append(vVal)
                            i += cBase.length + vSuffix.length
                            vMatched = true
                            matched = true
                            break
                        }
                    }
                    if (vMatched) break

                    result.append(cVal).append("්")
                    i += cBase.length
                    matched = true
                    break
                }
            }
            if (!matched) {
                result.append(input[i])
                i++
            }
        }
        return result.toString()
    }
}
