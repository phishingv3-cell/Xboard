# X Board Sinhala Keyboard - Google Play Store එකට Upload කරන පියවරෙන් පියවර මාර්ගෝපදේශය

මෙම Android Studio Keyboard Application එක ඔබගේ නමින් හෝ ආයතනයේ නමින් Google Play Store එකට දැමීමට පහත පියවර අනුගමනය කරන්න:

---

### පියවර 1: Android Studio මෘදුකාංගය ස්ථාපනය කර Project එක Open කරගැනීම
1. නොමිලේ [developer.android.com/studio](https://developer.android.com/studio) වෙබ් අඩවියෙන් **Android Studio** (නවතම අනුවාදය) බාගත කර ස්ථාපනය කරගන්න.
2. මෙම බාගත කරගත් ZIP ගොනුව Extract කරගන්න.
3. Android Studio විවෘත කර **"Open"** තෝරා, මෙම ෆෝල්ඩරය තෝරන්න.
4. Gradle Build එක සම්පූර්ණ වන තෙක් සුළු වේලාවක් රැඳී සිටින්න.

---

### පියවර 2: දුරකථනයට හෝ Emulator එකට දමා පරීක්ෂා කිරීම (Testing)
1. ඔබගේ Android දුරකථනයේ **Settings -> Developer Options -> USB Debugging** On කරන්න.
2. පරිගණකයට USB කේබලයකින් දුරකථනය සම්බන්ධ කරන්න.
3. Android Studio හි ඉහළ ඇති කොළ පැහැති **Run (▶)** බොත්තම ඔබන්න.
4. ඇප් එක දුරකථනයේ ස්ථාපනය වූ පසු:
   - "Enable in Settings" ඔබා යතුරුපුවරුව සක්‍රිය කරන්න.
   - "Choose Keyboard" ඔබා පෙරනිමි යතුරුපුවරුව ලෙස තෝරන්න.
   - ඕනෑම ඇප් එකක (WhatsApp, Facebook, SMS) සිංහල ටයිප් කර බලන්න!

---

### පියවර 3: Release AAB (Android App Bundle) එක සාදාගැනීම
Play Store එකට Upload කිරීමට අවශ්‍ය වන්නේ Signed .aab ගොනුවකි:
1. Android Studio හි ඉහළ Menu එකෙන් **Build -> Generate Signed Bundle / APK...** තෝරන්න.
2. **Android App Bundle** තෝරා Next ඔබන්න.
3. **Key store path** සඳහා "Create new..." ඔබා ඔබගේම Keystore ගොනුවක් සාදා Password එකක් දෙන්න (මෙම Key එක සහ Password එක ආරක්‍ෂිතව තබාගන්න - පසුව Updates දීමට අවශ්‍ය වේ).
4. Build Variant එක **release** තෝරා **Finish** ඔබන්න.
5. මිනිත්තු 1-2 කින් ඔබගේ `app/release/app-release.aab` ගොනුව සූදානම් වේ.

---

### පියවර 4: Google Play Console ගිණුමක් සෑදීම
1. [play.google.com/console](https://play.google.com/console) වෙත පිවිසෙන්න.
2. ඔබගේ Google Account එකෙන් Sign-in වී $25 ක එක්වරක් පමණක් ගෙවන ලියාපදිංචි ගාස්තුව ගෙවා Developer Account එක සාදාගන්න.

---

### පියවර 5: New App සාදා තොරතුරු පිරවීම
1. Play Console හි **"Create App"** ඔබන්න.
2. App Name: **X Board Sinhala Keyboard**
3. Default language: **Sinhala (si-LK)** හෝ **English (United States)**
4. App or Game: **App** | Free or Paid: **Free**

---

### පියවර 6: Store Listing Graphics & Details
- **Short Description**: "වේගවත් හා පහසු සිංහල හා ඉංග්‍රීසි ස්මාර්ට් යතුරුපුවරුව."
- **Full Description**: "Singlish phonetic typing, Wijesekara layout, emoji සහ smart suggestions සහිත නවීන සිංහල යතුරුපුවරුව."
- **App Icon**: 512 x 512 pixels (PNG/JPEG)
- **Feature Graphic**: 1024 x 500 pixels (PNG/JPEG)
- **Phone Screenshots**: දුරකථනයෙන් ගත් screenshots අවම වශයෙන් 2-4 ක්.

---

### පියවර 7: App Content & Policy සම්පූර්ණ කිරීම
- **Privacy Policy**: යතුරුපුවරු සඳහා Privacy Policy එකක් අනිවාර්ය වේ. පරිශීලකයාගේ යතුරුපුවරු දත්ත හෝ passwords කිසිවක් පිටත සර්වර් වෙත යවන්නේ නැති බව (Offline & Privacy-safe) සඳහන් කරන්න.
- **Data Safety Form**: පුද්ගලික දත්ත කිසිවක් රැස් නොකරන බැවින් "No user data is collected or shared" තෝරන්න.
- **Target Audience**: වයස් කාණ්ඩය (උදා: 13+).

---

### පියවර 8: Release කර Play Store වෙත යැවීම
1. **Production -> Create new release** වෙත යන්න.
2. ඔබ පියවර 3 හි සෑදූ `app-release.aab` ගොනුව Drag & Drop කර Upload කරන්න.
3. Release notes ලියන්න: "Initial release of X Board Sinhala Keyboard".
4. **Review release -> Start rollout to Production** ඔබන්න.
5. දින 1-3ක් ඇතුළත Google සමාගම පරීක්ෂා කර ඔබගේ යතුරුපුවරු ඇප් එක ලොව පුරා පරිශීලකයන්ට Play Store එකෙන් Download කරගත හැකි අයුරින් Live කරනු ඇත!
