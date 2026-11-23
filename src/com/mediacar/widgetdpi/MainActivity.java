package com.mediacar.widgetdpi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        try { startForegroundService(new Intent(this, FixService.class)); } catch (Throwable t) {}
        finish();
    }
}
