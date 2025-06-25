package bagagem.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Classe de testes para a classe abstrata Processo.
 * Utiliza uma instância de uma subclasse concreta (DanificacaoBagagem)
 * para testar os comportamentos definidos em Processo.
 * A estrutura usa @Nested para organizar os testes por funcionalidade.
 */
@DisplayName("Testes da Classe Abstrata Processo")
class ProcessoTest {

    // REVISÃO: O contador de ID é estático, então seu teste fica no nível principal.
    @Test
    @DisplayName("Deve gerar IDs sequenciais para novos processos")
    void testGeracaoAutomaticaDeId() {
        // Zera o contador para este teste específico
        Processo.setNextIdProcesso(0);
        
        // Arrange & Act
        Processo p1 = new DanificacaoBagagem("GYN", "11111", new Date(), "ETQ-001");
        Processo p2 = new ExtravioBagagem("BSB", "22222", new Date(), "ETQ-002");
        
        // Assert
        assertEquals(1, p1.getId(), "O ID do primeiro processo deveria ser 1.");
        assertEquals(2, p2.getId(), "O ID do segundo processo deveria ser 2.");
    }

    // REVISÃO: Usando @Nested para agrupar testes relacionados ao estado do objeto.
    @Nested
    @DisplayName("Testes de Estado, Construtor e Setters")
    class EstadoEConstrutorTest {
        
        private Processo processo; 
        private Date dataDeAbertura;

        @BeforeEach
        void setUp() {
            // Este setup roda apenas para os testes dentro desta classe aninhada.
            Processo.setNextIdProcesso(0); // Garante que o ID seja previsível (1)
            dataDeAbertura = new Date();
            processo = new DanificacaoBagagem("GYN", "12345", dataDeAbertura, "DA-12345678");
        }

        @Test
        @DisplayName("Deve inicializar atributos corretamente via construtor")
        void testConstrutorEGetters() {
            assertNotNull(processo);
            assertEquals(1, processo.getId());
            assertEquals("GYN", processo.getBase()); //
            assertEquals("12345", processo.getNumeroProcesso()); //
            assertEquals(dataDeAbertura, processo.getDataAbertura()); //
            
            assertNull(processo.getCaminhoDocumento());
            assertNull(processo.getTipoArquivoDocumento());
            assertEquals(0, processo.getTamanhoArquivoDocumento());
        }
        
        @Test
        @DisplayName("Deve permitir a alteração de atributos via setters")
        void testSetters() {
            // Act
            Date novaData = new Date(dataDeAbertura.getTime() + 1000);
            processo.setDataAbertura(novaData); //
            processo.setCaminhoDocumento("/docs/novo.pdf"); //
            processo.setTipoArquivoDocumento("PDF"); //
            processo.setTamanhoArquivoDocumento(2048); //

            // Assert
            assertEquals(novaData, processo.getDataAbertura());
            assertEquals("/docs/novo.pdf", processo.getCaminhoDocumento());
            assertEquals("PDF", processo.getTipoArquivoDocumento());
            assertEquals(2048, processo.getTamanhoArquivoDocumento());
        }
    }
    
    // REVISÃO: Usando @Nested para agrupar testes de lógica de negócio.
    @Nested
    @DisplayName("Testes de Lógica de Negócio")
    class LogicaDeNegocioTest {

        private Processo processo;

        @BeforeEach
        void setUp() {
            processo = new DanificacaoBagagem("CWB", "10101", new Date(), "ETQ-CWB-01");
        }
        
        @Test
        @DisplayName("Deve editar informações comuns usando o método editarInformacoes")
        void testEditarInformacoesComuns() throws InterruptedException {
            // Arrange
            TimeUnit.MILLISECONDS.sleep(10);
            Date novaData = new Date();
            long novoTamanho = 500L;
            
            Map<String, Object> novosDados = new HashMap<>();
            novosDados.put("dataAbertura", novaData);
            novosDados.put("caminhoDocumento", "caminho/editado.jpg");
            novosDados.put("tipoArquivoDocumento", "JPEG");
            novosDados.put("tamanhoArquivoDocumento", novoTamanho);
            
            // Act
            processo.editarInformacoes(novosDados); //
            
            // Assert
            assertEquals(novaData, processo.getDataAbertura());
            assertEquals("caminho/editado.jpg", processo.getCaminhoDocumento());
            assertEquals("JPEG", processo.getTipoArquivoDocumento());
            assertEquals(novoTamanho, processo.getTamanhoArquivoDocumento());
        }

        @Test
        @DisplayName("Deve simular a captura de imagem e atualizar os atributos")
        void testCapturarImagem() {
            // Act
            String caminho = processo.capturarImagem(); //
            
            // Assert
            assertNotNull(caminho);
            assertTrue(caminho.contains("imagem_capturada_"));
            assertEquals(caminho, processo.getCaminhoDocumento());
            assertEquals("JPG", processo.getTipoArquivoDocumento());
            assertEquals(1024 * 500, processo.getTamanhoArquivoDocumento());
        }
        
        @Test
        @DisplayName("Deve renomear o documento corretamente em vários cenários")
        void testRenomearDocumento() {
            // Cenário 1: Renomear arquivo com extensão.
            processo.setCaminhoDocumento("pasta/original.png");
            processo.renomearDocumento("novo-nome-1"); //
            assertEquals("novo-nome-1.png", processo.getCaminhoDocumento());
            
            // Cenário 2: Renomear usando o tipo do arquivo, pois não há extensão no caminho.
            processo.setCaminhoDocumento("pasta/original_sem_extensao");
            processo.setTipoArquivoDocumento("PDF");
            processo.renomearDocumento("novo-nome-2"); //
            assertEquals("novo-nome-2.pdf", processo.getCaminhoDocumento());
            
            // Cenário 3: Tenta renomear quando não há documento. Não deve fazer nada.
            Processo processoSemDoc = new ExtravioBagagem("SDU", "99999", new Date(), "ETQ-SDU");
            processoSemDoc.renomearDocumento("qualquer-coisa"); //
            assertNull(processoSemDoc.getCaminhoDocumento());
        }
    }
}