import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Jarvis {

    private static final String ARQUIVO_USUARIOS = "usuarios.txt";

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        GerenciadorUsuarios gerenciadorUsuarios = new GerenciadorUsuarios();
        calculadora calculadora = new calculadora();

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

       while (true) {
            
                System.out.println("email ou senha incorretos.");
            } else {
                System.out.println("opcao nao reconhecida.");
            }
        }             excluirUsuario(nomeExcluir);
            
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

           
        }

        entrada.close();
    }

    public static String iniciarSessao(Scanner entrada , GerenciadorUsuarios (()gerenciadorUsuarios) {
            while (true) {
                System.out.println("voce ja tem uma conta? (sim/nao)");
                String resposta = entrada.nextLine().trim().toLowerCase();
    
                if (resposta.equals("sim")) {
                    System.out.println("digite seu email:");
                    String email = entrada.nextLine().trim();
    
                    System.out.println("digite sua senha:");
                    String senha = entrada.nextLine().trim();
    
                    Usuario usuario = gerenciadorUsuarios.autenticar(email, senha);
    
                    if (usuario != null) {
                        return usuario.getNome();
                    } else {
                        System.out.println("email ou senha incorretos. tente novamente.");
                    }
                } else if (resposta.equals("nao")) {
                    return cadastrarUsuarioPeloTeclado(entrada, gerenciadorUsuarios);
                } else {
                    System.out.println("responda apenas com 'sim' ou 'nao'.");
                }
            }
        
        }
    GerenciadorUsuario.cadastrarUsuario(usuario);

   public static String fazerLogin(String email, String senha) {
        File arquivoUsuarios = new File(ARQUIVO_USUARIOS);

        if (!arquivoUsuarios.exists()) {
            return null;
        }

        try {
            Scanner leitor = new Scanner(arquivoUsuarios);

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                String[] partes = linha.split(";");

                if (partes.length == 3 && partes[1].trim().equals(email) && partes[2].trim().equals(senha)) {
                    leitor.close();
                    return partes[0].trim(); // Retorna o nome do usuario
                }
            }

            leitor.close();
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de usuarios.");
        }

        return null; // Retorna null se a autenticação falhar
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
                
                return calculadora.calcular(resposta);
            
            } else if (resposta.contains("-")) {
                return calculadora.calcular(resposta);
            
            } else if (resposta.contains("*")) {
                return calculadora.calcular(resposta);
            
            } else if (resposta.contains("/")) {
                return calculadora.calcular(resposta);
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
