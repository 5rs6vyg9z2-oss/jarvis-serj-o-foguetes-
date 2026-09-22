package br.com.meira.jarvis.service;

import br.com.meira.jarvis.model.Usuario;
import br.com.meira.jarvis.util.Calculadora;
import br.com.meira.jarvis.util.Datahora;
import org.springframework.stereotype.Service;

// Centraliza a interpretacao dos textos recebidos pela interface.
@Service
public class JarvisService {
    private final GerenciadorUsuarios gerenciadorUsuarios;
    private final Calculadora calculadora;

    public JarvisService(GerenciadorUsuarios gerenciadorUsuarios) {
        this.gerenciadorUsuarios = gerenciadorUsuarios;
        this.calculadora = new Calculadora();
    }

    // Identifica a area do comando e encaminha para a classe ou metodo responsavel.
    public String processarComando(String texto) {
        String intencao = identificarIntencao(texto);

        if (intencao == null) {
            return respostaDesconhecida();
        }

        switch (intencao) {

            case "agradecerUsuario":
                return agradecerUsuario();

            case "perguntaPadrao":
                return perguntaPadrao();

            case "lembrarDeUsuario":
                return responderMemoriaUsuario();

            case "responderPerguntaUsuario":
                return responderPerguntaUsuario();

            case "saudacao":
                return responderSaudacao();

            case "lembrar usuario":
                return responderMemoriaUsuario();

            case "hora":
            case "data":
                return processarDataHoraComando(texto);

            case "calculadora":
                return processarCalculadoraComando(texto);

            case "excluir usuario":
            case "alterar usuario":
            case "listar usuarios":
            case "alterar senha":
                String respostaUsuario = processarComandos(texto);
                return respostaUsuario != null
                        ? respostaUsuario
                        : "comando de usuario incompleto. Tente informar todos os dados pedidos.";

            case "adicionar usuario":
                return orientarCadastroUsuario();

            default:
                return respostaDesconhecida();
        }
    }

    // Mantem o nome antigo para telas ou codigos que ainda chamam responder.
    public String responder(String texto) {
        return processarComando(texto);
    }

    // Aceita sinonimos, mas produz uma frase padrao que os metodos abaixo entendem.
    private String processarComandos(String texto) {
        return processarUsuarioComando(normalizarComandoUsuario(texto));
    }

    // Encaminha somente comandos de usuario ja colocados no formato padrao.
    private String processarUsuarioComando(String texto) {
        String comando = texto.trim().toLowerCase();

        if (comando.startsWith("alterar nome")) {
            return alterarNome(texto);
        }

        if (comando.equals("listar usuarios")) {
            return listarUsuarios();
        }

        if (comando.startsWith("excluir usuario")) {
            return excluirUsuario(texto);
        }

        if (comando.startsWith("alterar email")) {
            return alterarEmail(texto);
        }

        if (comando.startsWith("alterar senha")) {
            return orientarAlteracaoSenha();
        }

        return null;
    }

    // Converte apenas o inicio do comando; os dados informados pelo usuario sao preservados.
    private String normalizarComandoUsuario(String texto) {
        String original = texto.trim();
        String comparacao = original.toLowerCase();

        if (comparacao.startsWith("salvar usuario ")) {
            return "adicionar usuario " + original.substring("salvar usuario ".length()).trim();
        }

        if (comparacao.startsWith("cadastrar usuario ")) {
            return "adicionar usuario " + original.substring("cadastrar usuario ".length()).trim();
        }

        if (comparacao.startsWith("criar usuario ")) {
            return "adicionar usuario " + original.substring("criar usuario ".length()).trim();
        }

        if (comparacao.startsWith("inserir usuario ")) {
            return "adicionar usuario " + original.substring("inserir usuario ".length()).trim();
        }

        if (comparacao.startsWith("remover usuario ")) {
            return "excluir usuario " + original.substring("remover usuario ".length()).trim();
        }

        if (comparacao.startsWith("apagar usuario ")) {
            return "excluir usuario " + original.substring("apagar usuario ".length()).trim();
        }

        if (comparacao.startsWith("deletar usuario ")) {
            return "excluir usuario " + original.substring("deletar usuario ".length()).trim();
        }

        if (comparacao.startsWith("delete usuario ")) {
            return "excluir usuario " + original.substring("delete usuario ".length()).trim();
        }

        if (comparacao.startsWith("mudar nome ")) {
            return "alterar nome " + original.substring("mudar nome ".length()).trim();
        }

        if (comparacao.startsWith("modificar nome ")) {
            return "alterar nome " + original.substring("modificar nome ".length()).trim();
        }

        if (comparacao.startsWith("mudar email ")) {
            return "alterar email " + original.substring("mudar email ".length()).trim();
        }

        if (comparacao.startsWith("modificar email ")) {
            return "alterar email " + original.substring("modificar email ".length()).trim();
        }

        if (comparacao.equals("mostrar usuarios") || comparacao.equals("mostrar usuario")
                || comparacao.equals("ver usuarios") || comparacao.equals("ver usuario")
                || comparacao.equals("listar usuario")) {
            return "listar usuarios";
        }

        if (comparacao.startsWith("alterar a senha") || comparacao.startsWith("mudar senha")
                || comparacao.startsWith("mudar a senha") || comparacao.startsWith("trocar senha")
                || comparacao.startsWith("trocar a senha") || comparacao.startsWith("modificar senha")
                || comparacao.startsWith("modificar a senha")) {
            return "alterar senha";
        }

        return original;
    }

