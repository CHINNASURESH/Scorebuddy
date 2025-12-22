#!/bin/bash
echo "Resetting working tree to HEAD..."
git reset --hard HEAD

echo "Cleaning build artifacts..."
rm -rf .gradle build local.properties

echo "Generating Gradle Wrapper..."
gradle wrapper

echo "Setup complete."
