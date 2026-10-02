# Lessons Learned
<!-- Formátum: [Dátum/Kontextus] Hiba -> Szabály -->
[2026-10-02/UI] Hiányzó funkcionális beállítások és hiányzó időpont-szerkesztés -> Beállításokban valódi állapotkezelő opciók kellenek; mérési naplózásnál az időpontnak mindig szerkeszthetőnek kell lennie.
[2026-10-02/Release] APK építés utáni kiadás -> Minden sikeres APK fordítás után automatikusan fel kell tölteni a GitHub Release-t a verziószám tag-el (v{version}) és csatolni kell az APK-t.
[2026-10-02/Compose] LocalContext felülbírálás ContextWrapperrel ActivityResultRegistryOwner nélkül -> LocalContext felülírásakor mindig ContextWrappert kell használni és biztosítani a LocalActivityResultRegistryOwner-t, különben az activity result launcher-ek (pl. Settings engedélykérés) crashelnek.
