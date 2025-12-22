#!/bin/bash
set -e # Exit immediately if a command exits with a non-zero status.

# --- Configuration ---
# You can change these versions based on your project's compileSdkVersion and buildToolsVersion
ANDROID_COMPILE_SDK="34"          # Matches compileSdk in build.gradle
ANDROID_BUILD_TOOLS="34.0.0"      # Matches buildToolsVersion in build.gradle
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"

# Define Paths
export ANDROID_HOME="$HOME/Android/sdk"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

echo "🛠️  Starting Android Environment Setup for Jules..."

# 1. Install Java (if not already present or if specific version needed)
# Jules VMs usually have Java, but ensuring the correct version for Gradle is good practice.
# Uncomment the line below if you need a specific JDK version (e.g., openjdk-17-jdk)
# sudo apt-get update && sudo apt-get install -y openjdk-17-jdk

# 2. Setup Android SDK Directory
if [ ! -d "$ANDROID_HOME/cmdline-tools" ]; then
    echo "📦 Downloading Android Command Line Tools..."
    mkdir -p "$ANDROID_HOME/cmdline-tools"
    
    # Download and unzip tools
    wget -q "$CMDLINE_TOOLS_URL" -O /tmp/cmdline-tools.zip
    unzip -q /tmp/cmdline-tools.zip -d /tmp/cmdline-tools-temp
    
    # Move to the correct structure: $ANDROID_HOME/cmdline-tools/latest/bin
    mkdir -p "$ANDROID_HOME/cmdline-tools/latest"
    mv /tmp/cmdline-tools-temp/cmdline-tools/* "$ANDROID_HOME/cmdline-tools/latest/"
    
    # Cleanup
    rm /tmp/cmdline-tools.zip
    rm -rf /tmp/cmdline-tools-temp
    echo "✅ Command Line Tools installed."
else
    echo "ℹ️  Android SDK already detected. Skipping download."
fi

# 3. Accept Licenses and Install SDK Components
echo "📜 Accepting Licenses and Installing SDK Components..."
yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses > /dev/null 2>&1

echo "📦 Installing Platforms and Build Tools..."
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" \
    "platform-tools" \
    "platforms;android-$ANDROID_COMPILE_SDK" \
    "build-tools;$ANDROID_BUILD_TOOLS" > /dev/null

# 4. Create local.properties
# Gradle requires this file to know where the SDK is located
if [ ! -f "local.properties" ]; then
    echo "📝 Creating local.properties..."
    echo "sdk.dir=$ANDROID_HOME" > local.properties
else
    echo "ℹ️  local.properties exists, ensuring sdk.dir is correct..."
    # Using sed to replace or append sdk.dir without overwriting other properties
    if grep -q "sdk.dir" local.properties; then
        sed -i "s|^sdk.dir=.*|sdk.dir=$ANDROID_HOME|" local.properties
    else
        echo "sdk.dir=$ANDROID_HOME" >> local.properties
    fi
fi

# 5. Set Environment Variables for the current session
# Note: In some CI/Agent environments, you may need to source this script or
# add these exports to your shell profile (.bashrc) if the session persists.
echo "export ANDROID_HOME=$ANDROID_HOME" >> $HOME/.bashrc
echo "export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools" >> $HOME/.bashrc

# 6. Verify Installation
echo "🔍 Verifying installation..."
$ANDROID_HOME/platform-tools/adb --version
echo "✅ Android Environment Setup Complete!"
