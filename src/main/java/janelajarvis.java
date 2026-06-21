import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class JanelaJarvis {
    private static final String TELA_LOGIN = "login";
    private static final String TELA_CADASTRO = "cadastro";
    private static final String TELA_CHAT = "chat";

    private static final Color FUNDO = new Color(9, 12, 18);
    private static final Color PAINEL = new Color(18, 24, 34);
    private static final Color CAMPO = new Color(28, 36, 50);
    private static final Color TEXTO = new Color(235, 240, 247);
    private static final Color TEXTO_FRACO = new Color(158, 169, 184);
    private static final Color DESTAQUE = new Color(56, 189, 248);
    private static final Color DESTAQUE_ESCURO = new Color(14, 116, 144);
    private static final Color BORDA = new Color(45, 56, 74);

    private JFrame janela;
    private JPanel painelPrincipal;
    private CardLayout cardLayout;

    private JTextField campoEmailLogin;
    private JPasswordField campoSenhaLogin;

    private JTextField campoNomeCadastro;
    private JTextField campoEmailCadastro;
    private JPasswordField campoSenhaCadastro;

    private JTextArea areaConversa;
    private JTextField campoMensagem;
    private JLabel labelUsuarioLogado;

    private GerenciadorUsuarios gerenciadorUsuarios;
    private Calculadora calculadora;
    private Usuario usuarioLogado;
    private String acaoPendente = "";
    private String nomeTemporario = "";
    private String emailTemporario = "";

    public JanelaJarvis() {
        gerenciadorUsuarios = new GerenciadorUsuarios();
        calculadora = new Calculadora();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JanelaJarvis app = new JanelaJarvis();
            app.iniciar();
        });
    }

    public void iniciar() {
        janela = new JFrame("Jarvis");
        janela.setSize(980, 680);
        janela.setMinimumSize(new Dimension(760, 560));
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        painelPrincipal.setBackground(FUNDO);

        painelPrincipal.add(criarTelaLogin(), TELA_LOGIN);
        painelPrincipal.add(criarTelaCadastro(), TELA_CADASTRO);
        painelPrincipal.add(criarTelaChat(), TELA_CHAT);

        janela.add(painelPrincipal);
        cardLayout.show(painelPrincipal, TELA_LOGIN);
        janela.setVisible(true);
    }

    private JPanel criarTelaLogin() {
        JPanel tela = criarTelaCentralizada();
        JPanel cartao = criarCartao(420);

        JLabel titulo = criarTitulo("Jarvis");
        JLabel subtitulo = criarSubtitulo("Acesse sua conta para iniciar a conversa.");

        campoEmailLogin = criarCampoTexto();
        campoSenhaLogin = criarCampoSenha();
        campoSenhaLogin.addActionListener(evento -> entrar());

        JButton botaoEntrar = criarBotaoPrincipal("Entrar");
        botaoEntrar.addActionListener(evento -> entrar());

        JButton botaoCadastro = criarBotaoSecundario("Criar conta");
        botaoCadastro.addActionListener(evento -> {
            limparCadastro();
            cardLayout.show(painelPrincipal, TELA_CADASTRO);
            campoNomeCadastro.requestFocusInWindow();
        });

        adicionarLinha(cartao, titulo, 0);
        adicionarLinha(cartao, subtitulo, 1);
        adicionarEspaco(cartao, 2, 12);
        adicionarCampo(cartao, "Email", campoEmailLogin, 3);
        adicionarCampo(cartao, "Senha", campoSenhaLogin, 5);
        adicionarEspaco(cartao, 7, 10);
        adicionarLinha(cartao, botaoEntrar, 8);
        adicionarLinha(cartao, botaoCadastro, 9);

        tela.add(cartao);
        return tela;
    }

    private JPanel criarTelaCadastro() {
        JPanel tela = criarTelaCentralizada();
        JPanel cartao = criarCartao(420);

        JLabel titulo = criarTitulo("Criar conta");
        JLabel subtitulo = criarSubtitulo("Cadastre nome, email e senha.");

        campoNomeCadastro = criarCampoTexto();
        campoEmailCadastro = criarCampoTexto();
        campoSenhaCadastro = criarCampoSenha();
        campoSenhaCadastro.addActionListener(evento -> cadastrar());

        JButton botaoCadastrar = criarBotaoPrincipal("Cadastrar");
        botaoCadastrar.addActionListener(evento -> cadastrar());

        JButton botaoVoltar = criarBotaoSecundario("Voltar para login");
        botaoVoltar.addActionListener(evento -> {
            limparLogin();
            cardLayout.show(painelPrincipal, TELA_LOGIN);
            campoEmailLogin.requestFocusInWindow();
        });

        adicionarLinha(cartao, titulo, 0);
        adicionarLinha(cartao, subtitulo, 1);
        adicionarEspaco(cartao, 2, 12);
        adicionarCampo(cartao, "Nome", campoNomeCadastro, 3);
        adicionarCampo(cartao, "Email", campoEmailCadastro, 5);
        adicionarCampo(cartao, "Senha", campoSenhaCadastro, 7);
        adicionarEspaco(cartao, 9, 10);
        adicionarLinha(cartao, botaoCadastrar, 10);
        adicionarLinha(cartao, botaoVoltar, 11);

        tela.add(cartao);
        return tela;
    }

    private JPanel criarTelaChat() {
        JPanel tela = new JPanel(new BorderLayout(0, 0));
        tela.setBackground(FUNDO);
        tela.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(FUNDO);

        JLabel titulo = new JLabel("Jarvis");
        titulo.setForeground(TEXTO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        labelUsuarioLogado = new JLabel("Nao conectado");
        labelUsuarioLogado.setForeground(TEXTO_FRACO);
        labelUsuarioLogado.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel blocoTitulo = new JPanel(new BorderLayout());
        blocoTitulo.setBackground(FUNDO);
        blocoTitulo.add(titulo, BorderLayout.NORTH);
        blocoTitulo.add(labelUsuarioLogado, BorderLayout.SOUTH);

        JButton botaoSair = criarBotaoSecundario("Sair");
        botaoSair.addActionListener(evento -> sairDaSessao());

        topo.add(blocoTitulo, BorderLayout.WEST);
        topo.add(botaoSair, BorderLayout.EAST);

        areaConversa = new JTextArea();
        areaConversa.setEditable(false);
        areaConversa.setLineWrap(true);
        areaConversa.setWrapStyleWord(true);
        areaConversa.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        areaConversa.setForeground(TEXTO);
        areaConversa.setBackground(PAINEL);
        areaConversa.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        areaConversa.setCaretColor(TEXTO);

        JScrollPane rolagem = new JScrollPane(areaConversa);
        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));

        campoMensagem = criarCampoTexto();
        campoMensagem.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campoMensagem.addActionListener(evento -> enviarMensagem());

        tela.add(topo, BorderLayout.NORTH);
        tela.add(rolagem, BorderLayout.CENTER);
        tela.add(campoMensagem, BorderLayout.SOUTH);

        return tela;
    }

    private void entrar() {
        String email = campoEmailLogin.getText().trim();
        String senha = new String(campoSenhaLogin.getPassword()).trim();

        if (!dadosLoginValidos(email, senha)) {
            return;
        }

        Usuario usuario = gerenciadorUsuarios.autenticar(email, senha);

        if (usuario == null) {
            mostrarAviso("Email ou senha incorretos.");
            return;
        }

        abrirChat(usuario);
    }

    private void cadastrar() {
        String nome = campoNomeCadastro.getText().trim();
        String email = campoEmailCadastro.getText().trim();
        String senha = new String(campoSenhaCadastro.getPassword()).trim();

        if (!dadosCadastroValidos(nome, email, senha)) {
            return;
        }

        Usuario usuario = new Usuario(nome, email, senha);

        if (!gerenciadorUsuarios.adicionarUsuario(usuario)) {
            mostrarAviso("Nao foi possivel cadastrar. Confira os dados.");
            return;
        }

        abrirChat(usuario);
    }

    private boolean dadosLoginValidos(String email, String senha) {
        if (email.isEmpty()) {
            mostrarAviso("Digite seu email.");
            return false;
        }

        if (senha.isEmpty()) {
            mostrarAviso("Digite sua senha.");
            return false;
        }

        if (!emailValido(email)) {
            mostrarAviso("Digite um email valido.");
            return false;
        }

        return true;
    }

    private boolean dadosCadastroValidos(String nome, String email, String senha) {
        if (nome.isEmpty()) {
            mostrarAviso("Digite seu nome.");
            return false;
        }

        if (!dadosLoginValidos(email, senha)) {
            return false;
        }

        if (gerenciadorUsuarios.emailJaExiste(email)) {
            mostrarAviso("Ja existe uma conta com esse email.");
            return false;
        }

        return true;
    }

    private boolean emailValido(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private void abrirChat(Usuario usuario) {
        usuarioLogado = usuario;
        labelUsuarioLogado.setText("Conectado como " + usuarioLogado.getNome());

        areaConversa.setText("");
        areaConversa.append("Jarvis: Ola, " + usuarioLogado.getNome() + ". Como posso ajudar?\n");
        areaConversa.append("Jarvis: Voce pode perguntar a hora, a data ou digitar uma conta.\n");
        areaConversa.append("Jarvis: Tambem aceito: cadastrar usuario, listar usuarios, alterar nome, alterar email e excluir usuario.\n");

        campoMensagem.setText("");
        cardLayout.show(painelPrincipal, TELA_CHAT);
        SwingUtilities.invokeLater(() -> campoMensagem.requestFocusInWindow());
    }

    private void enviarMensagem() {
        String mensagem = campoMensagem.getText().trim();

        if (mensagem.isEmpty()) {
            return;
        }

        areaConversa.append("Voce: " + mensagem + "\n");
        campoMensagem.setText("");

        String resposta = responder(mensagem);
        areaConversa.append("Jarvis: " + resposta + "\n");
        areaConversa.setCaretPosition(areaConversa.getDocument().getLength());
    }

    private String responder(String mensagem) {
        String comando = mensagem.trim().toLowerCase();

        if (!acaoPendente.isEmpty()) {
            return continuarAcaoPendente(mensagem);
        }

        if (comando.equals("sair")
                || comando.equals("ate")
                || comando.equals("obrigado")) {
            String nome = usuarioLogado.getNome();
            sairDaSessao();
            return "ate logo, " + nome + "!";
        }

        if (comando.contains("hora")) {
            return "agora sao " + Datahora.obterHorarioAtual();
        }

        if (comando.contains("data")) {
            return "hoje e " + Datahora.obterDataAtual();
        }

        if (calculadora.temOperacao(comando)) {
            return calculadora.calcular(comando);
        }

        if (comando.equals("cadastrar usuario")) {
            acaoPendente = "cadastrar_nome";
            return "digite o nome do usuario:";
        }

        if (comando.equals("listar usuarios")) {
            return listarUsuariosNaTela();
        }

        if (comando.equals("alterar nome")) {
            acaoPendente = "alterar_nome_antigo";
            return listarUsuariosNaTela() + "\nDigite o nome que deseja alterar:";
        }

        if (comando.equals("excluir usuario")) {
            acaoPendente = "excluir_nome";
            return listarUsuariosNaTela() + "\nDigite o nome que deseja excluir:";
        }

        if (comando.equals("alterar email")) {
            acaoPendente = "alterar_email_antigo";
            return listarUsuariosNaTela() + "\nDigite o email que deseja alterar:";
        }

        return "ainda estou aprendendo a responder isso.";
    }

    private String continuarAcaoPendente(String mensagem) {
        String resposta = mensagem.trim();

        if (acaoPendente.equals("cadastrar_nome")) {
            if (resposta.isEmpty()) {
                return "o nome nao pode ficar vazio. Digite o nome do usuario:";
            }

            nomeTemporario = resposta;
            acaoPendente = "cadastrar_email";
            return "digite o email do usuario:";
        }

        if (acaoPendente.equals("cadastrar_email")) {
            if (!emailValido(resposta)) {
                return "digite um email valido:";
            }

            if (gerenciadorUsuarios.emailJaExiste(resposta)) {
                limparAcaoPendente();
                return "ja existe um usuario cadastrado com esse email.";
            }

            emailTemporario = resposta;
            acaoPendente = "cadastrar_senha";
            return "digite a senha do usuario:";
        }

        if (acaoPendente.equals("cadastrar_senha")) {
            if (resposta.isEmpty()) {
                return "a senha nao pode ficar vazia. Digite a senha do usuario:";
            }

            Usuario usuario = new Usuario(nomeTemporario, emailTemporario, resposta);
            boolean cadastrado = gerenciadorUsuarios.adicionarUsuario(usuario);
            limparAcaoPendente();

            if (cadastrado) {
                return "usuario cadastrado com sucesso: " + usuario.getNome();
            }

            return "nao foi possivel cadastrar o usuario.";
        }

        if (acaoPendente.equals("alterar_nome_antigo")) {
            if (resposta.isEmpty()) {
                return "digite o nome que deseja alterar:";
            }

            nomeTemporario = resposta;
            acaoPendente = "alterar_nome_novo";
            return "digite o novo nome:";
        }

        if (acaoPendente.equals("alterar_nome_novo")) {
            if (resposta.isEmpty()) {
                return "o novo nome nao pode ficar vazio. Digite o novo nome:";
            }

            boolean alterado = gerenciadorUsuarios.alterarNome(nomeTemporario, resposta);
            limparAcaoPendente();

            if (alterado) {
                if (usuarioLogado != null) {
                    labelUsuarioLogado.setText("Conectado como " + usuarioLogado.getNome());
                }

                return "usuario alterado com sucesso: " + nomeTemporario + " para " + resposta;
            }

            return "usuario nao encontrado: " + nomeTemporario;
        }

        if (acaoPendente.equals("excluir_nome")) {
            if (resposta.isEmpty()) {
                return "digite o nome que deseja excluir:";
            }

            String nomeLogado = usuarioLogado.getNome();
            boolean excluido = gerenciadorUsuarios.excluirUsuarioPorNome(resposta);
            limparAcaoPendente();

            if (!excluido) {
                return "usuario nao encontrado: " + resposta;
            }

            if (nomeLogado.equals(resposta)) {
                sairDaSessao();
                return "usuario excluido com sucesso. Como era o usuario logado, a sessao foi encerrada.";
            }

            return "usuario excluido com sucesso: " + resposta;
        }

        if (acaoPendente.equals("alterar_email_antigo")) {
            if (resposta.isEmpty()) {
                return "digite o email que deseja alterar:";
            }

            emailTemporario = resposta;
            acaoPendente = "alterar_email_novo";
            return "digite o novo email:";
        }

        if (acaoPendente.equals("alterar_email_novo")) {
            if (!emailValido(resposta)) {
                return "digite um email valido:";
            }

            if (!emailTemporario.equals(resposta) && gerenciadorUsuarios.emailJaExiste(resposta)) {
                limparAcaoPendente();
                return "ja existe um usuario cadastrado com esse email.";
            }

            boolean alterado = gerenciadorUsuarios.alterarEmail(emailTemporario, resposta);
            limparAcaoPendente();

            if (alterado) {
                return "email alterado com sucesso: " + emailTemporario + " para " + resposta;
            }

            return "usuario nao encontrado com email: " + emailTemporario;
        }

        limparAcaoPendente();
        return "nao consegui continuar essa acao.";
    }

    private String listarUsuariosNaTela() {
        if (gerenciadorUsuarios.getUsuarios().isEmpty()) {
            return "nenhum usuario cadastrado ainda.";
        }

        StringBuilder texto = new StringBuilder("usuarios cadastrados:");

        for (Usuario usuario : gerenciadorUsuarios.getUsuarios()) {
            texto.append("\n- ").append(usuario.getNome());
        }

        return texto.toString();
    }

    private void sairDaSessao() {
        limparAcaoPendente();
        usuarioLogado = null;
        labelUsuarioLogado.setText("Nao conectado");
        limparLogin();
        cardLayout.show(painelPrincipal, TELA_LOGIN);
        SwingUtilities.invokeLater(() -> campoEmailLogin.requestFocusInWindow());
    }

    private JPanel criarTelaCentralizada() {
        JPanel tela = new JPanel(new GridBagLayout());
        tela.setBackground(FUNDO);
        tela.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        return tela;
    }

    private JPanel criarCartao(int largura) {
        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(PAINEL);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)));
        cartao.setPreferredSize(new Dimension(largura, 460));
        return cartao;
    }

    private JLabel criarTitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setForeground(TEXTO);
        label.setFont(new Font("Segoe UI", Font.BOLD, 30));
        return label;
    }

    private JLabel criarSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setForeground(TEXTO_FRACO);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        estilizarCampo(campo);
        return campo;
    }

    private JPasswordField criarCampoSenha() {
        JPasswordField campo = new JPasswordField();
        estilizarCampo(campo);
        return campo;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(CAMPO);
        campo.setForeground(TEXTO);
        campo.setCaretColor(TEXTO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
    }

    private JButton criarBotaoPrincipal(String texto) {
        JButton botao = criarBotaoBase(texto);
        botao.setBackground(DESTAQUE);
        botao.setForeground(FUNDO);
        return botao;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton botao = criarBotaoBase(texto);
        botao.setBackground(DESTAQUE_ESCURO);
        botao.setForeground(TEXTO);
        return botao;
    }

    private JButton criarBotaoBase(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setPreferredSize(new Dimension(120, 42));
        return botao;
    }

    private void adicionarCampo(JPanel painel, String texto, JTextField campo, int linha) {
        JLabel label = new JLabel(texto);
        label.setForeground(TEXTO_FRACO);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        adicionarLinha(painel, label, linha);
        adicionarLinha(painel, campo, linha + 1);
    }

    private void adicionarLinha(JPanel painel, Component componente, int linha) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        painel.add(componente, gbc);
    }

    private void adicionarEspaco(JPanel painel, int linha, int altura) {
        JPanel espaco = new JPanel();
        espaco.setOpaque(false);
        espaco.setPreferredSize(new Dimension(1, altura));
        adicionarLinha(painel, espaco, linha);
    }

    private void limparLogin() {
        campoEmailLogin.setText("");
        campoSenhaLogin.setText("");
    }

    private void limparCadastro() {
        campoNomeCadastro.setText("");
        campoEmailCadastro.setText("");
        campoSenhaCadastro.setText("");
    }

    private void limparAcaoPendente() {
        acaoPendente = "";
        nomeTemporario = "";
        emailTemporario = "";
    }

    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(janela, mensagem);
    }
}
