# BPJournal - Blood Pressure & Cardiovascular Log / Vérnyomás és Keringési Napló

[![Release](https://img.shields.io/github/v/release/kaszast/bpjournal?color=teal&label=Release)](https://github.com/kaszast/bpjournal/releases)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-blue)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-purple)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-green)](https://developer.android.com/jetpack/compose)

---

## Magyar Leírás (Hungarian)

A **BPJournal** egy modern, biztonságos, orvosi szabványoknak megfelelő, adatvédelmi fókuszú Android vérnyomás- és keringéskövető alkalmazás. Letisztult, professzionális megjelenésű felülettel készült (Medical Slate & Teal dizájn), kifejezetten az orvosi pontosságra és a könnyű kezelhetőségre optimalizálva.

### Főbb Funkciók

1. **Ergonomikus adatrögzítés és szerkesztés:**
   - **Görgetős dobválasztó (Wheel Picker):** Szisztolé (ST: 60–260 Hgmm), Diasztolé (DST: 40–160 Hgmm) és Pulzus (40–200 BPM) finom görgetéssel állítható be gépelés helyett.
   - **Egyképernyős modalitás:** Az adatrögzítő ablak görgetés nélkül elfér egyetlen képernyőn, a Mentés és Mégse gombok azonnal elérhetők.
   - **Dátum és időpont precíz szerkesztése:** Dátum- és időválasztó dialógusok a mérés pontos idejének utólagos módosításához.
   - **Kontextuális adatok:** Mérési kar (Bal / Jobb kar), testhelyzet (Ülő, Fekvő, Álló), címkék (Nyugalmi, Gyógyszer után, Stressz, Koffein, Testmozgás) és orvosi megjegyzések.

2. **Orvosi kiértékelés (ESH / ESC Szabvány):**
   - Hivatalos európai határértékek szerinti automatikus színkódolt besorolás:
     - Optimális (< 120 / < 80 Hgmm)
     - Normál (120–129 / 80–84 Hgmm)
     - Emelkedett normál (130–139 / 85–89 Hgmm)
     - I. fokú hipertónia (140–159 / 90–99 Hgmm)
     - II. fokú hipertónia (160–179 / 100–109 Hgmm)
     - III. fokú hipertónia (≥ 180 / ≥ 110 Hgmm)
     - Izolált szisztolés hipertónia
   - Egyértelmű **ST** (Szisztolés) és **DST** (Diasztolés) jelölések a kártyákon és a felületeken.

3. **Előzmények és Mérések Kezelése:**
   - Valós idejű szabadszöveges keresés és szűrés.
   - Megnövelt méretű szerkesztés és törlés gombok a gyors kezelhetőségért.
   - Biztonsági megerősítő párbeszédpanel (`AlertDialog`) törlés előtt a mérés pontos adataival.

4. **Trendgrafikonok és Időszakos Statisztikák:**
   - Napi, heti és havi aggregált átlagok interaktív Canvas trendgrafikonnal és célérték sávval.
   - Regionális beállításokat követő automatikus dátum- és időformátum.

5. **Mérési Emlékeztetők:**
   - Három függetlenül konfigurálható napi emlékeztető: **Reggeli**, **Déli** és **Esti** méréshez, tetszőlegesen módosítható időpontokkal.

6. **Többnyelvűség és Dinamikus Nyelvváltás:**
   - Alkalmazáson belüli azonnali nyelvváltás: **Rendszer alapértelmezett**, **Magyar** és **Angol (English)** között.
   - Teljes felület-, dátum- és időformátum adaptáció.

7. **Health Connect & Google Fit Szinkronizáció:**
   - Android 14+ rendszer-szintű Health Connect integráció (`BloodPressureRecord`).
   - Új mérések automatikus háttér-szinkronizálása és manuális teljes kötegfeltöltés.

8. **Orvosi Exportálás:**
   - **Orvosi PDF lelet:** Szabványos A4 nyomtatható orvosi dosszié statisztikákkal, határérték-megoszlással és táblázatos mérési naplóval.
   - **CSV táblázat:** Univerzális táblázatos export Excelhez és elemzésekhez.

---

## English Description

**BPJournal** is a modern, privacy-focused, medical-grade blood pressure and cardiovascular journaling app for Android. Engineered with a clean Medical Slate & Teal aesthetic, it emphasizes ergonomics, clarity, and compliance with clinical guidelines.

### Core Features

1. **Ergonomic Data Entry & Modification:**
   - **Interactive Wheel Pickers:** Smooth snap-fling scrolling selectors for Systolic (ST: 60–260 mmHg), Diastolic (DST: 40–160 mmHg), and Pulse (40–200 BPM) replacing error-prone keypad input.
   - **Single-Screen Dialog Layout:** Compact modal fitting entirely on a single screen viewport without vertical scroll barriers.
   - **Customizable Timestamp:** Native calendar and time pickers to backdate or fine-tune recorded measurements.
   - **Clinical Context Metadata:** Arm selection (Left/Right), body posture (Sitting, Lying down, Standing), situational tags, and free-text physician notes.

2. **Clinical Classification (ESH / ESC Guidelines):**
   - Automated color-coded categorization adhering to the European Society of Hypertension / European Society of Cardiology standards (Optimal, Normal, High Normal, Grade 1–3 Hypertension, Isolated Systolic).
   - Standardized **ST** and **DST** terminology throughout cards and charts.

3. **History & Record Management:**
   - Real-time text search and query filtering across notes, tags, and categories.
   - Enlarged edit and delete action buttons with generous touch targets.
   - Safe deletion flow featuring an explicit confirmation dialog displaying measurement details.

4. **Interactive Trends & Charts:**
   - Canvas-based trend visualizations supporting Daily, Weekly, and Monthly aggregation modes with reference target zones.
   - Locale-sensitive date and time formatting.

5. **Measurement Reminders:**
   - Three independent daily reminder schedules: **Morning**, **Midday (Noon)**, and **Evening**, customizable with native time pickers.

6. **In-App Dynamic Localization:**
   - Instant language switching between **System Default**, **Hungarian (Magyar)**, and **English**.
   - Zero-flicker re-rendering updating all labels and date formats.

7. **Health Connect & Google Fit Integration:**
   - Native integration with Android Health Connect API (`BloodPressureRecord`).
   - Real-time background sync on every entry and manual batch synchronization.

8. **Medical Export:**
   - **Clinical PDF Dossier:** ISO A4 printable summary featuring statistical breakdowns, min/max values, and complete logs.
   - **CSV Spreadsheet:** Standard tabular export compatible with spreadsheet software.

---

## Architecture & Technology Stack

- **Language:** Kotlin 2.4.10
- **UI Framework:** Jetpack Compose with Material Design 3
- **Design Pattern:** MVVM (Model-View-ViewModel) + Clean Architecture
- **Concurrency & State:** Kotlin Coroutines & `StateFlow`
- **Database:** Local SQLite (`BloodPressureDbHelper`) — zero cloud telemetry, 100% offline-first privacy
- **Integrations:** Android Health Connect Platform API (API 34+ native / API 26+ compatible)
- **Export Engine:** Native `android.graphics.pdf.PdfDocument` & `FileProvider`

---

## Build & Release

Build debug APK locally:
```bash
./gradlew testDebugUnitTest assembleDebug
```
Output path: `app/build/outputs/apk/debug/app-debug.apk`.

Publish new GitHub Release with automatic tagging:
```bash
python3 scripts/publish_release.py
```
Pre-compiled APKs are available directly on the [GitHub Releases page](https://github.com/kaszast/bpjournal/releases).
