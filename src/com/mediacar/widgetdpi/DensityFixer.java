package com.mediacar.widgetdpi;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import java.lang.reflect.Method;

/** Forces density TARGET on the main display and Embedded_Task virtual displays. */
public class DensityFixer implements DisplayManager.DisplayListener {
    static final String TAG = "volpanel";
    static final int TARGET = 300;
    static final int USER_CURRENT = -2;
    private final DisplayManager dm;
    private final Handler h;

    public DensityFixer(Context c, Handler h) {
        this.dm = (DisplayManager) c.getSystemService(Context.DISPLAY_SERVICE);
        this.h = h;
    }

    public void start() {
        dm.registerDisplayListener(this, h);
        for (Display d : dm.getDisplays()) check(d.getDisplayId());
    }

    public void rescan() { for (Display d : dm.getDisplays()) check(d.getDisplayId()); }

    public void stop() { try { dm.unregisterDisplayListener(this); } catch (Throwable t) {} }

    void check(int id) {
        try {
            Display d = dm.getDisplay(id);
            if (d == null || (id != Display.DEFAULT_DISPLAY && (d.getName() == null || !d.getName().startsWith("Embedded_Task")))) return;
            android.util.DisplayMetrics m = new android.util.DisplayMetrics();
            d.getRealMetrics(m);
            if (m.densityDpi == TARGET) return;
            Log.i(TAG, "display " + id + " " + d.getName() + " density " + m.densityDpi + " -> " + TARGET);
            Class<?> sm = Class.forName("android.os.ServiceManager");
            IBinder b = (IBinder) sm.getMethod("getService", String.class).invoke(null, "window");
            Class<?> stub = Class.forName("android.view.IWindowManager$Stub");
            Object wm = stub.getMethod("asInterface", IBinder.class).invoke(null, b);
            Method f = Class.forName("android.view.IWindowManager")
                .getMethod("setForcedDisplayDensityForUser", int.class, int.class, int.class);
            f.invoke(wm, id, TARGET, android.os.Process.myUserHandle().hashCode()); // own user id: -2 needs INTERACT_ACROSS_USERS_FULL
        } catch (Throwable t) { Log.e(TAG, "density fix failed for " + id, t); }
    }

    @Override public void onDisplayAdded(int id) { check(id); }
    @Override public void onDisplayChanged(int id) { check(id); }
    @Override public void onDisplayRemoved(int id) {}
}
