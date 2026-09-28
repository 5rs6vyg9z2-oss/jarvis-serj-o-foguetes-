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
        this.cliente = OpenAIOkHttpClient.fromEnv();
    }

    public String responder(String mensagem) {
        ResponseCreateParams parametros = ResponseCreateParams.builder()
                .input(mensagem)
                .model("gpt-6-astra")
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
                "A IA esta sem creditos ou atingiu um limite. Tente novamente mais tarde.",
                "Nao consegui consultar a IA agora. Tente novamente depois.",
                "A API esta sem saldo disponivel no momento.",
                "o cara e pobre tu acha que ele pagou os tokens? resposta nao.");

        int indiceAleatorio = ThreadLocalRandom.current().nextInt(mensagens.size());
        return mensagens.get(indiceAleatorio);
    }
}
