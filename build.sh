#!/bin/bash
# Build script for Kid Safe Launcher APK
# Uses Android SDK build tools directly (no Gradle/AGP dependency)
#
# Requirements:
#   - Android SDK with build-tools 35.0.0 and platform android-35
#   - Java 11+ (JDK)
#   - ANDROID_HOME or ANDROID_SDK_ROOT environment variable set
#
# Usage:
#   ./build.sh          # Build debug APK
#   ./build.sh release  # Build release APK

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SDK_DIR="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-/usr/local/lib/android/sdk}}"
BUILD_TOOLS="$SDK_DIR/build-tools/35.0.0"
PLATFORM="$SDK_DIR/platforms/android-35"
BUILD_DIR="$PROJECT_DIR/app/build/manual"
SRC_DIR="$PROJECT_DIR/app/src/main"
BUILD_TYPE="${1:-debug}"

echo "🔧 Kid Safe Launcher - Build Script"
echo "===================================="
echo "Build type: $BUILD_TYPE"
echo "SDK: $SDK_DIR"
echo ""

# Validate SDK
if [ ! -d "$PLATFORM" ]; then
    echo "❌ Android platform not found at $PLATFORM"
    exit 1
fi
if [ ! -d "$BUILD_TOOLS" ]; then
    echo "❌ Build tools not found at $BUILD_TOOLS"
    exit 1
fi

# Clean
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"/{gen,classes,dex,apk}

echo "📦 Step 1: Compile Resources..."
$BUILD_TOOLS/aapt2 compile --dir "$SRC_DIR/res" -o "$BUILD_DIR/compiled_res.zip"

echo "🔗 Step 2: Link Resources..."
$BUILD_TOOLS/aapt2 link \
    -o "$BUILD_DIR/apk/base.apk" \
    -I "$PLATFORM/android.jar" \
    --manifest "$SRC_DIR/AndroidManifest.xml" \
    --java "$BUILD_DIR/gen" \
    --auto-add-overlay \
    "$BUILD_DIR/compiled_res.zip"

echo "☕ Step 3: Compile Java..."
find "$SRC_DIR/java" -name "*.java" > "$BUILD_DIR/sources.txt"
find "$BUILD_DIR/gen" -name "*.java" >> "$BUILD_DIR/sources.txt"

javac \
    -source 11 -target 11 \
    -classpath "$PLATFORM/android.jar" \
    -d "$BUILD_DIR/classes" \
    @"$BUILD_DIR/sources.txt" \
    -Xlint:none

echo "📱 Step 4: Create DEX..."
$BUILD_TOOLS/d8 \
    --lib "$PLATFORM/android.jar" \
    --min-api 26 \
    --output "$BUILD_DIR/dex" \
    $(find "$BUILD_DIR/classes" -name "*.class")

echo "📋 Step 5: Package APK..."
cp "$BUILD_DIR/apk/base.apk" "$BUILD_DIR/apk/unsigned.apk"
cd "$BUILD_DIR/dex"
zip -uj "$BUILD_DIR/apk/unsigned.apk" classes.dex > /dev/null 2>&1
cd "$PROJECT_DIR"

echo "📐 Step 6: Align APK..."
$BUILD_TOOLS/zipalign -f 4 "$BUILD_DIR/apk/unsigned.apk" "$BUILD_DIR/apk/aligned.apk"

echo "🔑 Step 7: Sign APK..."
KEYSTORE="$BUILD_DIR/debug.keystore"
if [ ! -f "$KEYSTORE" ]; then
    keytool -genkeypair \
        -dname "CN=Debug, OU=Debug, O=KidSafe, L=Debug, ST=Debug, C=US" \
        -keystore "$KEYSTORE" \
        -keypass android \
        -storepass android \
        -alias androiddebugkey \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        2>/dev/null
fi

OUTPUT_APK="$PROJECT_DIR/app/build/KidSafeLauncher-${BUILD_TYPE}.apk"
mkdir -p "$(dirname "$OUTPUT_APK")"

$BUILD_TOOLS/apksigner sign \
    --ks "$KEYSTORE" \
    --ks-key-alias androiddebugkey \
    --ks-pass pass:android \
    --key-pass pass:android \
    --out "$OUTPUT_APK" \
    "$BUILD_DIR/apk/aligned.apk"

echo ""
echo "✅ Build Complete!"
echo "📱 APK: $OUTPUT_APK"
echo "📊 Size: $(du -h "$OUTPUT_APK" | cut -f1)"
