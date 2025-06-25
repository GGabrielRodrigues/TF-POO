package bagagem.model;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Classe de testes para a classe abstrata Recibo.
 * REVISADO: Estrutura simplificada com um único @BeforeEach.
 */
@DisplayName("Testes da Classe Abstrata Recibo")
class ReciboTest {

    private Processo processoValido;
    private Recibo reciboDeTeste;
    private Date dataAssinatura;

    /**
     * Prepara um ambiente limpo e objetos consistentes ANTES de cada teste.
     */
    @BeforeEach
    void setUp() {
        // Zera os contadores estáticos para garantir testes independentes.
        Processo.setNextIdProcesso(0);
        Recibo.setNextIdRecibo(0);
        
        dataAssinatura = new Date();
        
        // Cria um processo válido, pois todo recibo precisa de um.
        processoValido = new DanificacaoBagagem("GYN", "12345", new Date(), "DA-GYN-001");
        
        // REVISÃO: Cria a instância do recibo de teste aqui para ser usada por todos os testes.
        reciboDeTeste = new ReciboConsertoBagagem(
            processoValido.getBase(), 
            processoValido.getNumeroProcesso(), 
            dataAssinatura, 
            processoValido, 
            "Oficina Central"
        );
    }

    @Test
    @DisplayName("Deve gerar IDs sequenciais para novos recibos")
    void testGeracaoAutomaticaDeId() {
        // O primeiro recibo (reciboDeTeste) já foi criado no setUp com ID 1.
        // Criamos um segundo para verificar a sequência.
        Recibo r2 = new ReciboConsertoBagagem("BSB", "54321", dataAssinatura, processoValido, "Local 2");
        
        assertEquals(1, reciboDeTeste.getId(), "O ID do primeiro recibo (do setUp) deveria ser 1.");
        assertEquals(2, r2.getId(), "O ID do segundo recibo deveria ser 2.");
    }
    
    @Nested
    @DisplayName("Testes de Construtor e Estado")
    class ConstrutorEstadoTest {
        
        @Test
        @DisplayName("Deve inicializar atributos corretamente via construtor")
        void testConstrutorEGetters() {
            // Assert
            // Usa o objeto 'reciboDeTeste' criado no setUp principal.
            assertNotNull(reciboDeTeste);
            assertEquals(1, reciboDeTeste.getId());
            assertEquals("GYN", reciboDeTeste.getBase());
            assertEquals("12345", reciboDeTeste.getNumeroProcesso());
            assertEquals(dataAssinatura, reciboDeTeste.getDataAssinatura());
            assertSame(processoValido, reciboDeTeste.getProcessoAssociado());
        }
        
        @Test
        @DisplayName("Deve lançar exceção ao criar recibo com processo nulo")
        void testConstrutorLancaExcecaoParaProcessoNulo() {
            // Act & Assert
            // Este teste valida a regra na classe Recibo.
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new ReciboConsertoBagagem("CWB", "99999", dataAssinatura, null, "Local inválido");
            });
            assertEquals("Recibo deve estar vinculado a um Processo.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve permitir a alteração de dados via setters")
        void testSetters() {
            // Arrange
            Date novaData = new Date(dataAssinatura.getTime() + 1000);
            Processo novoProcesso = new DanificacaoBagagem("MIA", "20202", new Date(), "DA-MIA-002");
            
            // Act
            reciboDeTeste.setDataAssinatura(novaData);
            reciboDeTeste.setProcessoAssociado(novoProcesso);
            
            // Assert
            assertEquals(novaData, reciboDeTeste.getDataAssinatura());
            assertSame(novoProcesso, reciboDeTeste.getProcessoAssociado());
        }
    }
    
    @Nested
    @DisplayName("Testes de Lógica de Negócio e Representação")
    class LogicaDeNegocioTest {

        @Test
        @DisplayName("Deve editar informações básicas via método editarDadosRecibo")
        void testEditarDadosRecibo() {
            // Arrange
            Date novaData = new Date(dataAssinatura.getTime() + 5000);
            Map<String, Object> novosDados = new HashMap<>();
            novosDados.put("dataAssinatura", novaData);

            // Act
            reciboDeTeste.editarDadosRecibo(novosDados);

            // Assert
            assertEquals(novaData, reciboDeTeste.getDataAssinatura());
        }
        
        @Test
        @DisplayName("Deve gerar uma representação em String correta")
        void testToString() {
            // Arrange
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(dataAssinatura);
            
            // Act
            String resultadoToString = reciboDeTeste.toString();
            
            // Assert
            // REVISÃO: A verificação agora usa os dados corretos do objeto 'reciboDeTeste'
            // que foi criado com o processo GYN-12345.
            assertTrue(resultadoToString.contains("Tipo Recibo: ReciboConsertoBagagem"));
            assertTrue(resultadoToString.contains("Processo Associado: GYN-12345"));
            assertTrue(resultadoToString.contains("Data Assinatura: " + dataFormatada));
        }
    }
}