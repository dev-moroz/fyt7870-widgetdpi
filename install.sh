#!/bin/sh
# WidgetDPI для FYT 7870 (Android 13), без root. Перед запуском: adb connect <IP>:<порт>
set -e
cd "$(dirname "$0")"
adb install -r widgetdpi.apk
adb shell settings put global hidden_api_policy 1
adb shell pm grant com.mediacar.widgetdpi android.permission.WRITE_SECURE_SETTINGS
adb shell monkey -p com.mediacar.widgetdpi -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1 || true
echo "Готово."
