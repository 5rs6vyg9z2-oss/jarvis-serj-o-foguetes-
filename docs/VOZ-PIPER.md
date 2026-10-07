# Voz do Jarvis com Piper

O backend recebe o texto da resposta em `/sintetizar`, inicia o worker Piper na
primeira fala e devolve um WAV. O worker mantem o modelo carregado para as falas
seguintes. O navegador toca esse WAV; se a sintese falhar, usa a voz nativa do
navegador. A captura e transcricao de audio continuam no fluxo existente.

## Teste local no Windows

No PowerShell, na raiz do projeto:

```powershell
py -m venv .venv-piper
.\.venv-piper\Scripts\python.exe -m pip install piper-tts==1.8.0
New-Item -ItemType Directory -Force models
Invoke-WebRequest "https://huggingface.co/rhasspy/piper-voices/resolve/1b182b342fcce87f72d0e4fdf88131e5144f62d8/pt/pt_BR/cadu/medium/pt_BR-cadu-medium.onnx" -OutFile "models/pt_BR-cadu-medium.onnx"
Invoke-WebRequest "https://huggingface.co/rhasspy/piper-voices/resolve/1b182b342fcce87f72d0e4fdf88131e5144f62d8/pt/pt_BR/cadu/medium/pt_BR-cadu-medium.onnx.json" -OutFile "models/pt_BR-cadu-medium.onnx.json"
$env:JARVIS_TTS_PYTHON = (Resolve-Path ".venv-piper/Scripts/python.exe").Path
$env:JARVIS_TTS_SCRIPT = (Resolve-Path "src/main/tts/piper_worker.py").Path
$env:JARVIS_TTS_MODEL = (Resolve-Path "models/pt_BR-cadu-medium.onnx").Path
mvn spring-boot:run
```

O modelo fica em `models/`, ignorado pelo Git; nao o envie ao repositorio. A
primeira instalacao baixa cerca de 63 MB. Sem estas variaveis e arquivos locais,
a interface continua funcionando e recorre a voz do navegador.

## Docker

O `Dockerfile` instala o Piper, baixa a voz Cadu e define as variaveis de
ambiente automaticamente. O container limita o heap Java para reservar memoria
para o mecanismo ONNX. O consumo deve ser observado no plano usado pelo Render;
o modelo tambem aumenta o tamanho da imagem.

## Licencas e atribuicao

Consulte [`THIRD-PARTY-NOTICES.md`](../THIRD-PARTY-NOTICES.md) e
[`LICENSES/ISair-JARVIS-LICENSE.txt`](../LICENSES/ISair-JARVIS-LICENSE.txt).
O audio e gerado localmente no servidor; o texto da resposta nao e enviado a um
novo provedor de TTS. A transcricao existente continua usando o Groq.
