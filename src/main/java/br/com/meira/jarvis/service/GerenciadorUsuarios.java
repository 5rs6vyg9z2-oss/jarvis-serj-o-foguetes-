package br.com.meira.jarvis.service;

import br.com.meira.jarvis.model.Usuario;
import br.com.meira.jarvis.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


// Centraliza as regras de usuario entre a tela/terminal e o banco de dados.
@Service
public class GerenciadorUsuarios {
    // Lista em memoria usada pelo Jarvis enquanto o programa esta aberto.
    private final List<Usuario> usuarios;

    // Repository faz a parte de persistencia: gravar e buscar no PostgreSQL.
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder codificadorDeSenha = new BCryptPasswordEncoder();

    // Usa o banco padrao do projeto.
    public GerenciadorUsuarios() {
        this(new UsuarioRepository());
    }

    // Permite escolher outra URL JDBC, util para teste ou configuracao futura.
    public GerenciadorUsuarios(String urlBanco) {
        this(new UsuarioRepository(urlBanco));
    }

    // Recebe um repository pronto e ja carrega os usuarios salvos.
    public GerenciadorUsuarios(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarios = new ArrayList<>(usuarioRepository.listarTodos());
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

        Usuario novoUsuario = new Usuario(nome, email, codificarSenha(senha));
        usuarioRepository.inserirUsuario(novoUsuario);
        usuarios.add(novoUsuario);
        return true;
    }

    // Mantem compatibilidade com as telas antigas que fazem login usando email e senha.
    public Usuario autenticar(String email, String senha) {
        String emailLimpo = limparTexto(email);
        String senhaLimpa = limparTexto(senha);

        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(emailLimpo) && senhaConfere(senhaLimpa, usuario)) {
                migrarSenhaLegada(usuario, senhaLimpa);
                return usuario;
            }
        }

        return null;
    }

    // Confere email e senha e devolve o usuario quando o login estiver correto.
    public Usuario autenticar(String nome, String email, String senha) {
        String nomeLimpo = limparTexto(nome);
        String emailLimpo = limparTexto(email);
        String senhaLimpa = limparTexto(senha);

        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equals(nomeLimpo) && usuario.getEmail().equals(emailLimpo)
                    && senhaConfere(senhaLimpa, usuario)) {
                migrarSenhaLegada(usuario, senhaLimpa);
                return usuario;
            }
        }

        return null;
    }

    // Devolve a lista atual para telas, comandos e testes consultarem.
    public List<Usuario> getUsuarios() {
        return List.copyOf(usuarios);
    }

    // Remove um Usuario especifico da lista e atualiza o banco se conseguiu remover.
    public boolean removerUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        if (!usuarioRepository.excluirUsuarioPorEmail(usuario.getEmail())) {
            return false;
        }

        return usuarios.remove(usuario);
    }

    // Atualiza todos os dados principais de um usuario ja existente.
    public void atualizarUsuario(Usuario usuario, String novoNome, String novoEmail, String novaSenha) {
        if (usuario == null) {
            return;
        }

        String emailAtual = usuario.getEmail();
        Usuario atualizado = new Usuario(
                limparTexto(novoNome),
                limparTexto(novoEmail),
                codificarSenha(limparTexto(novaSenha)));

        if (atualizado.getNome().isEmpty() || atualizado.getEmail().isEmpty() || novaSenha == null || novaSenha.isBlank()) {
            return;
        }

        if (usuarioRepository.atualizarUsuario(emailAtual, atualizado)) {
            usuario.setNome(atualizado.getNome());
            usuario.setEmail(atualizado.getEmail());
            usuario.setSenha(atualizado.getSenha());
        }
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
    public boolean alterarSenha(Usuario usuario, String novaSenha) {
        if (usuario == null) {
            return false;
        }

        String novaSenhaLimpa = limparTexto(novaSenha);
        if (novaSenhaLimpa.isEmpty()) {
            return false;
        }

        Usuario atualizado = new Usuario(usuario.getNome(), usuario.getEmail(), codificarSenha(novaSenhaLimpa));
        if (!usuarioRepository.atualizarUsuario(usuario.getEmail(), atualizado)) {
            return false;
        }

        usuario.setSenha(atualizado.getSenha());
        return true;
    }

    // Confere a senha atual antes de salvar a nova senha da conta informada.
    public boolean alterarSenha(String email, String senhaAtual, String novaSenha) {
        String novaSenhaLimpa = limparTexto(novaSenha);
        Usuario usuario = autenticar(email, senhaAtual);

        if (usuario == null || novaSenhaLimpa.isEmpty()) {
            return false;
        }

        return alterarSenha(usuario, novaSenhaLimpa);
    }

    // Altera apenas o nome do usuario recebido.
    public boolean alterarNome(Usuario usuario, String novoNome) {
        if (usuario == null) {
            return false;
        }

        String novoNomeLimpo = limparTexto(novoNome);
        if (novoNomeLimpo.isEmpty()) {
            return false;
        }

        Usuario atualizado = new Usuario(novoNomeLimpo, usuario.getEmail(), usuario.getSenha());
        if (!usuarioRepository.atualizarUsuario(usuario.getEmail(), atualizado)) {
            return false;
        }

        usuario.setNome(novoNomeLimpo);
        return true;
    }

    // Versao usada por comandos de texto: acha pelo nome antigo e troca pelo novo.
    public boolean alterarNome(String nomeAntigo, String novoNome) {
        String nomeAntigoLimpo = limparTexto(nomeAntigo);
        String novoNomeLimpo = limparTexto(novoNome);

        if (novoNomeLimpo.isEmpty()) {
            return false;
        }

        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equalsIgnoreCase(nomeAntigoLimpo)) {
                return alterarNome(usuario, novoNomeLimpo);
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

        Usuario atualizado = new Usuario(usuario.getNome(), emailNovoLimpo, usuario.getSenha());
        if (!usuarioRepository.atualizarUsuario(emailAntigoLimpo, atualizado)) {
            return false;
        }

        usuario.setEmail(emailNovoLimpo);
        return true;
    }

    // Remove pelo nome porque esse era o jeito usado nos comandos antigos do terminal.
    public boolean excluirUsuarioPorNome(String nome) {
        String nomeLimpo = limparTexto(nome);

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);

            if (usuario.getNome().equalsIgnoreCase(nomeLimpo)) {
                if (!usuarioRepository.excluirUsuarioPorEmail(usuario.getEmail())) {
                    return false;
                }

                usuarios.remove(i);
                return true;
            }
        }

        return false;
    }

    private boolean senhaConfere(String senhaDigitada, Usuario usuario) {
        if (senhaUsaHash(usuario.getSenha())) {
            return codificadorDeSenha.matches(senhaDigitada, usuario.getSenha());
        }

        return usuario.getSenha().equals(senhaDigitada);
    }

    private void migrarSenhaLegada(Usuario usuario, String senhaDigitada) {
        if (senhaUsaHash(usuario.getSenha())) {
            return;
        }

        Usuario atualizado = new Usuario(usuario.getNome(), usuario.getEmail(), codificarSenha(senhaDigitada));
        if (usuarioRepository.atualizarUsuario(usuario.getEmail(), atualizado)) {
            usuario.setSenha(atualizado.getSenha());
        }
    }

    private boolean senhaUsaHash(String senha) {
        return senha != null && senha.startsWith("$2");
    }

    private String codificarSenha(String senha) {
        return codificadorDeSenha.encode(senha);
    }

    // Evita null e remove espacos extras das pontas do texto.
    private String limparTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.trim();
    }

}
