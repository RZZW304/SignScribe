#!/bin/bash

# Upload JAR to GitHub release

RELEASE_TAG="v1.0.0-alpha1"
JAR_FILE="build/libs/SignScribe-1.0.0.jar"
REPO="RZZW304/SignScribe"

echo "SignScribe v1.0.0 Alpha 1 Upload Script"
echo "======================================"
echo ""
echo "Tag: $RELEASE_TAG"
echo "JAR: $JAR_FILE"
echo "Repo: $REPO"
echo ""

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    echo "Run ./gradlew build first"
    exit 1
fi

echo "File size: $(ls -lh $JAR_FILE | awk '{print $5}')"
echo ""
echo "To upload this file to GitHub:"
echo ""
echo "1. Visit: https://github.com/$REPO/releases/edit/$RELEASE_TAG"
echo "2. Scroll to 'Binary files' section"
echo "3. Click 'Upload files'"
echo "4. Select: $(pwd)/$JAR_FILE"
echo "5. Click 'Update release'"
echo ""
echo "Alternative: Drag and drop the JAR file from:"
echo "  $(pwd)/$JAR_FILE"
echo "  to:"
echo "  https://github.com/$REPO/releases/edit/$RELEASE_TAG"
