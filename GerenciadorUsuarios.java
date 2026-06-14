import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GerenciadorUsuarios {
    private List<Usuario> usuarios;
    private String caminhoArquivo;

    public GerenciadorUsuarios() {
        this("usuarios.txt");
    }

    public GerenciadorUsuarios(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        this.usuarios = new ArrayList<>();
        carregarUsuarios();
    }

    private void carregarUsuarios() {
        File arquivoUsuarios = new File(caminhoArquivo);

        if (!arquivoUsuarios.exists()) {
            return;
        }

        try {
            Scanner leitor = new Scanner(arquivoUsuarios);

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                Usuario usuario = Usuario.criarPelaLinha(linha);

                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }

            leitor.close();
        } catch (IOException e) {
            System.out.println("erro ao carregar usuarios.");
        }
    }

    private void salvarUsuarios() {
        try {
            FileWriter escritor = new FileWriter(caminhoArquivo);

            for (Usuario usuario : usuarios) {
                escritor.write(usuario.transformarEmLinha() + "\n");
            }

            escritor.close();
        } catch (IOException e) {
            System.out.println("erro ao salvar usuarios.");
        }
    }

    public boolean adicionarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        if (usuario.getNome().isEmpty() || usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()) {
            return false;
        }

        if (emailJaExiste(usuario.getEmail())) {
            return false;
        }

        usuarios.add(usuario);
        salvarUsuarios();
        return true;
    }

    public Usuario autenticar(String email, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(email) && usuario.getSenha().equals(senha)) {
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
        usuario.setNome(novoNome);
        usuario.setEmail(novoEmail);
        usuario.setSenha(novaSenha);
        salvarUsuarios();
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                return usuario;
            }
        }

        return null;
    }

    public boolean emailJaExiste(String email) {
        return buscarUsuarioPorEmail(email) != null;
    }

    public void alterarSenha(Usuario usuario, String novaSenha) {
        usuario.setSenha(novaSenha);
        salvarUsuarios();
    }

    public void alterarNome(Usuario usuario, String novoNome) {
        usuario.setNome(novoNome);
        salvarUsuarios();
    }

    public boolean alterarNome(String nomeAntigo, String novoNome) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equals(nomeAntigo)) {
                usuario.setNome(novoNome);
                salvarUsuarios();
                return true;
            }
        }

        return false;
    }

    public boolean alterarEmail(String emailAntigo, String emailNovo) {
        Usuario usuario = buscarUsuarioPorEmail(emailAntigo);

        if (usuario == null) {
            return false;
        }

        if (!emailAntigo.equals(emailNovo) && emailJaExiste(emailNovo)) {
            return false;
        }

        usuario.setEmail(emailNovo);
        salvarUsuarios();
        return true;
    }

    public boolean excluirUsuarioPorNome(String nome) {
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);

            if (usuario.getNome().equals(nome)) {
                usuarios.remove(i);
                salvarUsuarios();
                return true;
            }
        }

        return false;
    }
}
