# BPJournal Részletes Specifikáció és Teendők

## Jóváhagyott specifikáció
- **Alkalmazás neve:** BPJournal
- **Célplatform:** Android (Kotlin, Jetpack Compose, Material 3 - orvosi/slate színpaletta, neon/UV színek nélkül)
- **Lokalizáció:** Magyar (alapértelmezett/hu) és Angol (en)
- **Git tároló:** `https://github.com/kaszast/bpjournal.git`
- **Főbb funkciók:**
  1. Vérnyomás értékek rögzítése: Szisztolés, Diasztolés, Pulzus (BPM), dátum és idő, mérési kar (bal/jobb), testhelyzet (ülő/fekvő/álló), címkék/körülmények (nyugalmi, gyógyszer után, stressz, koffein, testmozgás), megjegyzés.
  2. Orvosi kiértékelés: ESH/ESC (Európai Hipertónia Társaság) irányelvek szerinti kategória és Pulzusnyomás (PP = szisztolés - diasztolés) és MAP (Mean Arterial Pressure) kalkuláció.
  3. Statisztikák és grafikonok: Napi, heti, havi átlagok, eloszlási arányok, interaktív Compose Canvas grafikon.
  4. Adat export:
     - CSV export (szabványos mezőkkel, Intent share / SAF mentés).
     - Orvosi PDF riport generálás (Android PdfDocument natív API, statisztikai összefoglaló táblázat és mérési napló).
  5. Szinkronizáció: Health Connect API (`BloodPressureRecord`) azonnali kétirányú/mentési integráció, ami közvetlenül szinkronizál a Google Fit-tel.
  6. Emlékeztetők: Napi mérési emlékeztetők beállítása értesítésekkel.
  7. Adatkezelés: Helyi Room adatbázis (offline-first, titkosítás-barát, zero-leak).

---

## Feladatok
- [x] Specifikáció egyeztetése és rögzítése
- [x] Git tároló inicializálása és távoli origin (`https://github.com/kaszast/bpjournal.git`) beállítása, `.gitignore` létrehozása
- [x] Gradle projekt struktúra és konfigurációs fájlok létrehozása (`settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, gradle wrapper)
- [x] Színek, téma (Slate & Teal orvosi paletta - UV mentes) és Android manifest elkészítése
- [x] Lokalizációs stringek elkészítése (`values/strings.xml`, `values-hu/strings.xml`)
- [x] Adatmodell és perzisztencia (Room/SQLite entitások, DB Helper, Repository)
- [x] Üzleti logika: ESH kategóriák, MAP kalkuláció, szűrés és időszaki aggregációk (napi/heti/havi átlagok)
- [x] Health Connect szinkronizációs menedzser megvalósítása (Google Fit azonnali integráció)
- [x] Export szolgáltatás: CSV generátor és orvosi PDF riport (`PdfDocument`) generátor
- [x] UI komponensek:
  - Főképernyő (Dashboard): legutóbbi mérés, mai/heti átlagok, ESH indikátor
  - Mérési adatbevitel képernyő / párbeszédablak (dátum/idő választó, kar, kontextus)
  - Előzmények képernyő: szűrés, lapozás/keresés, törlés/módosítás
  - Grafikonok és trendek képernyő: napi, heti, havi nézetek
  - Export képernyő: időintervallum választás, PDF / CSV megosztás
  - Beállítások: Health Connect állapot, emlékeztetők
- [x] Unit tesztek (aggregációk, ESH kalkuláció, CSV generálás, Health Connect mapper)
- [x] Projekt build és tesztek futtatása, ellenőrzése (testDebugUnitTest és assembleDebug SIKERES)
- [x] Git állapot ellenőrzése és összefoglaló jelentés készítése
