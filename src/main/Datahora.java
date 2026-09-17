package src.main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zoneiId.ZoneRulesException;

public class Datahora {

    private static final String FUSO_HORARIO = "America/Sao_Paulo";
    
    public static String obterHorarioAtual() {
        LocalDateTime agora = LocalDateTime.now(FUSO_HORARIO);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm:ss");
        return agora.format(formatador);
    }
    
    public static String obterDataAtual() {
        LocalDateTime agora = LocalDateTime.now(FUSO_HORARIO);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return agora.format(formatador);
    }
    
    public static String obterDataEHora() {
        LocalDateTime agora = LocalDateTime.now(FUSO_HORARIO);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return agora.format(formatador);
    }
}
