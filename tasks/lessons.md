# Lessons Learned
<!-- Formátum: [Dátum/Kontextus] Hiba -> Szabály -->
[2026-10-02/UI] Hiányzó funkcionális beállítások és hiányzó időpont-szerkesztés -> Beállításokban valódi állapotkezelő opciók kellenek; mérési naplózásnál az időpontnak mindig szerkeszthetőnek kell lennie.
[2026-10-03/Release] APK Release elmaradása -> Minden sikeres APK/AAB build és verzióléptetés után kötelező azonnal lefutattni a 'scripts/publish_release.py' szkriptet, hogy a kiadás (tag, release jegyzet, APK asset) kikerüljön a GitHub Releases közé.
[2026-10-02/Compose] LocalContext felülbírálás ContextWrapperrel ActivityResultRegistryOwner nélkül -> LocalContext felülírásakor mindig ContextWrappert kell használni és biztosítani a LocalActivityResultRegistryOwner-t, különben az activity result launcher-ek (pl. Settings engedélykérés) crashelnek.
[2026-10-02/UI] Edge-to-edge esetén egyedi bottomBar-ra rátakar a 3-gombos rendszer navigáció -> Egyedi bottomBar esetén kötelező a navigationBarsPadding() használata, hogy a rendszergombok (pl. Samsung 3-button nav) ne fedjék el az alsó menüpontokat.
