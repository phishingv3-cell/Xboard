package com.xboard.sinhalakeyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
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

        // 1. Enable Keyboard in Settings (activity_settings.xml හි ඇති ID එක: btnEnableKeyboard)
        val btnEnableKeyboard = findViewById<Button>(R.id.btnEnableKeyboard)
        btnEnableKeyboard.setOnClickListener {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            startActivity(intent)
        }

        // 2. Choose Keyboard (activity_settings.xml හි ඇති ID එක: btnSwitchKeyboard)
        val btnSwitchKeyboard = findViewById<Button>(R.id.btnSwitchKeyboard)
        btnSwitchKeyboard.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        // 3. Live Nature Theme Button
        val btnNature = findViewById<Button>(R.id.btn_theme_nature)
        btnNature.setOnClickListener {
            applyOrWatchAdForTheme("live_nature.gif")
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
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
                saveSelectedTheme(themeGifName)
                Toast.makeText(this, "Live Theme Unlocked Successfully!", Toast.LENGTH_SHORT).show()
                loadRewardedAd()
            }
        } else {
            saveSelectedTheme(themeGifName)
            Toast.makeText(this, "Theme Applied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSelectedTheme(themeGifName: String) {
        val prefs = getSharedPreferences("keyboard_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("live_theme_gif", themeGifName).apply()
    }
}
