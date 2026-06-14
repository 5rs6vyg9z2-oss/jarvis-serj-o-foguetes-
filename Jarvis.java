import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

// Esses imports sao chatos de gravar, mas com pesquisa voce vai entendendo como funciona.
public class Jarvis {

    public static void main(String[] args) throws Exception {
        Scanner entrada = new Scanner(System.in);
        File arquivoUsuario = new File("usuario.txt");

        System.out.println("ola, sou o serjão foguetes");

        String nome;

        if (arquivoUsuario.exists()) {
            Scanner leitorArquivo = new Scanner(arquivoUsuario);

            if (leitorArquivo.hasNextLine()) {
                nome = leitorArquivo.nextLine();
            } else {
                System.out.println("qual seu nome meu nobre?");
                nome = entrada.nextLine();
                salvarUsuario(nome);
            }

            leitorArquivo.close();
            System.out.println("ola novamente, " + nome + "!");
        } else {
            System.out.println("qual seu nome meu nobre?");
            nome = entrada.nextLine();
            salvarUsuario(nome);

            System.out.println("ola. muito prazer em conhecer, " + nome + "!");
        }

        System.out.println("digite 'sair' quando quiser encerrar.");

        boolean primeiraPergunta = true;

        while (true) {
            if (primeiraPergunta) {
                System.out.println("em que posso te ajudar?");
                primeiraPergunta = false;
            } else {
                System.out.println("te ajudo em algo mais?");
            }

            String resposta = entrada.nextLine();

            if (resposta.equalsIgnoreCase("sair")
                    || resposta.equalsIgnoreCase("ate")
                    || resposta.equalsIgnoreCase("obrigado")) {
                System.out.println("ate logo, " + nome + "!");
                break;
            }

            if (resposta.contains("que horas sao")
                    || resposta.contains("horas")) {
                System.out.println("deixa eu ver...");
                System.out.println("mostrando horario...");
                mostrarHorario();
            } else if (resposta.contains("qual a data de hoje")
                    || resposta.contains("data")) {
                System.out.println("deixa eu ver...");
                mostrarData();
            } else if (resposta.contains("+")
                    || resposta.contains("-")
                    || resposta.contains("*")
                    || resposta.contains("/")) {
                calcular(resposta);
            } else {
                System.out.println("desculpe, nao entendi o que voce quis dizer.");
            }
        }

        entrada.close();
    }

    public static void mostrarHorario() {
        LocalDateTime agora = LocalDateTime.now();
        int hora = agora.getHour();
        int minuto = agora.getMinute();

        System.out.println("sao " + hora + ":" + minuto + " horas");
    }

    public static void mostrarData() {
        LocalDateTime agora = LocalDateTime.now();
        int dia = agora.getDayOfMonth();
        int mes = agora.getMonthValue();
        int ano = agora.getYear();

        System.out.println("hoje e " + dia + "/" + mes + "/" + ano);
    }

    public static void calcular(String resposta) {
        try {
            if (resposta.contains("+")) {
                String[] partes = resposta.split("\\+");

                if (partes.length == 2) {
                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    System.out.println("deixa eu calcular...");
                    System.out.println("o resultado e: " + (n1 + n2));
                } else {
                    System.out.println("digite uma soma com dois numeros, tipo 10+5.");
                }
            } else if (resposta.contains("-")) {
                String[] partes = resposta.split("-");

                if (partes.length == 2) {
                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    System.out.println("deixa eu ver...");
                    System.out.println("o resultado e: " + (n1 - n2));
                } else {
                    System.out.println("digite uma subtracao com dois numeros, tipo 10-5.");
                }
            } else if (resposta.contains("*")) {
                String[] partes = resposta.split("\\*");

                if (partes.length == 2) {
                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    System.out.println("caraca mane...");
                    System.out.println("o resultado e: " + (n1 * n2));
                } else {
                    System.out.println("digite uma multiplicacao com dois numeros, tipo 10*5.");
                }
            } else if (resposta.contains("/")) {
                String[] partes = resposta.split("/");

                if (partes.length == 2) {
                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    if (n2 != 0) {
                        System.out.println("deixa eu calcular...");
                        System.out.println("o resultado e: " + (n1 / n2));
                    } else {
                        System.out.println("nao posso dividir por zero.");
                    }
                } else {
                    System.out.println("digite uma divisao com dois numeros, tipo 10/5.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("nao consegui entender os numeros dessa conta.");
        }
    }

    public static void salvarUsuario(String nome) {
        try {
            FileWriter escritor = new FileWriter("usuario.txt");
            escritor.write(nome);
            escritor.close();
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao salvar o usuario: " + e.getMessage());
        }
    }

    public static void cadastrarUsuario(String nome) {
        try {
            FileWriter escritor = new FileWriter("usuarios.txt", true);
            escritor.write(nome + "\n");
            escritor.close();

            System.out.println("Usuario cadastrado com sucesso: " + nome);
        } catch (IOException e) {
            System.out.println("Erro ao cadastrar usuario.");
        }
    }
}
