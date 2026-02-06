package com.a2a410.plugindh;

import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

class DownloadRunnable implements Runnable {
    private static final String TAG = "DLHandlerPlugin";
    private DownloadHandlerActivity activity;
    private String fileName;
    private String mimeType;
    private String base64Data;

    public DownloadRunnable(DownloadHandlerActivity activity, String fileName, String mimeType, String base64Data) {
        this.activity = activity;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.base64Data = base64Data;
    }

    @Override
    public void run() {
        try {
            final byte[] data = Base64.decode(base64Data, Base64.DEFAULT);
            Uri savedUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savedUri = activity.saveWithMediaStore(fileName, mimeType, data);
            } else {
                savedUri = activity.saveWithLegacyApi(fileName, mimeType, data);
            }

            if (savedUri != null) {
                final String uriString = savedUri.toString();
                activity.runOnUiThread(new ResultRunnable(activity, "File saved successfully", uriString, true));
            } else {
                throw new Exception("Failed to save file");
            }

        } catch (final Exception e) {
            Log.e(TAG, "Error handling download", e);
            activity.runOnUiThread(new ResultRunnable(activity, "Download failed: " + e.getMessage(), null, false));
        }
    }
}
