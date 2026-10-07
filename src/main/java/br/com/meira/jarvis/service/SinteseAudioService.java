package br.com.meira.jarvis.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SinteseAudioService {
    private static final System.Logger LOGGER = System.getLogger(SinteseAudioService.class.getName());
    private static final int LIMITE_CARACTERES = 5000;
    private static final long TEMPO_LIMITE_SEGUNDOS = 60;

    private final String executavelPython;
    private final Path script;
    private final Path modelo;

    private Process processo;
    private BufferedWriter entradaWorker;
    private BufferedReader saidaWorker;

    public SinteseAudioService(
            @Value("${JARVIS_TTS_PYTHON:python3}") String executavelPython,
            @Value("${JARVIS_TTS_SCRIPT:src/main/tts/piper_worker.py}") String script,
            @Value("${JARVIS_TTS_MODEL:models/pt_BR-cadu-medium.onnx}") String modelo) {
        this.executavelPython = executavelPython;
        this.script = Path.of(script);
        this.modelo = Path.of(modelo);
    }

    public synchronized byte[] sintetizar(String texto) {
        if (texto == null || texto.isBlank() || texto.length() > LIMITE_CARACTERES) {
            throw new IllegalArgumentException("O texto para sintese esta vazio ou excede o limite.");
        }
        validarArquivos();

        try {
            iniciarWorkerSeNecessario();
            String textoCodificado = Base64.getEncoder()
                    .encodeToString(texto.getBytes(StandardCharsets.UTF_8));
            entradaWorker.write(textoCodificado);
            entradaWorker.newLine();
            entradaWorker.flush();

            String resposta = lerRespostaComTempoLimite();
            if (resposta.startsWith("ERR:")) {
                String detalhe = new String(
                        Base64.getDecoder().decode(resposta.substring(4)),
                        StandardCharsets.UTF_8);
                throw new IllegalStateException("Piper falhou ao gerar o audio: " + detalhe);
            }

            byte[] wav = Base64.getDecoder().decode(resposta);
            if (wav.length < 44 || wav[0] != 'R' || wav[1] != 'I'
                    || wav[2] != 'F' || wav[3] != 'F') {
                encerrarWorker();
                throw new IllegalStateException("Piper retornou um arquivo de audio invalido.");
            }
            return wav;
        } catch (IOException erro) {
            encerrarWorker();
            throw new IllegalStateException("Nao foi possivel comunicar com o sintetizador.", erro);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void aquecerModeloEmSegundoPlano() {
        Thread.ofVirtual().name("jarvis-tts-warmup").start(() -> {
            try {
                sintetizar("Jarvis pronto.");
            } catch (RuntimeException erro) {
                LOGGER.log(System.Logger.Level.WARNING,
                        "Nao foi possivel pre-aquecer a voz; a primeira sintese pode demorar mais: {0}",
                        erro.getMessage());
            }
        });
    }

    private void validarArquivos() {
        if (!Files.isRegularFile(script) || !Files.isRegularFile(modelo)
                || !Files.isRegularFile(Path.of(modelo + ".json"))) {
            throw new IllegalStateException("Piper ou o modelo de voz nao esta configurado.");
        }
    }

    private void iniciarWorkerSeNecessario() throws IOException {
        if (processo != null && processo.isAlive()) {
            return;
        }

        encerrarWorker();
        processo = new ProcessBuilder(
                executavelPython,
                script.toString(),
                modelo.toString())
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        entradaWorker = new BufferedWriter(
                new OutputStreamWriter(processo.getOutputStream(), StandardCharsets.UTF_8));
        saidaWorker = new BufferedReader(
                new InputStreamReader(processo.getInputStream(), StandardCharsets.UTF_8));

        if (!"READY".equals(lerRespostaComTempoLimite())) {
            encerrarWorker();
            throw new IllegalStateException("Piper nao conseguiu carregar o modelo de voz.");
        }
    }

    private String lerRespostaComTempoLimite() {
        BufferedReader saidaAtual = saidaWorker;
        CompletableFuture<String> leitura = CompletableFuture.supplyAsync(() -> {
            try {
                return saidaAtual.readLine();
            } catch (IOException erro) {
                throw new UncheckedIOException(erro);
            }
        });

        try {
            String linha = leitura.get(TEMPO_LIMITE_SEGUNDOS, TimeUnit.SECONDS);
            if (linha == null) {
                encerrarWorker();
                throw new IllegalStateException("O worker Piper foi encerrado inesperadamente.");
            }
            return linha;
        } catch (TimeoutException erro) {
            encerrarWorker();
            throw new IllegalStateException("A sintese de voz excedeu o tempo limite.", erro);
        } catch (InterruptedException erro) {
            Thread.currentThread().interrupt();
            encerrarWorker();
            throw new IllegalStateException("A sintese de voz foi interrompida.", erro);
        } catch (ExecutionException erro) {
            encerrarWorker();
            Throwable causa = erro.getCause();
            if (causa instanceof UncheckedIOException ioErro) {
                throw new IllegalStateException("Falha ao ler o audio sintetizado.", ioErro.getCause());
            }
            throw new IllegalStateException("Falha inesperada na leitura do audio.", causa);
        }
    }

    private void encerrarWorker() {
        fecharEntrada();
        if (processo != null) {
            processo.destroyForcibly();
            processo = null;
        }
        if (saidaWorker != null) {
            try {
                saidaWorker.close();
            } catch (IOException ignored) {
                // O processo ja esta sendo encerrado.
            }
            saidaWorker = null;
        }
    }

    private void fecharEntrada() {
        if (entradaWorker != null) {
            try {
                entradaWorker.close();
            } catch (IOException ignored) {
                // O processo ja esta sendo encerrado.
            }
            entradaWorker = null;
        }
    }

    @PreDestroy
    public synchronized void desligar() {
        fecharEntrada();
        if (processo != null) {
            try {
                if (!processo.waitFor(2, TimeUnit.SECONDS)) {
                    processo.destroyForcibly();
                }
            } catch (InterruptedException erro) {
                processo.destroyForcibly();
                Thread.currentThread().interrupt();
            }
            processo = null;
        }
        if (saidaWorker != null) {
            try {
                saidaWorker.close();
            } catch (IOException ignored) {
                // O worker ja foi encerrado.
            }
            saidaWorker = null;
        }
    }
}
