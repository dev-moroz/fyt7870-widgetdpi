#!/bin/zsh
# Build without Gradle: aapt2 + javac + d8 + apksigner.
# Usage: KS=path/to/keystore.jks KS_PASS=... ./build.sh   -> out/app.apk
set -e
cd "$(dirname $0)"
SDK=${ANDROID_HOME:-~/Library/Android/sdk}; BT=$SDK/build-tools/36.0.0; AJ=$SDK/platforms/android-35/android.jar
[ -d /opt/homebrew/opt/openjdk ] && export PATH=/opt/homebrew/opt/openjdk/bin:$PATH
rm -rf build out; mkdir -p build/cls out
$BT/aapt2 link -o build/base.apk -I $AJ --manifest AndroidManifest.xml
javac --release 8 -cp $AJ -d build/cls $(find src -name '*.java')
$BT/d8 --lib $AJ --min-api 26 --output build $(find build/cls -name '*.class')
cp build/base.apk build/u.apk; (cd build && zip -q u.apk classes.dex)
$BT/zipalign -f 4 build/u.apk build/a.apk
$BT/apksigner sign --ks "${KS:-$HOME/.android/debug.keystore}" --ks-pass env:KS_PASS --out out/app.apk build/a.apk
echo "built out/app.apk"
