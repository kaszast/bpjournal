# Feladatlista - Angol Nyelvválasztás Teljes Működése (v105)

- [x] 1. Verziószám frissítése 105-re (`app/build.gradle.kts`: versionCode = 105, versionName = "105")
- [x] 2. String erőforrások kiegészítése és `values-en/strings.xml` létrehozása (`values/strings.xml`, `values-en/strings.xml`, `values-hu/strings.xml`)
- [x] 3. `MainActivity.kt` és `LocaleHelper.kt` javítása (`LocalContext provides localizedContext`, API 33+ per-app language és recomposition/recreate kezelés)
- [x] 4. Hardcode-olt szövegek lecserélése `stringResource(...)`-ra:
  - `DashboardScreen.kt`
  - `BloodPressureChart.kt`
  - `BloodPressureCard.kt`
  - `HistoryScreen.kt`
  - `SettingsScreen.kt`
  - `AddEditEntryDialog.kt`
- [x] 5. Építés és egységtesztek futtatása (`./gradlew testDebugUnitTest assembleDebug`)
- [x] 6. Eszköztesztelés valós telefonon (Xiaomi 15) adb-vel: angol nyelvre váltás és képernyőkép-ellenőrzés
- [x] 7. GitHub release publikálás a v105 tag-gel és APK-val (`scripts/publish_release.py`)
- [x] 8. Git commit és push a távoli repository-ba
