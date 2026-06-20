import java.util.List;

public class GerenciadorUsuarios {
    private List<Usuario> usuarios;
    private UsuarioRepository usuarioRepository;

    public GerenciadorUsuarios() {
        this("jarvis.db");
    }

    public GerenciadorUsuarios(String caminhoArquivo) {
        this.usuarioRepository = new UsuarioRepository(caminhoArquivo);
        this.usuarios = usuarioRepository.listarTodos();
    }

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

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

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

    public void atualizarUsuario(Usuario usuario, String novoNome, String novoEmail, String novaSenha) {
        if (usuario == null) {
            return;
        }

        usuario.setNome(limparTexto(novoNome));
        usuario.setEmail(limparTexto(novoEmail));
        usuario.setSenha(limparTexto(novaSenha));
        salvarUsuarios();
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        String emailLimpo = limparTexto(email);

        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(emailLimpo)) {
                return usuario;
            }
        }

        return null;
    }

    public boolean emailJaExiste(String email) {
        return buscarUsuarioPorEmail(email) != null;
    }

    public void alterarSenha(Usuario usuario, String novaSenha) {
        if (usuario == null) {
            return;
        }

        usuario.setSenha(limparTexto(novaSenha));
        salvarUsuarios();
    }

    public void alterarNome(Usuario usuario, String novoNome) {
        if (usuario == null) {
            return;
        }

        usuario.setNome(limparTexto(novoNome));
        salvarUsuarios();
    }

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

    private void salvarUsuarios() {
        usuarioRepository.salvarTodos(usuarios);
    }

    private String limparTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.trim();
    }
}
