# Third-party notices

## isair/jarvis

`src/main/tts/piper_worker.py` adapts the Piper synthesis flow from
`src/jarvis/output/tts.py` in [isair/jarvis](https://github.com/isair/jarvis).
The upstream implementation loads `PiperVoice`, collects the audio chunks
returned by `synthesize`, and concatenates them before local playback. This
adaptation writes those samples as WAV data for playback in the browser instead.
Copyright (c) 2025 Baris Sencan. The applicable license is included at
`LICENSES/ISair-JARVIS-LICENSE.txt`; it permits non-commercial use and requires
derivative works to retain the same terms.

## Piper TTS

The Docker image installs `piper-tts` version 1.8.0 from
[OHF-Voice/piper1-gpl](https://github.com/OHF-Voice/piper1-gpl), licensed
GPL-3.0-or-later. Its license is distributed with the installed Python package
and is available at https://github.com/OHF-Voice/piper1-gpl/blob/main/COPYING.

## Brazilian Portuguese voice model

The image downloads `pt_BR-cadu-medium` and its JSON configuration from
[rhasspy/piper-voices](https://huggingface.co/rhasspy/piper-voices/tree/main/pt/pt_BR/cadu/medium).
The model card identifies Brazilian Portuguese, one speaker, 22,050 Hz, and a
CC0 dataset. See the upstream `MODEL_CARD` in that directory for the voice and
dataset details. The model is separate from the Piper software license.
