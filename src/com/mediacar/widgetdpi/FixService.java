package com.mediacar.widgetdpi;

import android.app.*;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class FixService extends Service {
    DensityFixer fixer;
    final android.content.BroadcastReceiver wake = new android.content.BroadcastReceiver() {
        @Override public void onReceive(android.content.Context c, Intent i) {
            if (fixer != null) fixer.rescan();
        }
    };
    @Override public IBinder onBind(Intent i) { return null; }
    @Override public int onStartCommand(Intent i, int f, int id) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(new NotificationChannel("widgetdpi", "Widget DPI", NotificationManager.IMPORTANCE_MIN));
        startForeground(2, new Notification.Builder(this, "widgetdpi")
            .setSmallIcon(android.R.drawable.ic_menu_manage).setContentTitle("Widget DPI").setOngoing(true).build());
        if (fixer == null) {
            android.content.IntentFilter flt = new android.content.IntentFilter(Intent.ACTION_SCREEN_ON);
            flt.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(wake, flt); fixer = new DensityFixer(this, new Handler(Looper.getMainLooper())); fixer.start(); }
        else fixer.rescan();
        return START_STICKY;
    }
    @Override public void onDestroy() { try { unregisterReceiver(wake); } catch (Throwable t) {} if (fixer != null) fixer.stop(); super.onDestroy(); }
}
