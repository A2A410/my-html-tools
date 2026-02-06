package com.a2a410.plugindh;

import android.content.Intent;
import android.widget.Toast;
import static android.app.Activity.RESULT_OK;
import static android.app.Activity.RESULT_CANCELED;

class ResultRunnable implements Runnable {
    private DownloadHandlerActivity activity;
    private String message;
    private String uri;
    private boolean success;

    public ResultRunnable(DownloadHandlerActivity activity, String message, String uri, boolean success) {
        this.activity = activity;
        this.message = message;
        this.uri = uri;
        this.success = success;
    }

    @Override
    public void run() {
        Toast.makeText(activity, message, success ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
        if (success) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("status", "success");
            resultIntent.putExtra("uri", uri);
            activity.setResult(RESULT_OK, resultIntent);
        } else {
            activity.setResult(RESULT_CANCELED);
        }
        activity.finish();
    }
}
