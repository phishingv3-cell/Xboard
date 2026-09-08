package com.xboard.sinhalakeyboard

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class SettingsActivity : AppCompatActivity() {

    private var rewardedAd: RewardedAd? = null
    private var pendingThemeName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        loadRewardedAd()

        // උදාහරණයක් ලෙස Live Nature Theme එක එබූ විට
        val btnNature = findViewById<Button>(R.id.btn_theme_nature)
        btnNature.setOnClickListener {
            applyOrWatchAdForTheme("live_nature.gif")
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        // AdMob Rewarded Ad Unit ID එක මෙතනට දමන්න (Test ID එකක් පහත දී ඇත)
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }
            })
    }

    private fun applyOrWatchAdForTheme(themeGifName: String) {
        pendingThemeName = themeGifName
        if (rewardedAd != null) {
            rewardedAd?.show(this) { _ ->
                // Ad එක බැලීම සාර්ථකව අවසන් වූ විට Theme එක Save කිරීම
                saveSelectedTheme(themeGifName)
                Toast.makeText(this, "Live Theme Unlocked Successfully!", Toast.LENGTH_SHORT).show()
                loadRewardedAd() // ඊළඟ Ad එක Load කර තබා ගැනීම
            }
        } else {
            // Ad එක Load නැතිනම් හෝ Offline නම් සෘජුවම Apply කිරීමට ඉඩදීම
            saveSelectedTheme(themeGifName)
            Toast.makeText(this, "Theme Applied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSelectedTheme(themeGifName: String) {
        val prefs = getSharedPreferences("keyboard_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("live_theme_gif", themeGifName).apply()
    }
}
