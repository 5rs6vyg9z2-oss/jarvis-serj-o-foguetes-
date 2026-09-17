package br.com.meira.jarvis.repository;

import br.com.meira.jarvis.model.Usuario;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Repository: camada responsavel por salvar e buscar usuarios no banco PostgreSQL.
public class UsuarioRepository {
    public UsuarioRepository() {
        this(
            obterConfiguracao("DB_URL"),
            obterConfiguracao("DB_USER"),
            obterConfiguracao("DB_PASSWORD")
        );
    }
    // URL de conexao com o banco PostgreSQL.
    private String urlBanco;
    private String usuarioBanco;
    private String senhaBanco;

    // Construtor padrao usado pelo projeto real.
    public UsuarioRepository(String urlBanco, String usuarioBanco, String senhaBanco) {
        this.urlBanco = urlBanco;
        this.usuarioBanco = usuarioBanco;
        this.senhaBanco = senhaBanco;
        criarTabela();
    }

    // Construtor com URL JDBC facilita testes ou troca de banco no futuro.
    public UsuarioRepository(String urlBanco) {
        this(
            urlBanco,
            obterConfiguracao("DB_USER"),
            obterConfiguracao("DB_PASSWORD")
        );
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
            throw erroBanco("listar usuarios", e);
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
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw erroBanco("salvar usuarios", e);
            }
        } catch (SQLException e) {
            throw erroBanco("conectar ao banco", e);
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
            throw erroBanco("buscar usuario", e);
        }

        return null;
    }

    // Abre uma conexao JDBC com o PostgreSQL configurado.
    private Connection conectar() throws SQLException {
        if (textoVazio(urlBanco) || textoVazio(usuarioBanco) || textoVazio(senhaBanco)) {
            throw new SQLException("configuracao do banco incompleta. Confira DB_URL, DB_USER e DB_PASSWORD.");
        }

        return DriverManager.getConnection(urlBanco, usuarioBanco, senhaBanco);
    }

    // Garante que a tabela exista antes do gerenciador tentar usar o banco.
    private void criarTabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id SERIAL PRIMARY KEY,
                    nome TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    senha TEXT NOT NULL
                )
                """;

        try (Connection conexao = conectar();
                Statement comando = conexao.createStatement()) {
            comando.execute(sql);
        } catch (SQLException e) {
            throw erroBanco("criar a tabela de usuarios", e);
        }
    }

    // Busca primeiro nas variaveis do sistema; se nao achar, le o arquivo .env.
    private static String obterConfiguracao(String nome) {
        String valor = System.getenv(nome);

        if (!textoVazio(valor)) {
            return valor;
        }

        return lerConfiguracaoDoEnv(nome);
    }

    private static String lerConfiguracaoDoEnv(String nome) {
        for (Path diretorio : diretoriosParaBuscarEnv()) {
            Path arquivoEnv = diretorio.resolve(".env");

            if (Files.exists(arquivoEnv)) {
                try {
                    for (String linha : Files.readAllLines(arquivoEnv)) {
                        String linhaLimpa = linha.trim();

                        if (linhaLimpa.isEmpty() || linhaLimpa.startsWith("#")) {
                            continue;
                        }

                        String[] partes = linhaLimpa.split("=", 2);

                        if (partes.length == 2 && partes[0].trim().equals(nome)) {
                            return partes[1].trim();
                        }
                    }
                } catch (IOException e) {
                    return null;
                }
            }

        }

        return null;
    }

    // Procura pelo .env tanto pela pasta de execucao quanto pela pasta do projeto compilado.
    private static Set<Path> diretoriosParaBuscarEnv() {
        Set<Path> diretorios = new LinkedHashSet<>();
        adicionarDiretorioEAncestrais(
            diretorios,
            Path.of(System.getProperty("user.dir")).toAbsolutePath()
        );

        try {
            Path origem = Path.of(
                UsuarioRepository.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );

            if (Files.isRegularFile(origem)) {
                origem = origem.getParent();
            }

            adicionarDiretorioEAncestrais(diretorios, origem);
        } catch (URISyntaxException | NullPointerException e) {
            // A busca pela pasta de execucao ainda funciona quando a origem nao esta disponivel.
        }

        return diretorios;
    }

    private static void adicionarDiretorioEAncestrais(Set<Path> diretorios, Path diretorio) {
        while (diretorio != null) {
            diretorios.add(diretorio);
            diretorio = diretorio.getParent();
        }
    }

    private static IllegalStateException erroBanco(String operacao, SQLException causa) {
        return new IllegalStateException(
            "Nao foi possivel " + operacao
                + ". Confira se o Docker/PostgreSQL esta ativo e se DB_URL, DB_USER e DB_PASSWORD estao corretos.",
            causa
        );
    }

    private static boolean textoVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
