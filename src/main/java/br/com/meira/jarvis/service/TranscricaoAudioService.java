package br.com.meira.jarvis.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TranscricaoAudioService {
    private final RestClient cliente;

    public TranscricaoAudioService() {
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Configure a variavel de ambiente GROQ_API_KEY.");
        }

        this.cliente = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    public String transcrever(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O audio enviado esta vazio.");
        }

        try {
            String tipoConteudo = arquivo.getContentType();
            String nomeArquivo = escolherNomeArquivo(tipoConteudo);
            ByteArrayResource audio = new ByteArrayResource(arquivo.getBytes()) {
                @Override
                public String getFilename() {
                    return nomeArquivo;
                }
            };

            HttpHeaders cabecalhosAudio = new HttpHeaders();
            if (tipoConteudo != null && !tipoConteudo.isBlank()) {
                cabecalhosAudio.setContentType(MediaType.parseMediaType(tipoConteudo));
            }

            MultiValueMap<String, Object> formulario = new LinkedMultiValueMap<>();
            formulario.add("file", new HttpEntity<>(audio, cabecalhosAudio));
            formulario.add("model", "whisper-large-v3-turbo");
            formulario.add("language", "pt");
            formulario.add("response_format", "json");

            Map<?, ?> resposta = cliente.post()
                    .uri("/audio/transcriptions")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(formulario)
                    .retrieve()
                    .body(Map.class);

            if (resposta == null || !(resposta.get("text") instanceof String texto)) {
                throw new IllegalStateException("O Groq nao retornou o texto transcrito.");
            }

            return texto.trim();
        } catch (IOException erro) {
            throw new IllegalStateException("Nao foi possivel ler o audio enviado.", erro);
        }
    }

    private String escolherNomeArquivo(String tipoConteudo) {
        if (tipoConteudo == null) {
            return "gravacao.webm";
        }

        String tipo = tipoConteudo.toLowerCase();
        if (tipo.contains("mp4")) {
            return "gravacao.mp4";
        }
        if (tipo.contains("ogg")) {
            return "gravacao.ogg";
        }
        if (tipo.contains("wav")) {
            return "gravacao.wav";
        }
        if (tipo.contains("mpeg")) {
            return "gravacao.mp3";
        }
        return "gravacao.webm";
    }
}
