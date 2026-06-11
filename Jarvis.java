import java.util.Scanner;
import java.time.LocalDateTime;
//esses imports sao muito ruins de gravar, mas apartie de uma breve pesquisa voce consegue avançar, e entender como funciona.

public class Jarvis {

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);

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

                    } else if(resposta.contains("somar")) {

                        String[] partes = resposta.split("somar");
                        if (partes.length == 2) {
                            String numeros = partes[1].trim();
                            String[] numerosArray = numeros.split("e|,| ");
                            int soma = 0;
                            for (String num : numerosArray) {
                                try {
                                    soma += Integer.parseInt(num.trim());
                                } catch (NumberFormatException e) {
                                    // Ignorar valores que não são números
                                }
                            }
                            System.out.println("a soma é: " + soma);
                        } else {
                            System.out.println("por favor, forneça os números para somar.");
                        }


                    }else if(resposta.contains("multiplicar")) {

                        String[] partes = resposta.split("multiplicar");
                        if (partes.length == 2) {
                            String numeros = partes[1].trim();
                            String[] numerosArray = numeros.split("e|,| ");
                            int produto = 1;
                            for (String num : numerosArray) {
                                try {
                                    produto *= Integer.parseInt(num.trim());
                                } catch (NumberFormatException e) {
                                    // Ignorar valores que não são números
                                }
                            }
                            System.out.println("o produto é: " + produto);
                        } else {
                            System.out.println("por favor, forneça os números para multiplicar.");
                        }


                    }else if(resposta.contains("dividir")) {

                        String[] partes = resposta.split("dividir");
                        if (partes.length == 2) {
                            String numeros = partes[1].trim();
                            String[] numerosArray = numeros.split("e|,| ");
                            if (numerosArray.length == 2) {
                                try {
                                    int num1 = Integer.parseInt(numerosArray[0].trim());
                                    int num2 = Integer.parseInt(numerosArray[1].trim());
                                    if (num2 != 0) {
                                        double resultado = (double) num1 / num2;
                                        System.out.println("o resultado da divisão é: " + resultado);
                                    } else {
                                        System.out.println("não é possível dividir por zero.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("por favor, forneça números válidos para dividir.");
                                }
                            } else {
                                System.out.println("por favor, forneça exatamente dois números para dividir.");
                            }
                        } else {
                            System.out.println("por favor, forneça os números para dividir.");
                        }

                    } else if(resposta.contains("subtrair")) {

                        String[] partes = resposta.split("subtrair");
                        if (partes.length == 2) {
                            String numeros = partes[1].trim();
                            String[] numerosArray = numeros.split("e|,| ");
                            if (numerosArray.length == 2) {
                                try {
                                    int num1 = Integer.parseInt(numerosArray[0].trim());
                                    int num2 = Integer.parseInt(numerosArray[1].trim());
                                    int resultado = num1 - num2;
                                    System.out.println("o resultado da subtração é: " + resultado);
                                } catch (NumberFormatException e) {
                                    System.out.println("por favor, forneça números válidos para subtrair.");
                                }
                            } else {
                                System.out.println("por favor, forneça exatamente dois números para subtrair.");
                            }
                        } else {
                            System.out.println("por favor, forneça os números para subtrair.");
                        }

                // essas partes sao perguntas basicas de calculadora, mas podendo expandir isso npara div ersar coisas e contas mais complexas, mas esse e o começo esse codigo ainda deve ser bem alterado.

            } else {

                System.out.println("desculpe, não entendi o que você quis dizer.");
            }

            //essa parte e para dar uma resposta mais natural, e nao parecer que a IA esta apenas respondendo perguntas, mas sim que ela esta interagindo com o usuario.
        }

        entrada.close();
    }
}
