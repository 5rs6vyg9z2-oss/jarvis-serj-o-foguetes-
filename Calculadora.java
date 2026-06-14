public class Calculadora {

    public boolean temOperacao(String texto) {
        return texto.contains("+")
                || texto.contains("-")
                || texto.contains("*")
                || texto.contains("/");
    }

    public String calcular(String expressao) {
        try {
            if (expressao.contains("+")) {
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
            return "nao consegui entender os numeros dessa conta.";
        }
    }
}
