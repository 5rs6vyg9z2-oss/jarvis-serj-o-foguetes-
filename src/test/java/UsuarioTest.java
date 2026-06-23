import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Usuario Tests")
public class UsuarioTest {

    // Constantes evitam repetir os mesmos textos em todos os testes.
    private static final String NOME = "Joao Silva";
    private static final String EMAIL = "joao@example.com";
    private static final String SENHA = "senha123";

    // Testa se o construtor realmente guarda os valores recebidos.
    @Test
    @DisplayName("Should create usuario with correct values")
    void testCriacao() {
        Usuario usuario = new Usuario(NOME, EMAIL, SENHA);

        assertEquals(NOME, usuario.getNome());
        assertEquals(EMAIL, usuario.getEmail());
        assertEquals(SENHA, usuario.getSenha());
    }

    @Test
    @DisplayName("Should update usuario name")
    void testSetNome() {
        Usuario usuario = new Usuario(NOME, EMAIL, SENHA);
        String novoNome = "Maria Santos";

        usuario.setNome(novoNome);
        assertEquals(novoNome, usuario.getNome());
    }

    @Test
    @DisplayName("Should update usuario email")
    void testSetEmail() {
        Usuario usuario = new Usuario(NOME, EMAIL, SENHA);
        String novoEmail = "maria@example.com";

        usuario.setEmail(novoEmail);
        assertEquals(novoEmail, usuario.getEmail());
    }

    @Test
    @DisplayName("Should update usuario password")
    void testSetSenha() {
        Usuario usuario = new Usuario(NOME, EMAIL, SENHA);
        String novaSenha = "novasenha456";

        usuario.setSenha(novaSenha);
        assertEquals(novaSenha, usuario.getSenha());
    }

    @Test
    @DisplayName("Should serialize usuario to line format")
    void testTransformarEmLinha() {
        Usuario usuario = new Usuario(NOME, EMAIL, SENHA);

        // Serializar aqui significa transformar o objeto em texto.
        String linha = usuario.transformarEmLinha();

        String esperado = NOME + ";" + EMAIL + ";" + SENHA;
        assertEquals(esperado, linha);
    }

    @Test
    @DisplayName("Should deserialize usuario from line format")
    void testCriarPelaLinha() {
        String linha = NOME + ";" + EMAIL + ";" + SENHA;

        // Desserializar faz o caminho inverso: texto vira objeto Usuario.
        Usuario usuario = Usuario.criarPelaLinha(linha);

        assertNotNull(usuario);
        assertEquals(NOME, usuario.getNome());
        assertEquals(EMAIL, usuario.getEmail());
        assertEquals(SENHA, usuario.getSenha());
    }

    @Test
    @DisplayName("Should handle line format with spaces")
    void testCriarPelaLinhaComEspacos() {
        String linha = "  Joao  ;  joao@example.com  ;  senha123  ";
        Usuario usuario = Usuario.criarPelaLinha(linha);

        assertNotNull(usuario);
        assertEquals("Joao", usuario.getNome());
        assertEquals("joao@example.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
    }

    @Test
    @DisplayName("Should return null for invalid line format")
    void testCriarPelaLinhaInvalido() {
        String linha = "nome;email";
        Usuario usuario = Usuario.criarPelaLinha(linha);

        assertNull(usuario);
    }

    @Test
    @DisplayName("Should roundtrip usuario serialization")
    void testRoundtrip() {
        Usuario original = new Usuario(NOME, EMAIL, SENHA);
        String linha = original.transformarEmLinha();
        Usuario recuperado = Usuario.criarPelaLinha(linha);

        assertNotNull(recuperado);
        assertEquals(original.getNome(), recuperado.getNome());
        assertEquals(original.getEmail(), recuperado.getEmail());
        assertEquals(original.getSenha(), recuperado.getSenha());
    }
}
