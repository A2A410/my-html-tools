# Plugin System

This directory contains standalone Android "Plugin" apps designed to extend the functionality of WebView-based applications.

## Plugins Included
- **[FilePicker](./FilePicker/README.md)**: `com.a2a410.pluginfp` - Handles picking and saving files via SAF.
- **[DLHandler](./DLHandler/README.md)**: `com.a2a410.plugindh` - Processes Base64 blobs and saves them to the Downloads folder.

## General Usage Principles

### 1. Authorization
Every Intent sent to these plugins **must** include a secret key extra for security:
- **Key:** `secret_key`
- **Value:** `jai`

If this key is missing or incorrect, the plugin will terminate immediately without performing any action.

### 2. Integration Example (Java)

To call a plugin from your main Android app:

```java
Intent intent = new Intent("com.a2a410.pluginfp.ACTION_PICK");
intent.setPackage("com.a2a410.pluginfp"); // Explicit intent
intent.putExtra("secret_key", "jai");
intent.putExtra("mime_type", "image/*");
intent.putExtra("return_type", "base64");

startActivityForResult(intent, MY_REQUEST_CODE);
```

### 3. Handling Results
Results can be received via `onActivityResult`:

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK && data != null) {
        String uri = data.getStringExtra("uri");
        String base64 = data.getStringExtra("file_data");
        // Use the data...
    }
}
```

Alternatively, you can specify a `callback_action` extra to receive the result via a `BroadcastReceiver`.

## Installation
Build and install each APK separately. They will not appear in the app drawer (no launcher icon), but will be available for other apps to call via the specified Intent actions.
