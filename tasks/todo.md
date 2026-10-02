# Feladatlista - UI és Funkcionális Javítások (v100)

- [x] 1. Verziószám frissítése 100-ra az `app/build.gradle.kts`-ben (`versionCode = 100`, `versionName = "100"`)
- [x] 2. DashboardScreen fejlécének frissítése: profilkép törlése, verziószám chip ("v100") elhelyezése
- [x] 3. DashboardScreen PulseRing és EshCategoryBadge igazítása:
  - BPM szám és "BPM" felirat közelebb hozása, pontosan középre igazítva
  - "EMELKEDETT NORMÁL" badge sormagasság szűkítése, tördelés megakadályozása
- [x] 4. Grafikonok és statisztikák dátumformázásának regionális beállításokhoz igazítása (Locale-alapú dátumformátum)
- [x] 5. BloodPressureCard egységesítése és PP/MAP lecserélése ST és DST jelölésre:
  - Minden kártya azonos felépítésű legyen
  - PP és MAP eltávolítása, helyette ST és DST mindenhol
- [x] 6. Keresés a kódban minden előfordulásra, ahol nem a teljes Systole / Diastole szó szerepel, és ST / DST-re cserélés
- [x] 7. StatisticsScreen: Időszakos mérések listájának tömörítése (sorok közti hézag csökkentése, egybefüggő kártya)
- [x] 8. AddEditEntryDialog újratervezése:
  - Görgetős dobválasztó (wheel picker) Szisztolé (ST), Diasztolé (DST) és Pulzus értékekhez
  - Egy képernyőre igazítás görgetésmentesen, azonnal elérhető Mentés és Mégse gombokkal
- [x] 9. Fordítás és egységtesztek futtatása (`./gradlew testDebugUnitTest assembleDebug`)
- [x] 10. Telepítés Xiaomi 15 eszközre, képernyőképek készítése és vizuális ellenőrzés
- [ ] 11. Git commit és automatikus push a távoli tárhelyre (`origin/main`)
