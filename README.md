# BPJournal - Vérnyomás és Keringési Napló (Android)

A **BPJournal** egy modern, megbízható és adatvédelmi fókuszú Android vérnyomás- és keringéskövető alkalmazás. Kifejezetten a modern orvosi szabványoknak megfelelő, letisztult, elegáns felülettel készült (orvosi mélykék és teal árnyalatok, a vibráló neon/UV színek teljes mellőzésével).

---

## Főbb funkciók

### 1. Mérési adatok precíz rögzítése
- Szisztolés és diasztolés értékek (Hgmm), pulzus (BPM).
- Dátum és idő pontos megadása.
- Mérési kar kiválasztása (Bal kar / Jobb kar).
- Testhelyzet rögzítése (Ülő, Fekvő, Álló).
- Körülmények és címkék (Nyugalmi, Gyógyszer után, Stressz, Koffein, Testmozgás).
- Szabad szöveges megjegyzések orvosi konzultációkhoz.

### 2. Orvosi kiértékelés és mutatószámok
- **ESH / ESC (European Society of Hypertension) kategóriák:**
  - Optimális (<120 / <80 Hgmm)
  - Normál (120–129 / 80–84 Hgmm)
  - Emelkedett normál (130–139 / 85–89 Hgmm)
  - I. fokú hipertónia (140–159 / 90–99 Hgmm)
  - II. fokú hipertónia (160–179 / 100–109 Hgmm)
  - III. fokú hipertónia (≥180 / ≥110 Hgmm)
  - Izolált szisztolés hipertónia (≥140 és <90 Hgmm)
- **Keringési mutatók automatikus számítása:**
  - **PP (Pulse Pressure / Pulzusnyomás):** Szisztolés − Diasztolés
  - **MAP (Mean Arterial Pressure / Középnyomás):** Diasztolés + (PP / 3)

### 3. Statisztikák és trendgrafikonok
- Napi, heti és havi aggregált átlagok automatikus kalkulációja.
- Személyre szabott, reszponzív Jetpack Compose Canvas trendgrafikon referencia célérték sávval.
- Kategória-eloszlási arányok (hány % optimális, normál, magas).

### 4. Orvosi PDF és táblázatos CSV exportálás
- **Orvosi PDF lelet:** Natív Android `PdfDocument` technológiával készült, szabványos A4 méretű nyomtatható dokumentum statisztikai összefoglaló panellel, átlagokkal, min/max értékekkel és részletes mérési napló táblázattal.
- **CSV export:** Szabványos formátum Excel és egyéb orvosi statisztikai szoftverekhez.
- Azonnali rendszer megosztási lehetőség (ShareSheet: e-mail, felhő, üzenet).

### 5. Health Connect és Google Fit azonnali szinkronizáció
- Közvetlen integráció az Android modern **Health Connect** (`BloodPressureRecord`) rendszerével.
- Új mérés rögzítésekor a rendszer automatikusan és azonnal szinkronizálja a vérnyomás adatokat.
- A szinkronizált adatok közvetlenül elérhetők a felhasználó összekapcsolt **Google Fit** és egyéb fitness fiókjaiban.

### 6. Nyelvi lokalizáció
- Teljes körű **magyar (HU)** és **angol (EN)** nyelv támogatás a rendszer nyelve szerint.

---

## Architektúra és Technológiák
- **Nyelv:** Kotlin (2.4.10)
- **UI Keretrendszer:** Jetpack Compose + Material 3
- **Tervezési minta:** MVVM (Model-View-ViewModel) + Clean Architecture
- **Aszinkronitás:** Kotlin Coroutines & StateFlow
- **Adatbázis:** SQLite (offline-first, zero-leakage, beépített perzisztencia)
- **Rendszerintegráció:** Health Connect Platform API (Android 14+ / API 34+ natív és API 26+ kompatibilis)
- **Export:** Natív `android.graphics.pdf.PdfDocument` és `FileProvider`

---

## Fordítás és Futtatás
```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```
A lefordított debug APK helye: `app/build/outputs/apk/debug/app-debug.apk`.
