#!/bin/bash

cd "$(dirname "$0")/.."

set -e  # fail là dừng ngay

echo "===> Running Spotless..."
./gradlew spotlessCheck --no-daemon

echo "===> Running Detekt..."
./gradlew detekt --no-daemon

echo "===> All checks passed!"