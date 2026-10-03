# Adatvédelmi Nyilatkozat / Privacy Policy — BPJournal

**Utolsó frissítés / Last updated:** 2026-10-03  
**Alkalmazás / Application:** BPJournal: Vérnyomásnapló (Blood Pressure Log)  
**Csomagnév / Package Name:** `com.kaszast.bpjournal`  
**Fejlesztő / Developer:** kaszast  

---

## 🇭🇺 Magyar változat

### 1. Áttekintés
A BPJournal egy vérnyomásnaplózó alkalmazás, amelyet a felhasználók adatainak maximális védelmére terveztünk. Az alkalmazás **nem igényel regisztrációt**, **nem tartalmaz reklámokat**, és **nem küld személyes vagy egészségügyi adatokat külső szerverekre**.

### 2. Gyűjtött és kezelt adatok köre
Az alkalmazás kizárólag a felhasználó által explicit módon megadott mérési adatokat tárolja a készülék helyi adatbázisában (Room SQLite):
- Szisztolés (ST) és diasztolés (DST) vérnyomásértékek (Hgmm)
- Pulzusszám (BPM)
- Mérés időpontja, mérési kar (bal/jobb), testhelyzet (ülő/fekvő/álló), megjegyzések és címkék

### 3. Google Health Connect integráció
Amennyiben a felhasználó engedélyezi, a BPJournal a hivatalos Google Health Connect rendszeren keresztül:
- **Írja (Write):** az alkalmazásban rögzített vérnyomás- és pulzusrekordokat a Health Connect központi tárolójába.
- **Olvassa (Read):** a Health Connectben elérhető korábbi vérnyomás- és pulzusadatokat a helyi napló szinkronizálása céljából.
- **Megfelelőség:** A Health Connectből származó adatok kezelése teljes mértékben megfelel a [Google Health Connect Permissions policy](https://support.google.com/googleplay/android-developer/answer/9888379) előírásainak. Az adatokat harmadik félnek nem adjuk át, nem értékesítjük, és hirdetési célokra nem használjuk fel.

### 4. Adattárolás és biztonság
Minden adat kizárólag a felhasználó eszközének biztonságos, védett belső tárhelyén (Sandboxed Storage) tárolódik. Az adatok törlése a bejegyzések törlésével vagy az alkalmazás adatainak törlésével / eltávolításával azonnal és véglegesen megtörténik.

### 5. PDF és CSV Export
A felhasználó saját döntése alapján exportálhatja adatait PDF (orvosi lelet) vagy CSV fájlformátumba. Az exportált fájlok generálása helyben történik, azokat az alkalmazás semmilyen hálózaton keresztül nem továbbítja; megosztásuk kizárólag a rendszer szabványos megosztási párbeszédpaneljén keresztül, a felhasználó közvetlen jóváhagyásával lehetséges.

### 6. Kapcsolat
Adatvédelemmel kapcsolatos kérdésekben: GitHub tárolón keresztül (`https://github.com/kaszast/bpjournal`).

---

## 🇬🇧 English Version

### 1. Overview
BPJournal is a privacy-first blood pressure logging application. It **does not require account creation**, **contains no advertisements**, and **never transmits health or personal data to external servers**.

### 2. Types of Data Processed
Only data explicitly entered by the user is stored in the local on-device database (Room SQLite):
- Systolic (ST) and Diastolic (DST) blood pressure readings (mmHg)
- Heart rate / Pulse (BPM)
- Timestamp, measurement arm (left/right), body posture (sitting/lying/standing), notes, and contextual tags

### 3. Google Health Connect Integration
When permission is granted by the user, BPJournal integrates with Google Health Connect to:
- **Write:** sync user-recorded blood pressure and heart rate records to the Health Connect repository.
- **Read:** retrieve existing blood pressure and heart rate records to keep the local journal synchronized.
- **Compliance:** Use of information received from Health Connect adheres to the [Health Connect Permissions Policy](https://support.google.com/googleplay/android-developer/answer/9888379). Health data is never sold, shared with third parties, or used for advertising.

### 4. Data Storage and Security
All records remain strictly on the user’s local device within secure app sandboxed storage. Deletion of records inside the app or uninstalling the app permanently purges all stored information.

### 5. Data Export (PDF & CSV)
Medical PDF reports and CSV spreadsheets are rendered locally on the device. No data is sent over the network; distribution occurs solely via the Android system share sheet upon explicit user command.

### 6. Contact
For privacy-related inquiries, visit the official repository at `https://github.com/kaszast/bpjournal`.
