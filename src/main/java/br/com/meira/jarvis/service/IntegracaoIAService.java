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
                .instructions("Responda em portugues brasileiro com tom caloroso, espontaneo e natural, como numa conversa falada. Use frases curtas e variadas, palavras comuns e transicoes discretas; evite soar como manual, relatorio, locutor ou atendimento automatico. Nao repita a pergunta nem comece sempre com a mesma formula. Seja direto, em ate 3 frases, e aprofunde apenas quando pedirem. Evite titulos, listas e Markdown, salvo se forem solicitados. Em matematica, diga o resultado e uma explicacao breve, sem LaTeX nem simbolos que precisem ser lidos em voz alta.")
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
