# DLHandler Plugin

A standalone Android plugin for handling downloads (saving blobs to the Downloads folder), designed for use with HTML-based applications.

## Package Name
`com.a2a410.plugindh`

## Usage

### Secret Key
All Intents must include the following extra:
- `secret_key`: `"jai"`

### 1. Download/Save a File
**Action:** `com.a2a410.plugindh.ACTION_DOWNLOAD`

**Extras:**
- `file_name` (String): The name of the file to save (e.g., `"report.pdf"`).
- `mime_type` (String): The MIME type of the file.
- `file_data` (String): The Base64 encoded content of the file.

**Behavior:**
- The plugin decodes the Base64 data.
- Saves it to the device's public `Downloads` folder.
- Registers the file with the Android `DownloadManager` so it appears in the system downloads list.
- Shows a Toast notification on success/failure.

**Response (via Activity Result):**
- `status`: `"success"`
- `uri`: The URI or path to the saved file.

## Implementation Details
- Targets Android 8.0+ (API 26)
- Uses `DownloadManager.addCompletedDownload`
- Supports `requestLegacyExternalStorage` for compatibility with Android 10
- No launcher icon
