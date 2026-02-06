package com.a2a410.pluginfp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class FilePickerActivity extends Activity {

    private static final String TAG = "FilePickerPlugin";
    private static final String SECRET_KEY = "jai";

    private static final int REQUEST_CODE_PICK = 1001;
    private static final int REQUEST_CODE_SAVE = 1002;

    private String callbackAction;
    private String returnType; // "uri" or "base64"

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
        callbackAction = intent.getStringExtra("callback_action");
        returnType = intent.getStringExtra("return_type");
        if (returnType == null) returnType = "uri";

        if ("com.a2a410.pluginfp.ACTION_PICK".equals(action)) {
            handlePickRequest(intent);
        } else if ("com.a2a410.pluginfp.ACTION_SAVE".equals(action)) {
            handleSaveRequest(intent);
        } else {
            Log.e(TAG, "Unknown action: " + action);
            finish();
        }
    }

    private void handlePickRequest(Intent intent) {
        String mimeType = intent.getStringExtra("mime_type");
        if (mimeType == null) mimeType = "*/*";

        Intent pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
        pickIntent.setType(mimeType);

        try {
            startActivityForResult(pickIntent, REQUEST_CODE_PICK);
        } catch (Exception e) {
            Log.e(TAG, "Error starting file picker", e);
            finish();
        }
    }

    private void handleSaveRequest(Intent intent) {
        String fileName = intent.getStringExtra("file_name");
        String mimeType = intent.getStringExtra("mime_type");
        if (mimeType == null) mimeType = "application/octet-stream";
        if (fileName == null) fileName = "downloaded_file";

        Intent saveIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        saveIntent.addCategory(Intent.CATEGORY_OPENABLE);
        saveIntent.setType(mimeType);
        saveIntent.putExtra(Intent.EXTRA_TITLE, fileName);

        try {
            startActivityForResult(saveIntent, REQUEST_CODE_SAVE);
        } catch (Exception e) {
            Log.e(TAG, "Error starting save picker", e);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            Log.i(TAG, "Operation cancelled by user");
            finish();
            return;
        }

        final Uri uri = data.getData();

        if (requestCode == REQUEST_CODE_PICK) {
            new Thread(() -> processPickResult(uri)).start();
        } else if (requestCode == REQUEST_CODE_SAVE) {
            new Thread(() -> processSaveResult(uri)).start();
        }
    }

    private void processPickResult(Uri uri) {
        final Intent resultIntent = new Intent();
        resultIntent.putExtra("uri", uri.toString());

        if ("base64".equals(returnType)) {
            try {
                String base64Data = readUriAsBase64(uri);
                resultIntent.putExtra("file_data", base64Data);
            } catch (IOException e) {
                Log.e(TAG, "Error reading file data", e);
                runOnUiThread(() -> {
                    setResult(RESULT_CANCELED);
                    finish();
                });
                return;
            }
        }

        runOnUiThread(() -> {
            setResult(RESULT_OK, resultIntent);

            if (callbackAction != null) {
                Intent broadcast = new Intent(callbackAction);
                broadcast.putExtras(resultIntent);
                sendBroadcast(broadcast);
            }
            finish();
        });
    }

    private void processSaveResult(Uri uri) {
        String base64Data = getIntent().getStringExtra("file_data");
        if (base64Data == null) {
            Log.e(TAG, "No file data provided for save action");
            runOnUiThread(this::finish);
            return;
        }

        try {
            try (InputStream encodedInput = new java.io.ByteArrayInputStream(base64Data.getBytes(StandardCharsets.US_ASCII));
                 InputStream decodedInput = new Base64InputStream(encodedInput, Base64.DEFAULT);
                 OutputStream os = getContentResolver().openOutputStream(uri)) {
                if (os == null) {
                    throw new IOException("Unable to open destination URI for writing");
                }

                byte[] buffer = new byte[8192];
                int read;
                while ((read = decodedInput.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }

            final Intent resultIntent = new Intent();
            resultIntent.putExtra("status", "success");
            resultIntent.putExtra("uri", uri.toString());

            runOnUiThread(() -> {
                setResult(RESULT_OK, resultIntent);
                if (callbackAction != null) {
                    Intent broadcast = new Intent(callbackAction);
                    broadcast.putExtras(resultIntent);
                    sendBroadcast(broadcast);
                }
                finish();
            });

        } catch (Exception e) {
            Log.e(TAG, "Error saving file data", e);
            runOnUiThread(() -> {
                setResult(RESULT_CANCELED);
                finish();
            });
        }
    }

    private String readUriAsBase64(Uri uri) throws IOException {
        try (InputStream is = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Base64OutputStream b64os = new Base64OutputStream(baos, Base64.NO_WRAP)) {
            if (is == null) {
                throw new IOException("Unable to open source URI for reading");
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                b64os.write(buffer, 0, bytesRead);
            }
            b64os.flush();
            return baos.toString(StandardCharsets.US_ASCII.name());
        }
    }
}