    // Formato: alterar nome nome antigo para nome novo.
    private String alterarNome(String texto) {
        String resto = extrairRestoDoComando(texto, "alterar nome");

        if (resto == null) {
            return "informe o nome antigo e o novo: alterar nome Carlos para Joao.";
        }

        String[] nomes = separarPorPara(resto);
        if (nomes == null) {
            return "informe o nome antigo e o novo: alterar nome Carlos para Joao.";
        }

        boolean alterado = gerenciadorUsuarios.alterarNome(nomes[0], nomes[1]);
        if (alterado) {
            return "nome alterado com sucesso: " + nomes[0] + " para " + nomes[1];
        }

        return "usuario nao encontrado com nome: " + nomes[0];
    }

    private String listarUsuarios() {
        if (gerenciadorUsuarios.getUsuarios().isEmpty()) {
            return "nenhum usuario cadastrado ainda.";
        }

        StringBuilder resposta = new StringBuilder("usuarios cadastrados:");
        for (Usuario usuario : gerenciadorUsuarios.getUsuarios()) {
            resposta.append("\n- ").append(usuario.getNome());
        }

        return resposta.toString();
    }

    // Formato: excluir usuario nome do usuario.
    private String excluirUsuario(String texto) {
        String nomeUsuario = extrairRestoDoComando(texto, "excluir usuario");

        if (nomeUsuario == null) {
            return "informe o nome: excluir usuario Carlos.";
        }

        boolean excluido = gerenciadorUsuarios.excluirUsuarioPorNome(nomeUsuario);
        if (excluido) {
            return "usuario excluido com sucesso: " + nomeUsuario;
        }

        return "usuario nao encontrado com nome: " + nomeUsuario;
    }

    // Formato: alterar email email-antigo para email-novo.
    private String alterarEmail(String texto) {
        String resto = extrairRestoDoComando(texto, "alterar email");

        if (resto == null) {
            return "informe os dois emails: alterar email antigo@exemplo.com para novo@exemplo.com.";
        }

        String[] emails = separarPorPara(resto);
        if (emails == null) {
            return "informe os dois emails: alterar email antigo@exemplo.com para novo@exemplo.com.";
        }

        boolean alterado = gerenciadorUsuarios.alterarEmail(emails[0], emails[1]);
        if (alterado) {
            return "email alterado com sucesso: " + emails[0] + " para " + emails[1];
        }

        return "nao foi possivel alterar o email. Confira o email antigo e se o novo ja nao esta em uso.";
    }

    // Senhas nao devem ser enviadas pela conversa, pois a rota de mensagem usa GET.
    private String orientarCadastroUsuario() {
        return "para criar uma conta, use o botao Criar conta na tela de login.";
    }

    // Senhas nao devem ser enviadas pela conversa, pois a rota de mensagem usa GET.
    private String orientarAlteracaoSenha() {
        return "para alterar sua senha, use a acao segura de configuracoes da conta.";
    }

    private String processarCalculadoraComando(String texto) {
        if (calculadora.temOperacao(texto)) {
            return calculadora.calcular(texto);
        }

        return "digite uma conta com dois numeros, por exemplo: 10 mais 5.";
    }

    private String processarDataHoraComando(String texto) {
        String comando = texto.trim().toLowerCase();

        if (comando.contains("hora") || comando.contains("horario") || comando.contains("time")) {
            return "agora sao " + Datahora.obterHorarioAtual();
        }

        return "hoje e " + Datahora.obterDataAtual();
    }

    private String responderSaudacao() {
        return "ola! Eu sou o Jarvis. Como posso ajudar?";
    }

    private String responderPerguntaUsuario(){
        return "ta tudo bem sim,serei uma ia muito grande e forte!! voce e normal de perguntar se uma maquina esta bem ou mal?";
    }

