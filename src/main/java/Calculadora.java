// Classe responsavel por entender e resolver contas simples digitadas pelo usuario.
public class Calculadora {

    // Verifica se o texto parece ter alguma operacao matematica basica.
    public boolean temOperacao(String texto) {
        return texto.contains("+")
                || texto.contains("-")
                || texto.contains("*")
                || texto.contains("/");
    }

    // Recebe uma expressao em texto, separa os numeros e devolve uma resposta pronta para a tela.
    public String calcular(String expressao) {
        try {
            if (expressao.contains("+")) {
                // O "+" precisa de "\\+" porque split usa regex, nao texto puro.
                String[] partes = expressao.split("\\+");

                if (partes.length == 2) {
                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    return "deixa eu calcular...\no resultado e: " + (n1 + n2);
                }

                return "digite uma soma com dois numeros, tipo 10+5.";
            }

            if (expressao.contains("-")) {
                String[] partes = expressao.split("-");

                if (partes.length == 2) {
                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    return "deixa eu ver...\no resultado e: " + (n1 - n2);
                }

                return "digite uma subtracao com dois numeros, tipo 10-5.";
            }

            if (expressao.contains("*")) {
                // O "*" tambem precisa de "\\*" pelo mesmo motivo do "+".
                String[] partes = expressao.split("\\*");

                if (partes.length == 2) {
                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    return "caraca mane...\no resultado e: " + (n1 * n2);
                }

                return "digite uma multiplicacao com dois numeros, tipo 10*5.";
            }

            if (expressao.contains("/")) {
                String[] partes = expressao.split("/");

                if (partes.length == 2) {
                    double n1 = Double.parseDouble(partes[0].trim());
                    double n2 = Double.parseDouble(partes[1].trim());

                    if (n2 == 0) {
                        return "nao posso dividir por zero.";
                    }

                    return "deixa eu calcular...\no resultado e: " + (n1 / n2);
                }

                return "digite uma divisao com dois numeros, tipo 10/5.";
            }

            return "nao encontrei uma operacao matematica.";
        } catch (NumberFormatException e) {
            // NumberFormatException acontece quando o texto nao consegue virar numero.
            return "nao consegui entender os numeros dessa conta.";
        }
    }
}
