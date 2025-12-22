#!/bin/bash
set -e

echo "Resetting working tree to HEAD..."
git reset --hard HEAD

echo "Cleaning build artifacts..."
rm -rf .gradle build local.properties

# Set up Android SDK
export ANDROID_HOME=$PWD/android-sdk
export CMDLINE_TOOLS_ROOT=$ANDROID_HOME/cmdline-tools
export PATH=$CMDLINE_TOOLS_ROOT/latest/bin:$PATH
export PATH=$ANDROID_HOME/platform-tools:$PATH

if [ ! -d "$ANDROID_HOME" ]; then
    echo "Installing Android SDK..."
    mkdir -p $CMDLINE_TOOLS_ROOT

    # Download
    ZIP_FILE=commandlinetools-linux-13114758_latest.zip
    URL=https://dl.google.com/android/repository/$ZIP_FILE

    echo "Downloading $URL..."
    curl -o $ZIP_FILE $URL

    echo "Unzipping..."
    unzip -q $ZIP_FILE -d $CMDLINE_TOOLS_ROOT
    rm $ZIP_FILE

    # Move to 'latest' as required by sdkmanager
    mv $CMDLINE_TOOLS_ROOT/cmdline-tools $CMDLINE_TOOLS_ROOT/latest

    echo "Accepting licenses..."
    yes | sdkmanager --licenses

    echo "Installing platform tools and SDK..."
    # Build Tools 33.0.1 was installed by Gradle automatically or defaults?
    # I'll install a specific version to be safe.
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
else
    echo "Android SDK already found at $ANDROID_HOME"
fi

# Create local.properties
echo "sdk.dir=$ANDROID_HOME" > local.properties

echo "Generating Gradle Wrapper..."
gradle wrapper

echo "Setup complete. You can now run ./gradlew assembleDebug"
