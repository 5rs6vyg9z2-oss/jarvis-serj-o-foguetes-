import java.util.Scanner;
import java.time.LocalDateTime;
import java.io.file;
import java.io.FileWriter;
import java.io.IOException;
//esses imports sao muito ruins de gravar, mas apartie de uma breve pesquisa voce consegue avançar, e entender como funciona.

public class Jarvis {

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);
        // o scanner e uma classe que serve para ler a entrada do usuario, nesse caso ele vai ler o que o usuario digitar no console.

        System.out.println("olá, sou o sejão foguetes");
        System.out.println("qual seu nome meu nobre?");

        //essa parte e o unicio onde a IA se comunica e interaje com o usurio, a partir daqui ele começa a ajudar
        String nome = entrada.nextLine();

        //essa e sempre a entrada em rincipalmente java (nao que eu va usar ate porque eu to pra backand.)
        System.out.println("olá. muito prazer em conheccer, " + nome + "!");
        System.out.println("digite 'sair' quando quiser encerrar.");

        boolean primeirapergunta = true;

        while (true) {
        // a while true e um loop infinito.. (mas bom) ele vai ficar rodando o codigo dentro dele ate o usuario digitar "sair" e ai ele vai encerrar o programa.

            if (primeirapergunta) {
                System.out.println("em que posso te ajudar?");
                primeirapergunta = false;
            } else {
                System.out.println("te ajudo em algo mais?");
            }

            String resposta = entrada.nextLine();

            // aparentemente o boolean e uma variavel queso e verdadeira ou falsa, e nesse caso ele serva pra controlar a primeira pergunta, para que seja diferente
            // de outras perguntas e nao pareça robotico, e sim mais natural.

            if (resposta.equalsIgnoreCase("sair")
                    || resposta.equalsIgnoreCase("até")
                    || resposta.equalsIgnoreCase("obrigado")) {

                System.out.println("até logo, " + nome + "!");
                break;
            }

            if (resposta.contains("que horas são")
                    || resposta.contains("horas")) {

                System.out.println("deixa eu ver...");

                LocalDateTime agora = LocalDateTime.now();
                int hora = agora.getHour();
                int minuto = agora.getMinute();

                System.out.println("são " + hora + ":" + minuto + " horas");

            } else if (resposta.contains("qual a data de hoje")
                    || resposta.contains("data")) {

                System.out.println("deixa eu ver...");

                LocalDateTime agora = LocalDateTime.now();
                int dia = agora.getDayOfMonth();
                int mes = agora.getMonthValue();
                int ano = agora.getYear();

                System.out.println("hoje é " + dia + "/" + mes + "/" + ano);

            } else if (resposta.contains("+")) {

                String[] partes = resposta.split("\\+");

                if (partes.length == 2) {

                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    System.out.println("deixa eu calcular...");
                    System.out.println("o resultado é: " + (n1 + n2));
                }

            } else if (resposta.contains("-")) {

                String[] partes = resposta.split("-");

                if (partes.length == 2) {

                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    System.out.println("deixa eu ver...");
                    System.out.println("o resultado é: " + (n1 - n2));
                }

            } else if (resposta.contains("*")) {

                String[] partes = resposta.split("\\*");

                if (partes.length == 2) {

                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    System.out.println("caraca mane...");
                    System.out.println("o resultado é: " + (n1 * n2));
                }

            } else if (resposta.contains("/")) {

                String[] partes = resposta.split("/");

                if (partes.length == 2) {

                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    if (n2 != 0) {

                        System.out.println("deixa eu calcular...");
                        System.out.println("o resultado é: " + (n1 / n2));

                    } else {

                        System.out.println("não posso dividir por zero.");
                    }
                }

            //resposta.contains basicamente identfica no que foi digitado pelo usuario e acha a "palavra chave" que nesse caso e o indicativo de que operação
            // split separa os numeros para a operaçao, sem que a maquina peça pro usuario falar separadamente
            //o legth e o idicativo que pra essas operacoes especificas ele precisa de uma quantidade especifica (como visto ai 2 numeros) se o usuario digitar algo errado como "13+" ele nao emite erro
            //Já o parte converte o texto em numeros

            } else {

                System.out.println("desculpe, não entendi o que você quis dizer.");
            }

            //essa parte e para dar uma resposta mais natural, e nao parecer que a IA esta apenas respondendo perguntas, mas sim que ela esta interagindo com o usuario.
        }

        entrada.close();
    }
}