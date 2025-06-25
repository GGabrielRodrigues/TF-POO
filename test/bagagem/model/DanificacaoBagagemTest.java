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
 * Classe de testes para a entidade DanificacaoBagagem.
 * Cobre a criação, manipulação de dados e comportamentos específicos e herdados.
 */
@DisplayName("Testes da Classe DanificacaoBagagem")
class DanificacaoBagagemTest {

    // Usaremos uma data fixa para ter consistência nos testes.
    private Date dataDeAbertura;

    /**
     * O método com a anotação @BeforeEach é executado antes de cada teste.
     * É ideal para inicializar objetos ou resetar estados.
     */
    @BeforeEach
    void setUp() {
        // Reseta o contador de ID estático da classe Processo para que os testes
        // de ID sejam previsíveis e não interfiram uns nos outros.
        Processo.setNextIdProcesso(0);
        dataDeAbertura = new Date();
    }

    /**
     * Testa se o construtor da classe preenche corretamente todos os atributos,
     * tanto os da própria classe quanto os herdados da classe Processo.
     */
    @Test
    @DisplayName("Deve criar um processo de danificação com os dados corretos")
    void testConstrutorEGetters() {
        // 1. Preparação (Arrange)
        DanificacaoBagagem processo = new DanificacaoBagagem("GRU", "54321", dataDeAbertura, "DA-12345678");

        // 2. Ação (Act) e 3. Verificação (Assert)
        // Usamos os getters para verificar se os valores foram atribuídos corretamente.
        assertNotNull(processo, "O processo não deveria ser nulo.");
        assertEquals("GRU", processo.getBase(), "A base do processo deve ser GRU."); // Atributo de Processo
        assertEquals("54321", processo.getNumeroProcesso(), "O número do processo deve ser 54321."); // Atributo de Processo
        assertEquals(dataDeAbertura, processo.getDataAbertura(), "A data de abertura deve ser a mesma que foi passada."); // Atributo de Processo
        assertEquals("DA-12345678", processo.getEtiquetaBagagemDanificada(), "A etiqueta da bagagem deve ser DA-12345678."); // Atributo específico de DanificacaoBagagem
    }
    
    /**
     * Testa se a geração de IDs automáticos e sequenciais está funcionando.
     * Este é um comportamento herdado da classe pai Processo.
     */
    @Test
    @DisplayName("Deve gerar IDs sequenciais para novos processos")
    void testGeracaoAutomaticaDeId() {
        // 1. Preparação (Arrange) e 2. Ação (Act)
        DanificacaoBagagem processo1 = new DanificacaoBagagem("GYN", "11111", new Date(), "ETQ-001");
        DanificacaoBagagem processo2 = new DanificacaoBagagem("BSB", "22222", new Date(), "ETQ-002");
        
        // 3. Verificação (Assert)
        // Como o contador foi resetado para 0 no setUp, o primeiro ID deve ser 1 e o segundo 2.
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
        DanificacaoBagagem processo = new DanificacaoBagagem("POA", "98765", dataDeAbertura, "ETQ-POA-01");
        
        // 2. Ação (Act)
        // Modificamos os dados do processo usando os setters.
        processo.setBase("VCP"); //
        processo.setEtiquetaBagagemDanificada("ETQ-VCP-02"); //
        
        // 3. Verificação (Assert)
        // Verificamos se os valores foram de fato atualizados.
        assertEquals("VCP", processo.getBase(), "O setter de base falhou.");
        assertEquals("ETQ-VCP-02", processo.getEtiquetaBagagemDanificada(), "O setter da etiqueta falhou.");
    }

    /**
     * Testa o método editarInformacoes, que deve ser capaz de atualizar
     * múltiplos atributos de uma só vez, incluindo os da classe pai.
     * @throws InterruptedException para a pausa na thread.
     */
    @Test
    @DisplayName("Deve editar informações do processo e da danificação via Map")
    void testEditarInformacoes() throws InterruptedException {
        // 1. Preparação (Arrange)
        DanificacaoBagagem processo = new DanificacaoBagagem("CWB", "10101", dataDeAbertura, "ETQ-CWB-01");
        
        // Simula a passagem de um tempo para que a nova data seja diferente.
        TimeUnit.MILLISECONDS.sleep(10); 
        Date novaData = new Date();
        
        // Cria um mapa com os novos dados.
        Map<String, Object> novosDados = new HashMap<>();
        novosDados.put("dataAbertura", novaData); // Dado da classe pai Processo
        novosDados.put("etiquetaBagagemDanificada", "ETQ-CWB-01-ALTERADA"); // Dado da classe filha DanificacaoBagagem
        
        // 2. Ação (Act)
        processo.editarInformacoes(novosDados);
        
        // 3. Verificação (Assert)
        assertEquals(novaData, processo.getDataAbertura(), "A data de abertura deveria ter sido atualizada.");
        assertEquals("ETQ-CWB-01-ALTERADA", processo.getEtiquetaBagagemDanificada(), "A etiqueta deveria ter sido atualizada.");
        assertEquals("CWB", processo.getBase(), "A base não deveria ter sido alterada.");
    }
}