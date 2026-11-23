# WidgetDPI — fix tiny apps in FYT 7870 theme widgets

Proper scaling for apps shown inside the built-in widgets of stock FYT UIS7870 themes (Android 13). No root.

Stock themes (`launcher_v2` and others) render the map / player widgets on separate virtual displays `Embedded_Task@…` with DPI 160. The global display size setting and `wm density` don't affect them, so everything inside looks tiny.

## Features
- Keeps DPI 300 on `Embedded_Task*` displays and on the main display.
- Watches for the displays being recreated (theme switch, ACC sleep/wake) and reapplies the value immediately.
- Starts automatically on boot (~15 s after power-on).

## Install
ADB is needed once — from a PC or directly on the head unit via LADB / Shizuku.
```
adb install -r widgetdpi.apk
adb shell settings put global hidden_api_policy 1
adb shell pm grant com.mediacar.widgetdpi android.permission.WRITE_SECURE_SETTINGS
```
Then open the app once. Or just run `install.sh`.

## Uninstall / rollback
Run `uninstall.sh`, or:
```
adb uninstall com.mediacar.widgetdpi
adb shell settings delete global hidden_api_policy
adb shell wm density reset
```

## Try it manually without the app
```
adb shell dumpsys display | grep Embedded_Task
adb shell wm density 300 -d <id>
```
This resets after a reboot — that's what the app is for.

## How it works
`DisplayManager.DisplayListener` + `IWindowManager.setForcedDisplayDensityForUser(displayId, 300, userId)` via reflection. Requires `WRITE_SECURE_SETTINGS` (granted with `pm grant`) and `hidden_api_policy=1`.

## Build
```
KS=my.jks KS_PASS=... ./build.sh
```

## Tested on
FYT UIS7870 6/128, Android 13 (build date 2026-05-08), theme launcher_v2. No warranty.

## Limitations
DPI (300) is hardcoded in `DensityFixer.java` (`TARGET`). The app will override any manual main display density.

License: MIT.
