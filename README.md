# WidgetDPI — fix tiny apps in FYT 7870 theme widgets

Нормальный масштаб приложений во встроенных виджетах штатных тем FYT UIS7870 (Android 13). Без root.

Штатные темы (`launcher_v2` и др.) показывают карту/плеер во встроенных окнах на отдельных виртуальных дисплеях `Embedded_Task@…` с DPI 160. Общий масштаб и `wm density` на них не действуют, поэтому всё мелкое.

## Возможности
- Держит DPI 300 на дисплеях `Embedded_Task*` и на основном экране.
- Следит за пересозданием дисплеев (смена темы, сон по ACC) и сразу возвращает значение.
- Автозапуск при включении (~15 с после старта).

## Установка
Один раз нужен ADB — с ПК или прямо с магнитолы через LADB / Shizuku.
```
adb install -r widgetdpi.apk
adb shell settings put global hidden_api_policy 1
adb shell pm grant com.mediacar.widgetdpi android.permission.WRITE_SECURE_SETTINGS
```
Затем открыть приложение один раз. Или запустить `install.sh`.

## Откат
`uninstall.sh`, или:
```
adb uninstall com.mediacar.widgetdpi
adb shell settings delete global hidden_api_policy
adb shell wm density reset
```

## Проверить вручную без приложения
```
adb shell dumpsys display | grep Embedded_Task
adb shell wm density 300 -d <id>
```
Сбрасывается после перезагрузки — для этого и нужно приложение.

## Как это работает
`DisplayManager.DisplayListener` + `IWindowManager.setForcedDisplayDensityForUser(displayId, 300, userId)` через рефлексию. Нужны `WRITE_SECURE_SETTINGS` (выдаётся `pm grant`) и `hidden_api_policy=1`.

## Сборка
```
KS=my.jks KS_PASS=... ./build.sh
```

## Проверено
FYT UIS7870 6/128, Android 13 (сборка 2026-05-08), тема launcher_v2. Без гарантий.

## Ограничения
DPI (300) задан в коде (`DensityFixer.java`, `TARGET`). Ручной масштаб основного экрана приложение перезапишет.

Лицензия: MIT.
