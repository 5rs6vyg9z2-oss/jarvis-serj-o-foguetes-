"""Adapted from isair/jarvis/src/jarvis/output/tts.py (PiperTTS).

This persistent worker keeps the voice model loaded and returns WAV data to the
Java web app instead of playing audio on the server.
"""

# Copyright (c) 2025 Baris Sencan. Adaptation follows the license in
# LICENSES/ISair-JARVIS-LICENSE.txt and remains for non-commercial use.

import base64
import io
import sys
import wave

import numpy as np
from piper.config import SynthesisConfig
from piper.voice import PiperVoice


def gerar_wav(voz, texto):
    configuracao = SynthesisConfig(
        length_scale=0.96,
        noise_scale=0.667,
        noise_w_scale=0.8,
    )
    blocos = [bloco.audio_int16_array for bloco in voz.synthesize(texto, configuracao)]
    if not blocos:
        raise RuntimeError("Piper generated no audio")

    pausa = np.zeros(int(0.25 * voz.config.sample_rate), dtype=np.int16)
    partes = []
    for indice, bloco in enumerate(blocos):
        if indice:
            partes.append(pausa)
        partes.append(bloco)
    amostras = np.concatenate(partes).astype("<i2", copy=False)

    buffer = io.BytesIO()
    with wave.open(buffer, "wb") as arquivo:
        arquivo.setnchannels(1)
        arquivo.setsampwidth(2)
        arquivo.setframerate(voz.config.sample_rate)
        arquivo.writeframes(amostras.tobytes())
    return buffer.getvalue()


def main():
    if len(sys.argv) != 2:
        raise SystemExit("usage: piper_worker.py MODEL.onnx")

    voz = PiperVoice.load(sys.argv[1])
    print("READY", flush=True)

    for linha in sys.stdin:
        try:
            texto = base64.b64decode(linha.strip(), validate=True).decode("utf-8").strip()
            if not texto:
                raise ValueError("text input is empty")
            audio = gerar_wav(voz, texto)
            print(base64.b64encode(audio).decode("ascii"), flush=True)
        except Exception as erro:
            mensagem = base64.b64encode(str(erro).encode("utf-8")).decode("ascii")
            print("ERR:" + mensagem, flush=True)


if __name__ == "__main__":
    main()
