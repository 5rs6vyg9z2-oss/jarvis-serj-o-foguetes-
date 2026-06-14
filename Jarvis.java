import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Jarvis {

    private static final String ARQUIVO_USUARIOS = "usuarios.txt";

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        System.out.println("ola, sou o serjao foguetes");

        String nome = iniciarSessao(entrada);

        if (nome == null) {
            entrada.close();
            return;
        }

        System.out.println("ola, " + nome + "!");
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
            String comando = resposta.trim().toLowerCase();

            if (comando.equals("sair")
                    || comando.equals("ate")
                    || comando.equals("obrigado")) {
                System.out.println("ate logo, " + nome + "!");
                break;
            }

            if (comando.contains("que horas sao")
                    || comando.contains("horas")) {
                System.out.println("deixa eu ver...");
                System.out.println("mostrando horario...");
                mostrarHorario();
            } else if (comando.contains("qual a data de hoje")
                    || comando.contains("data")) {
                System.out.println("deixa eu ver...");
                mostrarData();
            } else if (comando.contains("+")
                    || comando.contains("-")
                    || comando.contains("*")
                    || comando.contains("/")) {
                calcular(comando);
            } else if (comando.equals("cadastrar usuario")) {
                cadastrarUsuarioPeloTeclado(entrada);
            } else if (comando.equals("listar usuarios")) {
                listarUsuarios();
            } else if (comando.equals("alterar nome")) {
                listarUsuarios();

                System.out.println("digite o nome que deseja alterar:");
                String nomeAntigo = entrada.nextLine();

                System.out.println("digite o novo nome:");
                String novoNome = entrada.nextLine();

                alterarUsuario(nomeAntigo, novoNome);
            } else if (comando.equals("excluir usuario")) {
                listarUsuarios();

                System.out.println("digite o nome que deseja excluir:");
                String nomeExcluir = entrada.nextLine();

                excluirUsuario(nomeExcluir);
            
            } else if (comando.equals("alterar email")) {
                listarUsuarios();

                System.out.println("digite o email que deseja alterar:");
                String emailAntigo = entrada.nextLine();

                System.out.println("digite o novo email:");
                String emailNovo = entrada.nextLine();

                alterarEmail(emailAntigo, emailNovo);
                
            }else {
                System.out.println("desculpe, nao entendi o que voce quis dizer.");
        
            }

           

        }entrada.close();
    }

    public static String iniciarSessao(Scanner entrada) {
        while (true) {
            System.out.println("digite 'login' para entrar, 'cadastrar usuario' para criar conta ou 'sair' para encerrar:");
            String opcao = entrada.nextLine().trim().toLowerCase();

            if (opcao.equals("sair")) {
                System.out.println("ate logo!");
                return null;
            }

            if (opcao.equals("cadastrar usuario")) {
                String nome = cadastrarUsuarioPeloTeclado(entrada);

                if (nome != null) {
                    return nome;
                }
            } else if (opcao.equals("login")) {
                System.out.println("digite seu email:");
                String email = entrada.nextLine();

                System.out.println("digite sua senha:");
                String senha = entrada.nextLine();

                String nome = fazerLogin(email, senha);

                if (nome != null) {
                    return nome;
                }

                System.out.println("email ou senha incorretos.");
            } else {
                System.out.println("opcao nao reconhecida.");
            }
        }
    }

    public static String cadastrarUsuarioPeloTeclado(Scanner entrada) {
        System.out.println("digite o nome do usuario:");
        String nome = entrada.nextLine();

        System.out.println("digite o email do usuario:");
        String email = entrada.nextLine();

        System.out.println("digite a senha do usuario:");
        String senha = entrada.nextLine();

        if (cadastrarUsuario(nome, email, senha)) {
            return nome;
        }

        return null;
    }

    public static boolean cadastrarUsuario(String nome, String email, String senha) {
        if (emailJaExiste(email)) {
            System.out.println("ja existe um usuario cadastrado com esse email.");
            return false;
        }

        try {
            FileWriter escritor = new FileWriter(ARQUIVO_USUARIOS, true);
            escritor.write(nome + ";" + email + ";" + senha + "\n");
            escritor.close();

            System.out.println("Usuario cadastrado com sucesso: " + nome);
            return true;
        } catch (IOException e) {
            System.out.println("Erro ao cadastrar usuario.");
            return false;
        }
    }

    public static String fazerLogin(String email, String senha) {
        File arquivoUsuarios = new File(ARQUIVO_USUARIOS);

        if (!arquivoUsuarios.exists()) {
            System.out.println("Nenhum usuario cadastrado ainda.");
            return null;
        }

        try {
            Scanner leitor = new Scanner(arquivoUsuarios);

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(";");

                if (partes.length == 3) {
                    String nomeCadastrado = partes[0].trim();
                    String emailCadastrado = partes[1].trim();
                    String senhaCadastrada = partes[2].trim();

                    if (emailCadastrado.equals(email) && senhaCadastrada.equals(senha)) {
                        leitor.close();
                        return nomeCadastrado;
                    }
                }
            }

            leitor.close();
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de usuarios.");
        }

        return null;
    }

    public static boolean emailJaExiste(String email) {
        File arquivoUsuarios = new File(ARQUIVO_USUARIOS);

        if (!arquivoUsuarios.exists()) {
            return false;
        }

        try {
            Scanner leitor = new Scanner(arquivoUsuarios);

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(";");

                if (partes.length == 3 && partes[1].trim().equals(email)) {
                    leitor.close();
                    return true;
                }
            }

            leitor.close();
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de usuarios.");
        }

        return false;
    }

    public static void listarUsuarios() {
        File arquivoUsuarios = new File(ARQUIVO_USUARIOS);

        if (!arquivoUsuarios.exists()) {
            System.out.println("Nenhum usuario cadastrado ainda.");
            return;
        }

        try {
            Scanner leitor = new Scanner(arquivoUsuarios);
            System.out.println("Usuarios cadastrados:");

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(";");

                if (partes.length == 3) {
                    System.out.println("- " + partes[0]);
                }
            }

            leitor.close();
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de usuarios.");
        }
    }

    public static void alterarUsuario(String nomeAntigo, String novoNome) {
        File arquivoUsuarios = new File(ARQUIVO_USUARIOS);

        if (!arquivoUsuarios.exists()) {
            System.out.println("Nenhum usuario cadastrado para alterar.");
            return;
        }

        try {
            Scanner leitor = new Scanner(arquivoUsuarios);
            StringBuilder conteudo = new StringBuilder();
            boolean usuarioEncontrado = false;

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(";");

                if (partes.length == 3 && partes[0].trim().equals(nomeAntigo)) {
                    conteudo.append(novoNome).append(";").append(partes[1]).append(";").append(partes[2]).append("\n");
                    usuarioEncontrado = true;
                } else {
                    conteudo.append(linha).append("\n");
                }
            }

            leitor.close();

            FileWriter escritor = new FileWriter(arquivoUsuarios);
            escritor.write(conteudo.toString());
            escritor.close();

            if (usuarioEncontrado) {
                System.out.println("Usuario alterado com sucesso: " + nomeAntigo + " para " + novoNome);
            } else {
                System.out.println("Usuario nao encontrado: " + nomeAntigo);
            }
        } catch (IOException e) {
            System.out.println("Erro ao alterar usuario.");
        }
    }

    public static void excluirUsuario(String nome) {
        File arquivoUsuarios = new File(ARQUIVO_USUARIOS);

        if (!arquivoUsuarios.exists()) {
            System.out.println("Nenhum usuario cadastrado para excluir.");
            return;
        }

        try {
            Scanner leitor = new Scanner(arquivoUsuarios);
            StringBuilder conteudo = new StringBuilder();
            boolean usuarioEncontrado = false;

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(";");

                if (partes.length == 3 && partes[0].trim().equals(nome)) {
                    usuarioEncontrado = true;
                } else {
                    conteudo.append(linha).append("\n");
                }
            }

            leitor.close();

            FileWriter escritor = new FileWriter(arquivoUsuarios);
            escritor.write(conteudo.toString());
            escritor.close();

            if (usuarioEncontrado) {
                System.out.println("Usuario excluido com sucesso: " + nome);
            } else {
                System.out.println("Usuario nao encontrado: " + nome);
            }
        } catch (IOException e) {
            System.out.println("Erro ao excluir usuario.");
        }
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

    public static void alterarEmail(String emailAntigo, String emailNovo) {
        File arquivoUsuarios = new File(ARQUIVO_USUARIOS);

        if (!arquivoUsuarios.exists()) {
            System.out.println("Nenhum usuario cadastrado para alterar.");
            return;
        }

        try {
            Scanner leitor = new Scanner(arquivoUsuarios);
            StringBuilder conteudo = new StringBuilder();
            boolean usuarioEncontrado = false;

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(";");

                if (partes.length == 3 && partes[1].trim().equals(emailAntigo)) {
                    conteudo.append(partes[0]).append(";").append(emailNovo).append(";").append(partes[2]).append("\n");
                    usuarioEncontrado = true;
                } else {
                    conteudo.append(linha).append("\n");
                }
            }

            leitor.close();

            FileWriter escritor = new FileWriter(arquivoUsuarios);
            escritor.write(conteudo.toString());
            escritor.close();

            if (usuarioEncontrado) {
                System.out.println("Email alterado com sucesso: " + emailAntigo + " para " + emailNovo);
            } else {
                System.out.println("Usuario nao encontrado com email: " + emailAntigo);
            }
        } catch (IOException e) {
            System.out.println("Erro ao alterar email do usuario.");
        }
    }
}
