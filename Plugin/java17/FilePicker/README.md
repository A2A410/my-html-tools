# FilePicker Plugin

A standalone Android plugin for picking and saving files, designed for use with HTML-based applications.

## Package Name
`com.a2a410.pluginfp`

## Usage

### Caller Security
- The exported activity is protected by a signature-level permission (`com.a2a410.pluginfp.ACCESS_PLUGIN`).
- Caller app must be signed with the same certificate and declare `uses-permission` for this permission.
- Existing `secret_key` extra is still required as an additional app-level check.


### Secret Key
All Intents must include the following extra:
- `secret_key`: `"jai"`

### 1. Pick a File
**Action:** `com.a2a410.pluginfp.ACTION_PICK`

**Extras:**
- `mime_type` (String, optional): The MIME type to filter (e.g., `"image/*"`, `"application/pdf"`). Defaults to `"*/*"`.
- `return_type` (String, optional): `"uri"` (default) or `"base64"`.
- `callback_action` (String, optional): Action for a broadcast result.

**Response:**
- `uri` (String): The URI of the chosen file.
- `file_data` (String): Base64 encoded data (if `return_type` was `"base64"`).

### 2. Save a File
**Action:** `com.a2a410.pluginfp.ACTION_SAVE`

**Extras:**
- `file_name` (String): The suggested name for the file.
- `mime_type` (String): The MIME type of the file.
- `file_data` (String): The Base64 encoded content of the file.
- `callback_action` (String, optional): Action for a broadcast result.

**Response:**
- `status`: `"success"`
- `uri`: The URI where the file was saved.

## Implementation Details
- Targets Android 8.0+ (API 26)
- Uses Storage Access Framework (SAF)
- No launcher icon


## Build Requirements
- JDK 17 to 21 (recommended).
- Android SDK installed and configured via `ANDROID_HOME` or `local.properties` (`sdk.dir=...`).
- This project intentionally avoids machine-specific `org.gradle.java.home` so builds remain portable across environments.
