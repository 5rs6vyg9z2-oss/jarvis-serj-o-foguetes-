import java.util.List;

// Centraliza as regras de usuario entre a tela/terminal e o banco de dados.
public class GerenciadorUsuarios {
    // Lista em memoria usada pelo Jarvis enquanto o programa esta aberto.
    private List<Usuario> usuarios;

    // Repository faz a parte de persistencia: gravar e buscar no SQLite.
    private UsuarioRepository usuarioRepository;

    // Usa o banco padrao do projeto.
    public GerenciadorUsuarios() {
        this("jarvis.db");
    }

    // Permite escolher outro arquivo de banco, util para teste ou configuracao futura.
    public GerenciadorUsuarios(String caminhoBanco) {
        this(new UsuarioRepository(caminhoBanco));
    }

    // Recebe um repository pronto e ja carrega os usuarios salvos.
    public GerenciadorUsuarios(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarios = usuarioRepository.listarTodos();
    }

    // Valida, evita email repetido, adiciona na lista e salva no banco.
    public boolean adicionarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        String nome = limparTexto(usuario.getNome());
        String email = limparTexto(usuario.getEmail());
        String senha = limparTexto(usuario.getSenha());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            return false;
        }

        if (emailJaExiste(email)) {
            return false;
        }

        usuarios.add(new Usuario(nome, email, senha));
        salvarUsuarios();
        return true;
    }

    // Confere email e senha e devolve o usuario quando o login estiver correto.
    public Usuario autenticar(String email, String senha) {
        String emailLimpo = limparTexto(email);
        String senhaLimpa = limparTexto(senha);

        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(emailLimpo) && usuario.getSenha().equals(senhaLimpa)) {
                return usuario;
            }
        }

        return null;
    }

    // Devolve a lista atual para telas, comandos e testes consultarem.
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    // Remove um Usuario especifico da lista e atualiza o banco se conseguiu remover.
    public boolean removerUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        boolean removido = usuarios.remove(usuario);

        if (removido) {
            salvarUsuarios();
        }

        return removido;
    }

    // Atualiza todos os dados principais de um usuario ja existente.
    public void atualizarUsuario(Usuario usuario, String novoNome, String novoEmail, String novaSenha) {
        if (usuario == null) {
            return;
        }

        usuario.setNome(limparTexto(novoNome));
        usuario.setEmail(limparTexto(novoEmail));
        usuario.setSenha(limparTexto(novaSenha));
        salvarUsuarios();
    }

    // Procura na lista em memoria pelo email informado.
    public Usuario buscarUsuarioPorEmail(String email) {
        String emailLimpo = limparTexto(email);

        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(emailLimpo)) {
                return usuario;
            }
        }

        return null;
    }

    // Metodo pequeno que melhora a leitura das validacoes de cadastro.
    public boolean emailJaExiste(String email) {
        return buscarUsuarioPorEmail(email) != null;
    }

    // Altera apenas a senha do usuario recebido.
    public void alterarSenha(Usuario usuario, String novaSenha) {
        if (usuario == null) {
            return;
        }

        usuario.setSenha(limparTexto(novaSenha));
        salvarUsuarios();
    }

    // Altera apenas o nome do usuario recebido.
    public void alterarNome(Usuario usuario, String novoNome) {
        if (usuario == null) {
            return;
        }

        usuario.setNome(limparTexto(novoNome));
        salvarUsuarios();
    }

    // Versao usada por comandos de texto: acha pelo nome antigo e troca pelo novo.
    public boolean alterarNome(String nomeAntigo, String novoNome) {
        String nomeAntigoLimpo = limparTexto(nomeAntigo);
        String novoNomeLimpo = limparTexto(novoNome);

        if (novoNomeLimpo.isEmpty()) {
            return false;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equals(nomeAntigoLimpo)) {
                usuario.setNome(novoNomeLimpo);
                salvarUsuarios();
                return true;
            }
        }

        return false;
    }

    // Troca o email garantindo que o novo email nao pertence a outro usuario.
    public boolean alterarEmail(String emailAntigo, String emailNovo) {
        String emailAntigoLimpo = limparTexto(emailAntigo);
        String emailNovoLimpo = limparTexto(emailNovo);

        if (emailNovoLimpo.isEmpty()) {
            return false;
        }

        Usuario usuario = buscarUsuarioPorEmail(emailAntigoLimpo);

        if (usuario == null) {
            return false;
        }

        if (!emailAntigoLimpo.equals(emailNovoLimpo) && emailJaExiste(emailNovoLimpo)) {
            return false;
        }

        usuario.setEmail(emailNovoLimpo);
        salvarUsuarios();
        return true;
    }

    // Remove pelo nome porque esse era o jeito usado nos comandos antigos do terminal.
    public boolean excluirUsuarioPorNome(String nome) {
        String nomeLimpo = limparTexto(nome);

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);

            if (usuario.getNome().equals(nomeLimpo)) {
                usuarios.remove(i);
                salvarUsuarios();
                return true;
            }
        }

        return false;
    }

    // Toda mudanca na lista passa por aqui para manter memoria e banco sincronizados.
    private void salvarUsuarios() {
        usuarioRepository.salvarTodos(usuarios);
    }

    // Evita null e remove espacos extras das pontas do texto.
    private String limparTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.trim();
    }
}
