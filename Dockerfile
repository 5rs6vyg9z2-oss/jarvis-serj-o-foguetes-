FROM maven:3.9-eclipse-temurin-21  AS build 

 WORKDIR /app

 COPY pom.xml .
 COPY src ./src

 RUN mvn clean package

 FROM eclipse-temurin:21-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends python3 python3-venv curl libgomp1 \
    && rm -rf /var/lib/apt/lists/* \
    && python3 -m venv /opt/piper-venv \
    && /opt/piper-venv/bin/pip install --no-cache-dir piper-tts==1.8.0 \
    && mkdir -p /opt/piper-voices

RUN curl -fsSL "https://huggingface.co/rhasspy/piper-voices/resolve/1b182b342fcce87f72d0e4fdf88131e5144f62d8/pt/pt_BR/cadu/medium/pt_BR-cadu-medium.onnx" \
        -o /opt/piper-voices/pt_BR-cadu-medium.onnx \
    && curl -fsSL "https://huggingface.co/rhasspy/piper-voices/resolve/1b182b342fcce87f72d0e4fdf88131e5144f62d8/pt/pt_BR/cadu/medium/pt_BR-cadu-medium.onnx.json" \
        -o /opt/piper-voices/pt_BR-cadu-medium.onnx.json

COPY --from=build /app/target/projeto-jarvis-1.0-SNAPSHOT.jar /app.jar
COPY src/main/tts/piper_worker.py /opt/jarvis/piper_worker.py

ENV JARVIS_TTS_PYTHON=/opt/piper-venv/bin/python \
    JARVIS_TTS_SCRIPT=/opt/jarvis/piper_worker.py \
    JARVIS_TTS_MODEL=/opt/piper-voices/pt_BR-cadu-medium.onnx \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=35"

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]
