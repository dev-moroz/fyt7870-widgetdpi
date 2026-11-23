#!/bin/sh
adb uninstall com.mediacar.widgetdpi || true
adb shell settings delete global hidden_api_policy
adb shell wm density reset
echo "Откат выполнен. DPI виджетов темы вернётся к 160 после перезагрузки."
