import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Datahora {

    public static String obterHorarioAtual() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm:ss");
        return agora.format(formatador);
    }

    public static String obterDataAtual() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return agora.format(formatador);
    }

    public static String obterDataEHora() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return agora.format(formatador);
    }
}
