#!/bin/bash
# Run unit tests for Kid Safe Launcher
#
# Requirements:
#   - Android SDK with platform android-35
#   - Java 11+ (JDK)
#   - JUnit 4.13.2 and Hamcrest Core 1.3 jars in test_deps/
#
# Usage:
#   ./test.sh

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SDK_DIR="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-/usr/local/lib/android/sdk}}"
PLATFORM="$SDK_DIR/platforms/android-35"
BUILD_DIR="$PROJECT_DIR/app/build/manual"
TEST_BUILD="$PROJECT_DIR/app/build/test_classes"
TEST_DEPS="$PROJECT_DIR/test_deps"

echo "🧪 Kid Safe Launcher - Test Runner"
echo "===================================="

# Check for test dependencies
if [ ! -f "$TEST_DEPS/junit-4.13.2.jar" ]; then
    echo "📦 Downloading test dependencies..."
    mkdir -p "$TEST_DEPS"
    curl -sL "https://repo.maven.apache.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar" -o "$TEST_DEPS/junit-4.13.2.jar"
    curl -sL "https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" -o "$TEST_DEPS/hamcrest-core-1.3.jar"
fi

# Ensure app is compiled
if [ ! -d "$BUILD_DIR/classes" ]; then
    echo "⚠️  App classes not found. Run build.sh first."
    exit 1
fi

echo "☕ Compiling tests..."
rm -rf "$TEST_BUILD"
mkdir -p "$TEST_BUILD"

TEST_CP="$PLATFORM/android.jar:$TEST_DEPS/junit-4.13.2.jar:$TEST_DEPS/hamcrest-core-1.3.jar:$BUILD_DIR/classes"

find "$PROJECT_DIR/app/src/test/java" -name "*.java" > /tmp/test_sources.txt
javac -source 11 -target 11 -classpath "$TEST_CP" -d "$TEST_BUILD" @/tmp/test_sources.txt -Xlint:none

echo "🏃 Running tests..."
echo ""

RUN_CP="$PLATFORM/android.jar:$TEST_DEPS/junit-4.13.2.jar:$TEST_DEPS/hamcrest-core-1.3.jar:$BUILD_DIR/classes:$TEST_BUILD"

TEST_CLASSES=(
    "com.kidsafe.launcher.models.AppInfoTest"
    "com.kidsafe.launcher.models.DeviceInfoTest"
    "com.kidsafe.launcher.models.ScreenSizeTest"
    "com.kidsafe.launcher.utils.AppUtilsTest"
    "com.kidsafe.launcher.utils.ScreenUtilsTest"
    "com.kidsafe.launcher.utils.DeviceUtilsTest"
    "com.kidsafe.launcher.utils.NetworkUtilsTest"
    "com.kidsafe.launcher.adapters.AppGridAdapterTest"
    "com.kidsafe.launcher.adapters.AppManageAdapterTest"
    "com.kidsafe.launcher.receivers.PackageChangeReceiverTest"
)

TOTAL=0; PASSED=0; FAILED=0

for tc in "${TEST_CLASSES[@]}"; do
    output=$(java -cp "$RUN_CP" org.junit.runner.JUnitCore "$tc" 2>&1)
    if echo "$output" | grep -q "OK"; then
        count=$(echo "$output" | grep -oP '\d+ tests?' | head -1 | grep -oP '\d+')
        PASSED=$((PASSED + count)); TOTAL=$((TOTAL + count))
        echo "✅ $tc: $count tests passed"
    elif echo "$output" | grep -q "FAILURES"; then
        run=$(echo "$output" | grep -oP 'Tests run: \d+' | grep -oP '\d+')
        fail=$(echo "$output" | grep -oP 'Failures: \d+' | grep -oP '\d+')
        TOTAL=$((TOTAL + run)); FAILED=$((FAILED + fail)); PASSED=$((PASSED + run - fail))
        echo "❌ $tc: $fail/$run failed"
        echo "$output" | grep -A3 "^[0-9]\+)"
    fi
done

echo ""
echo "============================="
echo "📊 Total: $TOTAL | ✅ Passed: $PASSED | ❌ Failed: $FAILED"
if [ $TOTAL -gt 0 ]; then
    echo "📈 Pass Rate: $(( (PASSED * 100) / TOTAL ))%"
fi
echo "============================="

if [ $FAILED -gt 0 ]; then
    exit 1
fi
