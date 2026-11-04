# Quick Scanner 📄⚡

> **Free, unlimited, open-source document scanner for Android. Zero subscriptions, zero CPU hogging, zero cloud lock-in.**

Quick Scanner is an ultra-lightweight, high-performance alternative to proprietary document scanning apps (like Adobe Scan and CamScanner). Built with modern Android native architecture (**Kotlin + Jetpack Compose + Google ML Kit**), it delivers hardware-accelerated edge detection, perspective warping, and on-device OCR with zero latency and no quota limits.

---

## 🚀 Features vs Adobe Scan

| Feature | Adobe Scan | **Quick Scanner (Open Source)** |
| :--- | :---: | :---: |
| **Document Scanning** | ✅ | ✅ **Auto boundary detection & capture** |
| **Edge Crop & Perspective Warp** | ✅ | ✅ **Hardware-accelerated rectification** |
| **Shadow Removal & Cleanup** | ✅ | ✅ **B&W / Whiteboard / Auto-color filters** |
| **ID Card Mode** | Requires Premium | ✅ **Free 2-sided composite on 1 sheet** |
| **Book & Whiteboard Mode** | ✅ | ✅ **Included** |
| **On-Device OCR (Text Recognition)**| Quota/Paywall | ✅ **Unlimited, 100% offline & free** |
| **Export to PDF & JPEG** | Cloud sync req. | ✅ **Direct native PDF export (A4)** |
| **Scan Limits** | Capped on free tier | ✅ **Unlimited forever** |
| **Data Privacy & Telemetry** | Cloud uploaded | ✅ **100% local on-device storage** |
| **CPU & Battery Footprint** | Heavy background tasks | ✅ **Ultra-lean (< 35 MB RAM, 0 idle CPU)** |

---

## 🛠️ Architecture & Tech Stack

- **UI Framework**: Modern declarative UI with **Jetpack Compose (Material 3)**.
- **Vision Engine**: **Google ML Kit Document Scanner** for hardware-accelerated auto-capture, edge detection, and perspective transform.
- **On-Device OCR**: **ML Kit Vision Text Recognition** running fully on-device (zero network latency, zero API keys required).
- **Image Pipeline**: Native Android `Bitmap` manipulation with `ColorMatrix` GPU acceleration for B&W thresholding, whiteboard enhancement, and page rotation.
- **PDF Synthesis**: Native `android.graphics.pdf.PdfDocument` with page aspect preservation and compression.
- **Local Persistence**: **Room Database** for high-speed local document metadata and search queries.
- **Asynchronous Engine**: Kotlin Coroutines & Flow for 60fps stutter-free UI.

---

## 📂 Project Structure

```
QuickScanner/
├── .github/workflows/
│   └── android.yml            # Automated CI/CD building APK on push
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/quickscanner/app/
│   │   │   ├── QuickScannerApp.kt        # App lifecycle & Room initialization
│   │   │   ├── MainActivity.kt           # ML Kit Scanner integration & routing
│   │   │   ├── data/
│   │   │   │   ├── model/ScanDocument.kt # Room database entity
│   │   │   │   ├── dao/ScanDocumentDao.kt# Reactive document queries
│   │   │   │   └── database/AppDatabase.kt
│   │   │   ├── engine/
│   │   │   │   ├── DocScannerEngine.kt   # Hardware scanner client & options
│   │   │   │   ├── ImageProcessor.kt     # B&W, Whiteboard, ID card stitcher
│   │   │   │   ├── OcrEngine.kt          # Offline ML Kit TextRecognizer
│   │   │   │   └── PdfGenerator.kt       # Native A4 PDF document builder
│   │   │   └── ui/
│   │   │       ├── navigation/           # Compose NavGraph
│   │   │       ├── screens/
│   │   │       │   ├── HomeScreen.kt     # Document catalog & search
│   │   │       │   ├── DocumentViewerScreen.kt # Page viewer & filter editor
│   │   │       │   ├── IdCardScannerScreen.kt  # Guided dual-sided scanner
│   │   │       │   └── OcrResultScreen.kt      # Extracted text & copy
│   │   │       └── theme/
│   │   └── res/                          # XML resources, strings, provider paths
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml                # Modern version catalog
├── build.gradle.kts
└── settings.gradle.kts
```

---

## ⚡ How to Build & Run

### 1. Open in Android Studio
1. Clone this repository:
   ```bash
   git clone <repo-url>
   ```
2. Open Android Studio (Ladybug / Koala / Hedgehog or newer).
3. Select **File > Open** and choose the `Scanning software` directory.
4. Allow Gradle to sync dependencies automatically.
5. Press **Run (Shift + F10)** on a connected physical device or emulator.

### 2. Build APK via Command Line
```bash
# Windows
.\gradlew assembleDebug

# Linux / macOS
./gradlew assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🔒 Privacy & Permissions

- **Camera**: Used exclusively for capturing documents when user initiates scanning.
- **Storage / Media**: Used to import images from gallery or export PDFs to device storage.
- **Network**: **Zero internet connection required.** Scans and OCR models run 100% locally on your phone.

---

## 📄 License

Licensed under the [MIT License](LICENSE). Free for personal and commercial distribution.

