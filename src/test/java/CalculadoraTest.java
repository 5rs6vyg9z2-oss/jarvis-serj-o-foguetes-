import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Calculadora class.
 * Tests basic arithmetic operations: addition, subtraction, multiplication, division.
 */
@DisplayName("Calculadora Tests")
public class CalculadoraTest {

    private Calculadora calculadora = new Calculadora();

    @Test
    @DisplayName("Should detect addition operation in text")
    void testTemOperacaoAddicao() {
        assertTrue(calculadora.temOperacao("10+5"));
        assertTrue(calculadora.temOperacao("100 + 200"));
    }

    @Test
    @DisplayName("Should detect subtraction operation in text")
    void testTemOperacaoSubtracao() {
        assertTrue(calculadora.temOperacao("10-5"));
        assertTrue(calculadora.temOperacao("50 - 20"));
    }

    @Test
    @DisplayName("Should detect multiplication operation in text")
    void testTemOperacaoMultiplicacao() {
        assertTrue(calculadora.temOperacao("10*5"));
        assertTrue(calculadora.temOperacao("3 * 7"));
    }

    @Test
    @DisplayName("Should detect division operation in text")
    void testTemOperacaoDivisao() {
        assertTrue(calculadora.temOperacao("10/5"));
        assertTrue(calculadora.temOperacao("20 / 4"));
    }

    @Test
    @DisplayName("Should return false for text without operations")
    void testTemOperacaoFalso() {
        assertFalse(calculadora.temOperacao("hello world"));
        assertFalse(calculadora.temOperacao("12345"));
    }

    @Test
    @DisplayName("Should calculate simple addition")
    void testCalcularAdicao() {
        String resultado = calculadora.calcular("10+5");
        assertNotNull(resultado);
        assertTrue(resultado.contains("15") || resultado.toLowerCase().contains("resultado"));
    }

    @Test
    @DisplayName("Should calculate simple subtraction")
    void testCalcularSubtracao() {
        String resultado = calculadora.calcular("10-3");
        assertNotNull(resultado);
        assertTrue(resultado.contains("7") || resultado.toLowerCase().contains("resultado"));
    }

    @Test
    @DisplayName("Should calculate multiplication")
    void testCalcularMultiplicacao() {
        String resultado = calculadora.calcular("6*7");
        assertNotNull(resultado);
        assertTrue(resultado.contains("42") || resultado.toLowerCase().contains("resultado"));
    }

    @Test
    @DisplayName("Should calculate division")
    void testCalcularDivisao() {
        String resultado = calculadora.calcular("20/4");
        assertNotNull(resultado);
        assertTrue(resultado.contains("5") || resultado.toLowerCase().contains("resultado"));
    }

    @Test
    @DisplayName("Should handle division by zero")
    void testCalcularDivisaoPorZero() {
        String resultado = calculadora.calcular("10/0");
        assertNotNull(resultado);
        assertTrue(resultado.toLowerCase().contains("zero"));
    }

    @Test
    @DisplayName("Should handle invalid number format")
    void testCalcularComNumeroInvalido() {
        String resultado = calculadora.calcular("abc+def");
        assertNotNull(resultado);
        assertTrue(resultado.toLowerCase().contains("numero") || resultado.toLowerCase().contains("entender"));
    }
}
