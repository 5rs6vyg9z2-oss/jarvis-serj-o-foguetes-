import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Classe utilitaria: nao guarda estado, apenas devolve data e hora formatadas.
public class Datahora {

    // static permite chamar Datahora.obterHorarioAtual() sem criar um objeto Datahora.
    public static String obterHorarioAtual() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm:ss");
        return agora.format(formatador);
    }

    // LocalDateTime pega a data/hora do computador no momento da chamada.
    public static String obterDataAtual() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return agora.format(formatador);
    }

    // DateTimeFormatter define como a informacao sera mostrada para o usuario.
    public static String obterDataEHora() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return agora.format(formatador);
    }
}
