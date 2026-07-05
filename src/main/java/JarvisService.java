// Camada de servico: decide respostas simples sem depender da interface grafica.
public class JarvisService {
    private GerenciadorUsuarios gerenciadorUsuarios;
    private Calculadora calculadora;

    public JarvisService(GerenciadorUsuarios gerenciadorUsuarios) {
        this.gerenciadorUsuarios = gerenciadorUsuarios;
        this.calculadora = new Calculadora();
    }

    public String responder(String mensagem) {
        String comando = mensagem.trim().toLowerCase();

        if (comando.contains("hora")) {
            return "agora sao " + Datahora.obterHorarioAtual();
        }

        if (comando.contains("data")) {
            return "hoje e " + Datahora.obterDataAtual();
        }

        if (calculadora.temOperacao(comando)) {
            return calculadora.calcular(comando);
        }

        if (comando.equals("listar usuarios")) {
            return listarUsuarios();
        }

        if (comando.startsWith("alterar nome")) {
            return alterarNome(mensagem);
        }

        return null;
    }

    private String alterarNome(String mensagem) {
        String comando = mensagem.trim().toLowerCase();

        // Extrai o que vem depois de "alterar nome".
        String[] partes = comando.split("alterar nome", 2);

        if (partes.length < 2 || partes[1].trim().isEmpty()) {
            return null;
        }

        // Se chegar dois nomes separados por "para", processa no service.
        String resto = partes[1].trim();
        if (resto.contains(" para ")) {
            String[] nomes = resto.split(" para ", 2);
            String nomeAntigo = nomes[0].trim();
            String novoNome = nomes[1].trim();

            if (nomeAntigo.isEmpty() || novoNome.isEmpty()) {
                return "nomes nao podem ficar vazios.";
            }

            boolean alterado = gerenciadorUsuarios.alterarNome(nomeAntigo, novoNome);

            if (alterado) {
                return "nome alterado com sucesso: " + nomeAntigo + " para " + novoNome;
            }

            return "usuario nao encontrado com nome: " + nomeAntigo;
        }

        return null;
    }

    private String listarUsuarios() {
        if (gerenciadorUsuarios.getUsuarios().isEmpty()) {
            return "nenhum usuario cadastrado ainda.";
        }

        StringBuilder texto = new StringBuilder("usuarios cadastrados:");

        for (Usuario usuario : gerenciadorUsuarios.getUsuarios()) {
            texto.append("\n- ").append(usuario.getNome());
        }

        return texto.toString();
    }

    private String excluirUsuario(String mensagem) {
        String comando = mensagem.trim().toLowerCase();

        // Extrai o que vem depois de "excluir usuario".
        String[] partes = comando.split("excluir usuario", 2);

        if (partes.length < 2 || partes[1].trim().isEmpty()) {
            return null;
        }

        String nomeUsuario = partes[1].trim();
        boolean excluido = gerenciadorUsuarios.excluirUsuarioPorNome(nomeUsuario);

        if (excluido) {
            return "usuario excluido com sucesso: " + nomeUsuario;
        }

        return "usuario nao encontrado com nome: " + nomeUsuario;
    }

    /* Processa o comando recebido e retorna a resposta apropriada.
    futuramente esse metodo pode ser expandido para processar outros tipos de comandos
    e sera o cerebro do sistema. em uma classe que ja e o cerebro do codigo */
    
    public String processarComando(String mensagem) {
        String resposta = responder(mensagem);

        if (resposta != null) {
            return resposta;
        }

        // Se não for nenhum comando conhecido, tenta excluir usuário.
        resposta = excluirUsuario(mensagem);
        if (resposta != null) {
            return resposta;
        }

        return "desculpe, nao entendi o comando.";
    }
}
