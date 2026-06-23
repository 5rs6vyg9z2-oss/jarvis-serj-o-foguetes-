import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Repository: camada responsavel por salvar e buscar usuarios no banco SQLite.
public class UsuarioRepository {
    // Caminho do arquivo .db que o SQLite usa para guardar os dados.
    private String caminhoBanco;

    // Construtor padrao usado pelo projeto real.
    public UsuarioRepository() {
        this("jarvis.db");
    }

    // Construtor com caminho facilita testes ou troca de banco no futuro.
    public UsuarioRepository(String caminhoBanco) {
        this.caminhoBanco = caminhoBanco;
        criarTabela();
    }

    // Le todos os usuarios do banco e coloca em uma lista na memoria.
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT nome, email, senha FROM usuarios";

        try (Connection conexao = conectar();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                String nome = resultado.getString("nome");
                String email = resultado.getString("email");
                String senha = resultado.getString("senha");

                usuarios.add(new Usuario(nome, email, senha));
            }
        } catch (SQLException e) {
            System.out.println("erro ao listar usuarios no banco.");
        }

        return usuarios;
    }

    // Salva a lista inteira de usuarios no banco em uma unica transacao.
    public void salvarTodos(List<Usuario> usuarios) {
        String apagarSql = "DELETE FROM usuarios";
        String inserirSql = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection conexao = conectar()) {
            // AutoCommit falso permite confirmar tudo junto ou desfazer se der erro.
            conexao.setAutoCommit(false);

            try (Statement apagar = conexao.createStatement();
                    PreparedStatement inserir = conexao.prepareStatement(inserirSql)) {

                apagar.executeUpdate(apagarSql);

                for (Usuario usuario : usuarios) {
                    inserir.setString(1, usuario.getNome());
                    inserir.setString(2, usuario.getEmail());
                    inserir.setString(3, usuario.getSenha());
                    inserir.addBatch();
                }

                inserir.executeBatch();
                conexao.commit();
            } catch (SQLException e) {
                conexao.rollback();
                System.out.println("erro ao salvar usuarios no banco.");
            }
        } catch (SQLException e) {
            System.out.println("erro ao conectar no banco.");
        }
    }

    // Busca um usuario pelo email usando PreparedStatement para evitar SQL montado na mao.
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT nome, email, senha FROM usuarios WHERE email = ?";

        try (Connection conexao = conectar();
                PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, email);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    String nome = resultado.getString("nome");
                    String emailEncontrado = resultado.getString("email");
                    String senha = resultado.getString("senha");

                    return new Usuario(nome, emailEncontrado, senha);
                }
            }
        } catch (SQLException e) {
            System.out.println("erro ao buscar usuario no banco.");
        }

        return null;
    }

    // Abre uma conexao JDBC com o arquivo SQLite configurado.
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + caminhoBanco);
    }

    // Garante que a tabela exista antes do gerenciador tentar usar o banco.
    private void criarTabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    senha TEXT NOT NULL
                )
                """;

        try (Connection conexao = conectar();
                Statement comando = conexao.createStatement()) {
            comando.execute(sql);
        } catch (SQLException e) {
            System.out.println("erro ao criar tabela de usuarios.");
        }
    }
}
