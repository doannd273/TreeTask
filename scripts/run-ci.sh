#!/bin/bash
cd "$(dirname "$0")/.."

set -e  # nếu lỗi thì dừng ngay

echo "=============================="
echo "Running Spotless Check"
echo "=============================="
./gradlew spotlessCheck
echo ""
echo "=============================="

echo "Running Detekt"
echo "=============================="
./gradlew detekt
echo ""
echo "=============================="

echo "Running Assemble Debug"
echo "=============================="
./gradlew assembleDebug
echo ""
echo "DONE: Code quality and build successful! Ready to push."