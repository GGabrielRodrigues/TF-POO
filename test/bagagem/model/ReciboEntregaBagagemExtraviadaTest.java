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
 * Classe de testes para a entidade ReciboEntregaBagagemExtraviada.
 * Testa a criação, validações de associação e manipulação de dados.
 */
@DisplayName("Testes da Classe ReciboEntregaBagagemExtraviada")
class ReciboEntregaBagagemExtraviadaTest {

    private ExtravioBagagem processoExtravio;
    private Date dataAssinatura;

    /**
     * Prepara um ambiente limpo para cada teste.
     * Cria um processo do tipo ExtravioBagagem, que é o tipo correto para
     * a associação com ReciboEntregaBagagemExtraviada.
     */
    @BeforeEach
    void setUp() {
        // Reseta os contadores estáticos
        Processo.setNextIdProcesso(0);
        Recibo.setNextIdRecibo(0);
        
        dataAssinatura = new Date();
        // Cria um processo do tipo correto.
        processoExtravio = new ExtravioBagagem("GRU", "54321", new Date(), "EX-GRU-123");
    }

    /**
     * Testa a criação bem-sucedida de um recibo quando associado a um
     * processo de Extravio de Bagagem.
     */
    @Test
    @DisplayName("Deve criar um recibo com sucesso quando associado a um processo de extravio")
    void testConstrutorComProcessoValido() {
        // 1. Ação (Act)
        ReciboEntregaBagagemExtraviada recibo = new ReciboEntregaBagagemExtraviada(
            processoExtravio.getBase(),
            processoExtravio.getNumeroProcesso(),
            dataAssinatura,
            processoExtravio,
            "Entregue no endereço do cliente"
        );

        // 2. Verificação (Assert)
        assertNotNull(recibo, "O recibo não deveria ser nulo.");
        assertEquals(1, recibo.getId(), "O ID do recibo deveria ser 1."); //
        assertEquals("GRU", recibo.getBase(), "A base deve ser a do processo associado."); //
        assertEquals("Entregue no endereço do cliente", recibo.getEntregaOuRetiradaEmAeroporto(), "O local de entrega não corresponde."); //
        assertSame(processoExtravio, recibo.getProcessoAssociado(), "A associação ao processo falhou."); //
    }
    
    /**
     * Testa a regra de negócio que impede a criação do recibo quando o
     * processo associado é de um tipo incorreto (ex: DanificacaoBagagem).
     */
    @Test
    @DisplayName("Deve lançar exceção ao associar com tipo de processo incorreto")
    void testDeveLancarExcecaoComProcessoInvalido() {
        // 1. Preparação (Arrange)
        // Cria um processo de tipo DanificacaoBagagem, que é inválido para este recibo.
        DanificacaoBagagem processoInvalido = new DanificacaoBagagem("CWB", "11111", new Date(), "DA-CWB-111");

        // 2. Ação e Verificação (Act & Assert)
        // Verifica se a exceção correta é lançada ao tentar criar o recibo.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ReciboEntregaBagagemExtraviada(
                processoInvalido.getBase(),
                processoInvalido.getNumeroProcesso(),
                dataAssinatura,
                processoInvalido, // Associando com o tipo incorreto
                "Local inválido"
            );
        });
        
        // Opcional: verifica a mensagem da exceção.
        assertEquals("ReciboEntregaBagagemExtraviada deve ser associado a um processo de ExtravioBagagem.", exception.getMessage());
    }

    /**
     * Testa a geração sequencial de IDs para múltiplos recibos.
     */
    @Test
    @DisplayName("Deve gerar IDs sequenciais para novos recibos")
    void testGeracaoAutomaticaDeId() {
        // 1. Ação (Act)
        ReciboEntregaBagagemExtraviada recibo1 = new ReciboEntregaBagagemExtraviada("RBR", "22222", dataAssinatura, processoExtravio, "Local 1");
        // Precisamos de outro processo de extravio para criar um segundo recibo associado a ele
        ExtravioBagagem outroProcessoExtravio = new ExtravioBagagem("POA", "33333", new Date(), "EX-POA-321");
        ReciboEntregaBagagemExtraviada recibo2 = new ReciboEntregaBagagemExtraviada("POA", "33333", dataAssinatura, outroProcessoExtravio, "Local 2");

        // 2. Verificação (Assert)
        assertEquals(1, recibo1.getId(), "O ID do primeiro recibo deveria ser 1.");
        assertEquals(2, recibo2.getId(), "O ID do segundo recibo deveria ser 2.");
    }

    /**
     * Testa se o método de edição em massa funciona para os atributos
     * da classe pai e da classe filha.
     */
    @Test
    @DisplayName("Deve editar informações do recibo via Map")
    void testEditarDadosRecibo() throws InterruptedException {
        // 1. Preparação (Arrange)
        ReciboEntregaBagagemExtraviada recibo = new ReciboEntregaBagagemExtraviada("SDU", "44444", dataAssinatura, processoExtravio, "Balcão Aeroporto SDU");
        
        TimeUnit.MILLISECONDS.sleep(10); // Pausa para a nova data ser diferente
        Date novaData = new Date();
        
        Map<String, Object> novosDados = new HashMap<>();
        novosDados.put("dataAssinatura", novaData); // Dado da classe pai
        novosDados.put("entregaOuRetiradaEmAeroporto", "Retirado na central de bagagens"); // Dado da classe filha

        // 2. Ação (Act)
        recibo.editarDadosRecibo(novosDados);

        // 3. Verificação (Assert)
        assertEquals(novaData, recibo.getDataAssinatura());
        assertEquals("Retirado na central de bagagens", recibo.getEntregaOuRetiradaEmAeroporto());
    }
}