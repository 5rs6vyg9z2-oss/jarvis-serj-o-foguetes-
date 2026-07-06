// Camada de servico: decide respostas simples sem depender da interface grafica.
public class JarvisService {
    private GerenciadorUsuarios gerenciadorUsuarios;
    private Calculadora calculadora;

    public JarvisService(GerenciadorUsuarios gerenciadorUsuarios) {
        this.gerenciadorUsuarios = gerenciadorUsuarios;
        this.calculadora = new Calculadora();
    }

    public String processarComando(String mensagem) {
        String resposta = processarDataHoraComando(mensagem);
    
        if (resposta != null) {
            return resposta;
        }
    
        resposta = processarCalculadoraComando(mensagem);
    
        if (resposta != null) {
            return resposta;
        }
    
        resposta = processarUsuarioComando(mensagem);
    
        if (resposta != null) {
            return resposta;
        }
    
        return null;
    }

    
// Processa a mensagem recebida e retorna a resposta apropriada, verificando se a mensagem contém comandos relacionados a data, hora, calculadora ou usuários.
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
// Processa o comando de alterar nome, verificando se o formato da mensagem está correto e chamando o gerenciador de usuários para realizar a alteração.
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
/* a excluir usuario alem de ser auto explicativo, futuramente pode ser movida para o processarUsuarioComando */
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
<<<<<<< HEAD
    
    public String processarComando(String mensagem) {
        String resposta = processarDataHoraComando(mensagem);
=======
    /* 
    Processa comandos relacionados a usuários, como alterar nome, listar. o excluir e funçao direta do processarComando, mas pode ser movida para o
    processarUsuarioComando futuramente. 
        */ 
    private String processarUsuarioComando(String mensagem) {
        String comando = mensagem.trim().toLowerCase();
>>>>>>> feature/separar-logica-da-interface

        if (comando.startsWith("alterar nome")) {
            return alterarNome(mensagem);
        }

<<<<<<< HEAD
        resposta = processarCalculadoraComando(mensagem);

        if (resposta != null) {
            return resposta;
        }

        resposta = processarUsuarioComando(mensagem);

        if (resposta != null) {
            return resposta;
        }

        return null;
    }
    /* 
    Processa comandos relacionados a usuários, como alterar nome, listar. o excluir e funçao direta do processarComando, mas pode ser movida para o
    processarUsuarioComando futuramente. 
        */ 
    private String processarUsuarioComando(String mensagem) {
        String comando = mensagem.trim().toLowerCase();

        if (comando.startsWith("alterar nome")) {
            return alterarNome(mensagem);
        }

        if (comando.equals("listar usuarios")) {
            return listarUsuarios();
        }

=======
        if (comando.equals("listar usuarios")) {
            return listarUsuarios();
        }

>>>>>>> feature/separar-logica-da-interface
        if (comando.startsWith("excluir usuario")) {
            return excluirUsuario(mensagem);
        }

        return null;
    }

    // Processa comandos relacionados a calculadora, como somar, subtrair, multiplicar ou dividir.
    private String processarCalculadoraComando(String mensagem) {
        String comando = mensagem.trim().toLowerCase();

        if (calculadora.temOperacao(comando)) {
            return calculadora.calcular(comando);
        }

        return null;
    }

    // Processa comandos relacionados a data e hora, como "que horas sao" ou "qual a data de hoje".
    private String processarDataHoraComando(String mensagem) {
        String comando = mensagem.trim().toLowerCase();

        if (comando.contains("hora")) {
            return "agora sao " + Datahora.obterHorarioAtual();
        }

        if (comando.contains("data")) {
            return "hoje e " + Datahora.obterDataAtual();
        }

        return null;
    }
}

