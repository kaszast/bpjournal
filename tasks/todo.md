# Feladatlista - Samsung 3-gombos Navigáció Átfedés Javítása (v107)

- [x] 1. `MainActivity.kt`: `enableEdgeToEdge()` beállítása és `navigationBarsPadding()` hozzáadása a `bottomBar` felületéhez (`Surface` -> `Box` -> `Row`)
- [x] 2. `app/build.gradle.kts`: Verziószám léptetése 107-re (`versionCode = 107`, `versionName = "107"`)
- [x] 3. Egységtesztek futtatása és release/debug APK fordítása (`./gradlew testDebugUnitTest assembleDebug assembleRelease`)
- [x] 4. Telepítés és tesztelés valós eszközön
- [ ] 5. GitHub Release publikálás a v107 taggel és APK-val (`scripts/publish_release.py`)
- [ ] 6. Git commit és push
