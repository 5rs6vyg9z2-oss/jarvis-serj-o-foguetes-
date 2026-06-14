import java.time.LocalDateTime;
import java.util.Scanner;

public class Jarvis {

    private static final String ARQUIVO_USUARIOS = "usuarios.txt";

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        GerenciadorUsuarios gerenciadorUsuarios = new GerenciadorUsuarios(ARQUIVO_USUARIOS);
        Calculadora calculadora = new Calculadora();

        System.out.println("ola, sou o serjao foguetes");

        String nome = iniciarSessao(entrada, gerenciadorUsuarios);

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
            } else if (calculadora.temOperacao(comando)) {
                System.out.println(calculadora.calcular(comando));
            } else if (comando.equals("cadastrar usuario")) {
                cadastrarUsuarioPeloTeclado(entrada, gerenciadorUsuarios);
            } else if (comando.equals("listar usuarios")) {
                listarUsuarios(gerenciadorUsuarios);
            } else if (comando.equals("alterar nome")) {
                listarUsuarios(gerenciadorUsuarios);

                System.out.println("digite o nome que deseja alterar:");
                String nomeAntigo = entrada.nextLine().trim();

                System.out.println("digite o novo nome:");
                String novoNome = entrada.nextLine().trim();

                alterarUsuario(nomeAntigo, novoNome, gerenciadorUsuarios);
            } else if (comando.equals("excluir usuario")) {
                listarUsuarios(gerenciadorUsuarios);

                System.out.println("digite o nome que deseja excluir:");
                String nomeExcluir = entrada.nextLine().trim();

                excluirUsuario(nomeExcluir, gerenciadorUsuarios);
            } else if (comando.equals("alterar email")) {
                listarUsuarios(gerenciadorUsuarios);

                System.out.println("digite o email que deseja alterar:");
                String emailAntigo = entrada.nextLine().trim();

                System.out.println("digite o novo email:");
                String emailNovo = entrada.nextLine().trim();

                alterarEmail(emailAntigo, emailNovo, gerenciadorUsuarios);
            } else {
                System.out.println("desculpe, nao entendi o que voce quis dizer.");
            }
        }

        entrada.close();
    }

    public static String iniciarSessao(Scanner entrada, GerenciadorUsuarios gerenciadorUsuarios) {
        while (true) {
            System.out.println("voce ja tem uma conta? (sim/nao/sair)");
            String resposta = entrada.nextLine().trim().toLowerCase();

            if (resposta.equals("sair")) {
                System.out.println("ate logo!");
                return null;
            }

            if (resposta.equals("sim")) {
                System.out.println("digite seu email:");
                String email = entrada.nextLine().trim();

                System.out.println("digite sua senha:");
                String senha = entrada.nextLine().trim();

                Usuario usuario = gerenciadorUsuarios.autenticar(email, senha);

                if (usuario != null) {
                    return usuario.getNome();
                }

                System.out.println("email ou senha incorretos. tente novamente.");
            } else if (resposta.equals("nao")) {
                String nome = cadastrarUsuarioPeloTeclado(entrada, gerenciadorUsuarios);

                if (nome != null) {
                    return nome;
                }
            } else {
                System.out.println("responda apenas com 'sim', 'nao' ou 'sair'.");
            }
        }
    }

    public static String cadastrarUsuarioPeloTeclado(Scanner entrada, GerenciadorUsuarios gerenciadorUsuarios) {
        System.out.println("digite o nome do usuario:");
        String nome = entrada.nextLine().trim();

        System.out.println("digite o email do usuario:");
        String email = entrada.nextLine().trim();

        System.out.println("digite a senha do usuario:");
        String senha = entrada.nextLine().trim();

        Usuario usuario = new Usuario(nome, email, senha);

        if (gerenciadorUsuarios.adicionarUsuario(usuario)) {
            System.out.println("usuario cadastrado com sucesso!");
            return usuario.getNome();
        }

        System.out.println("nao foi possivel cadastrar o usuario. confira se os campos nao estao vazios ou se o email ja existe.");
        return null;
    }

    public static void listarUsuarios(GerenciadorUsuarios gerenciadorUsuarios) {
        if (gerenciadorUsuarios.getUsuarios().isEmpty()) {
            System.out.println("nenhum usuario cadastrado ainda.");
            return;
        }

        System.out.println("usuarios cadastrados:");

        for (Usuario usuario : gerenciadorUsuarios.getUsuarios()) {
            System.out.println("- " + usuario.getNome());
        }
    }

    public static void alterarUsuario(String nomeAntigo, String novoNome, GerenciadorUsuarios gerenciadorUsuarios) {
        if (novoNome.isEmpty()) {
            System.out.println("o novo nome nao pode ficar vazio.");
            return;
        }

        if (gerenciadorUsuarios.alterarNome(nomeAntigo, novoNome)) {
            System.out.println("usuario alterado com sucesso: " + nomeAntigo + " para " + novoNome);
        } else {
            System.out.println("usuario nao encontrado: " + nomeAntigo);
        }
    }

    public static void excluirUsuario(String nome, GerenciadorUsuarios gerenciadorUsuarios) {
        if (gerenciadorUsuarios.excluirUsuarioPorNome(nome)) {
            System.out.println("usuario excluido com sucesso: " + nome);
        } else {
            System.out.println("usuario nao encontrado: " + nome);
        }
    }

    public static void alterarEmail(String emailAntigo, String emailNovo, GerenciadorUsuarios gerenciadorUsuarios) {
        if (emailNovo.isEmpty()) {
            System.out.println("o novo email nao pode ficar vazio.");
            return;
        }

        if (!emailAntigo.equals(emailNovo) && gerenciadorUsuarios.emailJaExiste(emailNovo)) {
            System.out.println("ja existe um usuario cadastrado com esse email.");
            return;
        }

        if (gerenciadorUsuarios.alterarEmail(emailAntigo, emailNovo)) {
            System.out.println("email alterado com sucesso: " + emailAntigo + " para " + emailNovo);
        } else {
            System.out.println("usuario nao encontrado com email: " + emailAntigo);
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
}
