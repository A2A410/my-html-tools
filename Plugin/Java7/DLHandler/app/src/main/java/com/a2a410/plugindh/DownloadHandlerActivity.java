package com.a2a410.plugindh;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DownloadHandlerActivity extends Activity {

    private static final String TAG = "DLHandlerPlugin";
    private static final String SECRET_KEY = "jai";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String key = intent.getStringExtra("secret_key");

        if (!SECRET_KEY.equals(key)) {
            Log.e(TAG, "Unauthorized access: Invalid secret key");
            finish();
            return;
        }

        String action = intent.getAction();
        if ("com.a2a410.plugindh.ACTION_DOWNLOAD".equals(action)) {
            handleDownload(intent);
        } else {
            Log.e(TAG, "Unknown action: " + action);
            finish();
        }
    }

    private void handleDownload(Intent intent) {
        final String fileName = intent.getStringExtra("file_name") != null ?
                intent.getStringExtra("file_name") : "downloaded_file";
        final String mimeType = intent.getStringExtra("mime_type") != null ?
                intent.getStringExtra("mime_type") : "application/octet-stream";
        final String base64Data = intent.getStringExtra("file_data");

        if (base64Data == null) {
            Log.e(TAG, "No file data provided");
            finish();
            return;
        }

        // Run in background thread to avoid ANR
        new Thread(new DownloadRunnable(this, fileName, mimeType, base64Data)).start();
    }

    Uri saveWithMediaStore(String fileName, String mimeType, byte[] data) throws Exception {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        Uri fileUri = getContentResolver().insert(collection, values);

        if (fileUri != null) {
            try (OutputStream os = getContentResolver().openOutputStream(fileUri)) {
                if (os != null) {
                    os.write(data);
                    os.flush();
                }
            }
        }
        return fileUri;
    }

    Uri saveWithLegacyApi(String fileName, String mimeType, byte[] data) throws Exception {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        File outputFile = new File(downloadDir, fileName);
        // Basic duplicate handling
        int count = 1;
        String nameWithoutExt = fileName;
        String ext = "";
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) {
            nameWithoutExt = fileName.substring(0, dotIdx);
            ext = fileName.substring(dotIdx);
        }
        while (outputFile.exists()) {
            outputFile = new File(downloadDir, nameWithoutExt + "_" + count + ext);
            count++;
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(data);
            fos.flush();
        }

        // Register with DownloadManager
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            dm.addCompletedDownload(
                    outputFile.getName(),
                    "Downloaded via DLHandler",
                    true,
                    mimeType,
                    outputFile.getAbsolutePath(),
                    data.length,
                    true
            );
        }
        return Uri.fromFile(outputFile);
    }
}
