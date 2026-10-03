# Feladatlista - Mintaadatok eltávolítása és Emlékeztető rendszer javítása

- [x] 1. Mintaadatok betöltésének eltávolítása
  - [x] `BloodPressureViewModel.kt`: Automatikus mintaadat-betöltés törlése az `init` blokkból, `addSampleData` és `replaceWithSampleData` eltávolítása
  - [x] `DashboardScreen.kt`: Mintaadatok betöltése gomb eltávolítása az üres állapot kártyájáról
  - [x] `strings.xml` (HU, EN, default): Welcome szövegek frissítése (mintaadat hivatkozás kivezetése)
- [x] 2. Emlékeztető és Értesítési rendszer megvalósítása
  - [x] `ReminderScheduler.kt`: Pontos időzítés és ismétlődés kalkuláció (`AlarmManager.setAlarmClock`), riasztások ütemezése és törlése
  - [x] `ReminderReceiver.kt`: Riasztás fogadása, NotificationChannel létrehozása (`IMPORTANCE_HIGH`, hang, rezgés), Notification megjelenítése, következő napi riasztás automatikus újraütemezése
  - [x] `BootReceiver.kt`: `ACTION_BOOT_COMPLETED` és csomagfrissítés után aktív emlékeztetők újbóli beállítása
  - [x] `AndroidManifest.xml`: Engedélyek (`RECEIVE_BOOT_COMPLETED`, `SCHEDULE_EXACT_ALARM`, `POST_NOTIFICATIONS`) és Receiverek konfigurálása
  - [x] `UserSettingsManager.kt` & `BloodPressureViewModel.kt`: Emlékeztető ki/bekapcsolásakor és időpont módosításakor `ReminderScheduler` hívása
  - [x] Runtime jogosultság kérése: `POST_NOTIFICATIONS` ellenőrzése és kérése a `SettingsScreen.kt`-ben
- [x] 3. Verziószám és Build
  - [x] `app/build.gradle.kts`: `versionCode = 109`, `versionName = "109"`
  - [x] Unit tesztek és Release build futtatása
- [x] 4. Működés igazolása és tesztelés Xiaomi 15-ön
  - [x] Üres adatbázis tesztelése (nincs mintaadat)
  - [x] Emlékeztető tesztelése (teszt riasztás beállítása 1 percre, hang és értesítés ellenőrzése adb / képernyő mentés útján)
  - [x] Release AAB csomag előállítása és ellenőrzése
- [x] 5. GitHub Release és APK feltöltés a v109 taggel (`scripts/publish_release.py`)