    private String responderMemoriaUsuario() {
        return "ainda nao consigo identificar sua conta apenas pela conversa. Estou evoluindo essa parte da sessao.";
    }

    private String identificarIntencao(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return null;
        }

        String texto = normalizarTexto(mensagem);

           if (texto.contains("obrigado")
              || (texto.contains("voce e o melhor"))
              || (texto.contains("caraca que incrivel"))
              || (texto.contains("vlw"))
              ||(texto.contains("valeu"))){
                return "agradecerUsuario";
              }

            if (texto.contains("oque voce faz")
                 || (texto.contains("qual suas funções"))
                 || (texto.contains("quais suas habilidades"))){
                 return "perguntaPadrao";
                 }
        
            if (texto.contains("ola, como vai")
                  || texto.contains("tudo bem")
                  || texto.contains("como voce esta")) {
              return "responderPerguntaUsuario";
}

        if (ehSaudacao(texto)) {
            return "saudacao";
        }

        if (texto.contains("lembra de mim") || texto.contains("me conhece")) {
            return "lembrar usuario";
        }

        if (texto.contains("senha") && (texto.contains("alterar") || texto.contains("mudar")
                || texto.contains("trocar") || texto.contains("modificar"))) {
            return "alterar senha";
        }

        if ((texto.contains("apagar") || texto.contains("excluir") || texto.contains("deletar")
                || texto.contains("delete") || texto.contains("remover")) && texto.contains("usuario")) {
            return "excluir usuario";
        }

        if ((texto.contains("alterar") || texto.contains("mudar") || texto.contains("modificar")
                || texto.contains("update")) && (texto.contains("nome") || texto.contains("email"))) {
            return "alterar usuario";
        }

        if ((texto.contains("adicionar") || texto.contains("salvar") || texto.contains("cadastrar")
                || texto.contains("inserir") || texto.contains("criar") || texto.contains("create"))
                && texto.contains("usuario")) {
            return "adicionar usuario";
        }

        if ((texto.contains("listar") || texto.contains("ver") || texto.contains("mostrar")
                || texto.contains("list")) && texto.contains("usuario")) {
            return "listar usuarios";
        }

        if (texto.contains("hora") || texto.contains("horario") || texto.contains("time")) {
            return "hora";
        }

        if (texto.contains("data") || texto.contains("dia") || texto.contains("calendario")
                || texto.contains("today") || texto.contains("date")) {
            return "data";
        }

        if (calculadora.temOperacao(texto) || texto.contains("calcular") || texto.contains("resolver")
                || texto.contains("operacao") || texto.contains("quanto e") || texto.contains("resultado")) {
            return "calculadora";
        }

        return null;
    }

    private String agradecerUsuario(){
        return "de nada meu amigo. eu ainda estarei aqui para te ajudar sempre que você precisar. meu criador me ensinou a ser como ele, amigavel gentil e prestativo.";
    }

        private String perguntaPadrao(){
            return "minhas funções sao limitadas ao momento, mas eu posso fazer: cadastro,login,alteração de informações dos usuarios, calculadora, te informar data e hora. mas meu LINDO E BELO E FORTE criador esta me desenvolvendo com as vozes da cabeça dele.";

        }

    private boolean ehSaudacao(String texto) {
        return texto.equals("ola")
                || texto.equals("oi")
                || texto.equals("ola jarvis")
                || texto.equals("oi jarvis")
                || texto.equals("bom dia")
                || texto.equals("boa tarde")
                || texto.equals("boa noite")
                || texto.equals("ola tudo bem")
                || texto.equals("bom dia como vai");
    }

    private String normalizarTexto(String texto) {
        return java.text.Normalizer.normalize(texto.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private String extrairRestoDoComando(String texto, String inicio) {
        String original = texto.trim();
        if (!original.toLowerCase().startsWith(inicio)) {
            return null;
        }

        String resto = original.substring(inicio.length()).trim();
        return resto.isEmpty() ? null : resto;
    }

    private String[] separarPorPara(String texto) {
        String marcador = " para ";
        int indice = texto.toLowerCase().indexOf(marcador);

        if (indice < 1 || indice + marcador.length() >= texto.length()) {
            return null;
        }

        String primeiroValor = texto.substring(0, indice).trim();
        String segundoValor = texto.substring(indice + marcador.length()).trim();

        if (primeiroValor.isEmpty() || segundoValor.isEmpty()) {
            return null;
        }

        return new String[] {primeiroValor, segundoValor};
    }

    private String respostaDesconhecida() {
        return "desculpa, ainda nao sei como responder a isso. Tente outro comando ou pergunte sobre hora, data, calculadora ou usuarios.";
    }
}
