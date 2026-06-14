import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UsuarioRepository {
    private String caminhoArquivo;

    public UsuarioRepository() {
        this("usuarios.txt");
    }

    public UsuarioRepository(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        File arquivoUsuarios = new File(caminhoArquivo);

        if (!arquivoUsuarios.exists()) {
            return usuarios;
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

        return usuarios;
    }

    public void salvarTodos(List<Usuario> usuarios) {
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

    public Usuario buscarPorEmail(String email) {
        for (Usuario usuario : listarTodos()) {
            if (usuario.getEmail().equals(email)) {
                return usuario;
            }
        }

        return null;
    }
}
