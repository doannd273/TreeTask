#!/bin/bash

cd "$(dirname "$0")/.."

set -e

echo "===> Running Spotless Apply..."

./gradlew spotlessApply

echo "===> Spotless formatting applied successfully!"