# Feladatlista - Beállítások, Déli emlékeztető és Nyelvválasztás (v102)

- [x] 1. Verziószám frissítése 102-re az `app/build.gradle.kts`-ben (`versionCode = 102`, `versionName = "102"`)
- [x] 2. `UserSettingsManager.kt` bővítése:
  - Déli emlékeztető (`reminderNoonEnabled`, `reminderNoonTime`)
  - Nyelv beállítás (`appLanguage`: system / hu / en)
- [x] 3. Nyelvváltás infrastruktúra kialakítása (Android Locale és per-app language támogatás, LocaleHelper)
- [x] 4. Szöveges erőforrások frissítése (`strings.xml`, `values-hu/strings.xml`)
- [x] 5. `SettingsScreen.kt` átdolgozása:
  - Déli emlékeztető kártya beépítése
  - Nyelvválasztó dialógus/menü beépítése
  - Adatkezelés és tesztelés (tesztadat generálás, összes adat törlése) teljes eltávolítása
- [x] 6. Fordítás és egységtesztek futtatása (`./gradlew testDebugUnitTest assembleDebug`)
- [x] 7. Telepítés Xiaomi 15 eszközre, ellenőrzés képernyőképekkel (magyar és angol nézet)
- [x] 8. Automatikus GitHub Release publikálás a v102 tag-gel és APK-val
- [x] 9. Git commit és push a távoli repository-ba
