#!/bin/bash
cd "$(dirname "$0")/.."

set -e  # nếu lỗi thì dừng ngay

echo "=============================="
echo "Running Spotless Check"
echo "=============================="
./gradlew spotlessCheck --no-daemon
echo ""
echo "=============================="

echo "Running Detekt"
echo "=============================="
./gradlew detekt --no-daemon
echo ""
echo "=============================="

echo "Running Assemble Debug"
echo "=============================="
./gradlew assembleDebug --no-daemon
echo ""
echo "DONE: Code quality and build successful! Ready to push."