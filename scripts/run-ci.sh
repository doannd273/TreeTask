#!/bin/bash

cd "$(dirname "$0")/.."

set -e  # nếu lỗi thì dừng ngay

echo "=============================="
echo "🚀 Running Gradle Check"
echo "=============================="

./gradlew check

echo ""
echo "=============================="
echo "🔨 Running Assemble Debug"
echo "=============================="

./gradlew assembleDebug

echo ""
echo "🎉 DONE: check + build successful!"