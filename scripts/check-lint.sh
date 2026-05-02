#!/bin/bash

cd "$(dirname "$0")/.."

set -e  # fail là dừng ngay

echo "===> Running Spotless..."
./gradlew spotlessCheck

echo "===> Running Detekt..."
./gradlew detekt

echo "===> All checks passed!"