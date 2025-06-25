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
 * Classe de testes para a entidade ItemEsquecidoAviao.
 * Cobre a criação, manipulação de dados e comportamentos da classe.
 */
@DisplayName("Testes da Classe ItemEsquecidoAviao")
class ItemEsquecidoAviaoTest {

    private Date dataDeAbertura;

    /**
     * Prepara o ambiente antes de cada teste, garantindo a independência entre eles.
     */
    @BeforeEach
    void setUp() {
        // Zera o contador de ID para que cada teste comece do 1.
        Processo.setNextIdProcesso(0);
        dataDeAbertura = new Date();
    }

    /**
     * Testa se o construtor da classe preenche corretamente todos os atributos,
     * tanto os herdados quanto o específico 'numeroVoo'.
     */
    @Test
    @DisplayName("Deve criar um processo de item esquecido com os dados corretos")
    void testConstrutorEGetters() {
        // 1. Preparação (Arrange)
        ItemEsquecidoAviao processo = new ItemEsquecidoAviao("CDG", "77788", dataDeAbertura, "AF 1234");

        // 2. Ação (Act) e 3. Verificação (Assert)
        assertNotNull(processo, "O processo não deveria ser nulo.");
        assertEquals("CDG", processo.getBase(), "A base do processo deve ser CDG."); // Herdado de Processo
        assertEquals("77788", processo.getNumeroProcesso(), "O número do processo deve ser 77788."); // Herdado de Processo
        assertEquals(dataDeAbertura, processo.getDataAbertura(), "A data de abertura deve ser a mesma."); // Herdado de Processo
        assertEquals("AF 1234", processo.getNumeroVoo(), "O número do voo deve ser AF 1234."); // Específico de ItemEsquecidoAviao
    }

    /**
     * Testa a geração sequencial de IDs para a classe ItemEsquecidoAviao.
     */
    @Test
    @DisplayName("Deve gerar IDs sequenciais para novos processos de item esquecido")
    void testGeracaoAutomaticaDeId() {
        // 1. Preparação (Arrange) e 2. Ação (Act)
        ItemEsquecidoAviao processo1 = new ItemEsquecidoAviao("LIS", "55555", new Date(), "TP 0087");
        ItemEsquecidoAviao processo2 = new ItemEsquecidoAviao("MAD", "66666", new Date(), "IB 6025");
        
        // 3. Verificação (Assert)
        // O contador foi resetado para 0, então os IDs devem ser 1 e 2.
        assertEquals(1, processo1.getId(), "O ID do primeiro processo deveria ser 1.");
        assertEquals(2, processo2.getId(), "O ID do segundo processo deveria ser 2.");
    }

    /**
     * Testa se os métodos setters alteram corretamente os atributos do objeto.
     */
    @Test
    @DisplayName("Deve permitir a alteração de dados via setters")
    void testSetters() {
        // 1. Preparação (Arrange)
        ItemEsquecidoAviao processo = new ItemEsquecidoAviao("LHR", "44332", dataDeAbertura, "BA 0247");
        
        // 2. Ação (Act)
        processo.setBase("FRA"); // Setter de Processo
        processo.setNumeroVoo("LH 1122"); // Setter de ItemEsquecidoAviao
        
        // 3. Verificação (Assert)
        assertEquals("FRA", processo.getBase(), "O setter de base falhou.");
        assertEquals("LH 1122", processo.getNumeroVoo(), "O setter de número do voo falhou.");
    }
    
    /**
     * Testa o método editarInformacoes, que atualiza dados usando um Map.
     * @throws InterruptedException para a pausa na thread.
     */
    @Test
    @DisplayName("Deve editar informações do processo e do item esquecido via Map")
    void testEditarInformacoes() throws InterruptedException {
        // 1. Preparação (Arrange)
        ItemEsquecidoAviao processo = new ItemEsquecidoAviao("AMS", "30303", dataDeAbertura, "KL 0791");
        
        // Simula a passagem de um tempo.
        TimeUnit.MILLISECONDS.sleep(10); 
        Date novaData = new Date();
        
        // Cria um mapa com os novos dados.
        Map<String, Object> novosDados = new HashMap<>();
        novosDados.put("dataAbertura", novaData); // Dado herdado de Processo
        novosDados.put("numeroVoo", "KL 0792"); // Dado específico de ItemEsquecidoAviao
        
        // 2. Ação (Act)
        processo.editarInformacoes(novosDados);
        
        // 3. Verificação (Assert)
        assertEquals(novaData, processo.getDataAbertura(), "A data de abertura deveria ter sido atualizada.");
        assertEquals("KL 0792", processo.getNumeroVoo(), "O número do voo deveria ter sido atualizado.");
        assertEquals("AMS", processo.getBase(), "A base não deveria ter sido alterada.");
    }
}