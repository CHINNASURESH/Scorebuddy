FROM openjdk:17-jdk-slim

# Install system dependencies
RUN apt-get update && apt-get install -y curl unzip git python3

WORKDIR /project

# Setup Android SDK
ENV ANDROID_HOME=/project/android-sdk
ENV CMDLINE_TOOLS_ROOT=$ANDROID_HOME/cmdline-tools
ENV PATH=$CMDLINE_TOOLS_ROOT/latest/bin:$ANDROID_HOME/platform-tools:$PATH

# Download Command Line Tools
RUN mkdir -p $CMDLINE_TOOLS_ROOT && \
    curl -o cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip && \
    unzip -q cmdline-tools.zip -d $CMDLINE_TOOLS_ROOT && \
    rm cmdline-tools.zip && \
    mv $CMDLINE_TOOLS_ROOT/cmdline-tools $CMDLINE_TOOLS_ROOT/latest

# Accept Licenses
RUN yes | sdkmanager --licenses

# Install Build Tools and Platforms
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Copy Project
COPY . .

# Create local.properties
RUN echo "sdk.dir=$ANDROID_HOME" > local.properties

# Build
RUN chmod +x gradlew
RUN ./gradlew assembleDebug

# Serve
# Render sets PORT environment variable, defaults to 10000 if not set
ENV PORT=10000
CMD python3 -m http.server $PORT --directory app/build/outputs/apk/debug
