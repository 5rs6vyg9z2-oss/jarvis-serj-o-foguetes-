package br.com.meira.jarvis.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.errors.RateLimitException;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

@Service
public class IntegracaoIAService {
    private final OpenAIClient cliente;

    public IntegracaoIAService() {
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Configure a variavel de ambiente GROQ_API_KEY.");
        }

        this.cliente = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl("https://api.groq.com/openai/v1")
                .build();
    }

    public String responder(String mensagem) {
        ResponseCreateParams parametros = ResponseCreateParams.builder()
                .instructions("Responda em portugues claro e direto, em no maximo 3 frases. Foque no que foi perguntado e evite introducoes, repeticoes e exemplos desnecessarios. So explique detalhadamente quando o usuario pedir. Para matematica, mostre o resultado com uma explicacao curta, sem LaTeX.")
                .input(mensagem)
                .model("openai/gpt-oss-20b")
                .build();

        try {
            Response resposta = cliente.responses().create(parametros);
            return resposta.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(mensagemResposta -> mensagemResposta.content().stream())
                    .flatMap(conteudo -> conteudo.outputText().stream())
                    .map(texto -> texto.text())
                    .collect(Collectors.joining());
        } catch (RateLimitException erro) {
            return escolherMensagemDeLimite();
        }
    }

    private String escolherMensagemDeLimite() {
        List<String> mensagens = List.of(
                "o desenvolvedor e um duro e nao pagou.",
                "desculpa, isso e igual celular precisa consultar os creditos da IA",
                "A API esta sem saldo disponivel no momento.",
                "o cara e pobre tu acha que ele pagou os tokens? resposta nao.");

        int indiceAleatorio = ThreadLocalRandom.current().nextInt(mensagens.size());
        return mensagens.get(indiceAleatorio);
    }
}
