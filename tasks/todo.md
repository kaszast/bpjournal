# Feladatlista - Beállítások Ikon Lefagyás Javítása (v106)

- [x] 1. Hiba javítása `MainActivity.kt`-ban: `LocalizedActivityContext` és `LocalActivityResultRegistryOwner provides this@MainActivity`
- [x] 2. Verziószám léptetése 106-ra (`app/build.gradle.kts`: versionCode = 106, versionName = "106")
- [x] 3. Egységtesztek futtatása és release/debug APK fordítása (`./gradlew testDebugUnitTest assembleDebug assembleRelease`)
- [x] 4. Telepítés a csatlakoztatott Xiaomi 15 készülékre és SettingsScreen tesztelése (kattintás a beállítások ikonra, crash hiányának és a képernyő megjelenésének igazolása)
- [x] 5. GitHub Release és APK feltöltés a v106 taggel (`scripts/publish_release.py`)
- [x] 6. Git commit és push a távoli tárba
