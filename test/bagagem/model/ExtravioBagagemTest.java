package bagagem.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe de testes para a entidade ExtravioBagagem.
 * Cobre a criação, manipulação de dados e comportamentos da classe.
 */
@DisplayName("Testes da Classe ExtravioBagagem")
class ExtravioBagagemTest {

    private Date dataDeAbertura;

    /**
     * Reseta o estado antes da execução de cada teste.
     * Garante que os testes sejam independentes.
     */
    @BeforeEach
    void setUp() {
        // Zera o contador de ID da classe Processo para garantir previsibilidade.
        Processo.setNextIdProcesso(0);
        dataDeAbertura = new Date();
    }

    /**
     * Testa se o construtor preenche corretamente todos os atributos,
     * incluindo os herdados de Processo e o específico de ExtravioBagagem.
     */
    @Test
    @DisplayName("Deve criar um processo de extravio com os dados corretos")
    void testConstrutorEGetters() {
        // 1. Preparação (Arrange)
        ExtravioBagagem processo = new ExtravioBagagem("MIA", "88776", dataDeAbertura, "EX-87654321");

        // 2. Ação (Act) e 3. Verificação (Assert)
        assertNotNull(processo, "O processo não deveria ser nulo.");
        assertEquals("MIA", processo.getBase(), "A base do processo deve ser MIA."); // Herdado de Processo
        assertEquals("88776", processo.getNumeroProcesso(), "O número do processo deve ser 88776."); // Herdado de Processo
        assertEquals(dataDeAbertura, processo.getDataAbertura(), "A data de abertura deve ser a mesma."); // Herdado de Processo
        assertEquals("EX-87654321", processo.getEtiquetaBagagemExtraviada(), "A etiqueta da bagagem extraviada deve ser EX-87654321."); // Específico de ExtravioBagagem
    }
    
    /**
     * Testa se a geração de IDs sequenciais funciona para múltiplos objetos ExtravioBagagem.
     */
    @Test
    @DisplayName("Deve gerar IDs sequenciais para novos processos de extravio")
    void testGeracaoAutomaticaDeId() {
        // 1. Preparação (Arrange) e 2. Ação (Act)
        ExtravioBagagem processo1 = new ExtravioBagagem("ATL", "33333", new Date(), "ETQ-003");
        ExtravioBagagem processo2 = new ExtravioBagagem("LAX", "44444", new Date(), "ETQ-004");
        
        // 3. Verificação (Assert)
        // O contador foi resetado para 0 no setUp, então os IDs devem ser 1 e 2.
        assertEquals(1, processo1.getId(), "O ID do primeiro processo de extravio deveria ser 1.");
        assertEquals(2, processo2.getId(), "O ID do segundo processo de extravio deveria ser 2.");
    }

    /**
     * Testa se os métodos setters alteram corretamente os atributos do objeto.
     */
    @Test
    @DisplayName("Deve permitir a alteração de dados via setters")
    void testSetters() {
        // 1. Preparação (Arrange)
        ExtravioBagagem processo = new ExtravioBagagem("JFK", "55555", dataDeAbertura, "ETQ-JFK-01");
        
        // 2. Ação (Act)
        processo.setBase("ORD"); // Setter de Processo
        processo.setEtiquetaBagagemExtraviada("ETQ-ORD-02"); // Setter de ExtravioBagagem
        
        // 3. Verificação (Assert)
        assertEquals("ORD", processo.getBase(), "O setter de base falhou.");
        assertEquals("ETQ-ORD-02", processo.getEtiquetaBagagemExtraviada(), "O setter da etiqueta de extravio falhou.");
    }
    
    /**
     * Testa o método editarInformacoes, que atualiza dados via um Map.
     * @throws InterruptedException para a pausa na thread.
     */
    @Test
    @DisplayName("Deve editar informações do processo e do extravio via Map")
    void testEditarInformacoes() throws InterruptedException {
        // 1. Preparação (Arrange)
        ExtravioBagagem processo = new ExtravioBagagem("SFO", "20202", dataDeAbertura, "ETQ-SFO-01");
        
        // Simula a passagem de um tempo.
        TimeUnit.MILLISECONDS.sleep(10); 
        Date novaData = new Date();
        
        // Cria um mapa com os novos dados.
        Map<String, Object> novosDados = new HashMap<>();
        novosDados.put("dataAbertura", novaData); // Dado herdado de Processo
        novosDados.put("etiquetaBagagemExtraviada", "ETQ-SFO-01-ENCONTRADA"); // Dado específico de ExtravioBagagem
        
        // 2. Ação (Act)
        processo.editarInformacoes(novosDados);
        
        // 3. Verificação (Assert)
        assertEquals(novaData, processo.getDataAbertura(), "A data de abertura deveria ter sido atualizada.");
        assertEquals("ETQ-SFO-01-ENCONTRADA", processo.getEtiquetaBagagemExtraviada(), "A etiqueta de extravio deveria ter sido atualizada.");
        assertEquals("SFO", processo.getBase(), "A base não deveria ter sido alterada.");
    }
}