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
 * Classe de testes para a entidade ReciboItemEsquecidoAviao.
 * Testa a criação, validações de associação e manipulação de dados.
 */
@DisplayName("Testes da Classe ReciboItemEsquecidoAviao")
class ReciboItemEsquecidoAviaoTest {

    private ItemEsquecidoAviao processoItemEsquecido;
    private Date dataAssinatura;

    /**
     * Prepara um ambiente limpo para cada teste.
     * Cria um processo do tipo ItemEsquecidoAviao, que é a dependência correta.
     */
    @BeforeEach
    void setUp() {
        // Reseta os contadores estáticos
        Processo.setNextIdProcesso(0);
        Recibo.setNextIdRecibo(0);
        
        dataAssinatura = new Date();
        // Cria um processo do tipo correto para a associação.
        processoItemEsquecido = new ItemEsquecidoAviao("JFK", "78901", new Date(), "AA 0975");
    }

    /**
     * Testa a criação bem-sucedida de um recibo quando associado a um
     * processo de Item Esquecido em Avião.
     */
    @Test
    @DisplayName("Deve criar um recibo com sucesso quando associado a um processo de item esquecido")
    void testConstrutorComProcessoValido() {
        // 1. Ação (Act)
        ReciboItemEsquecidoAviao recibo = new ReciboItemEsquecidoAviao(
            processoItemEsquecido.getBase(),
            processoItemEsquecido.getNumeroProcesso(),
            dataAssinatura,
            processoItemEsquecido,
            "RG 12.345.678-9" // Documento do cliente
        );

        // 2. Verificação (Assert)
        assertNotNull(recibo, "O recibo não deveria ser nulo.");
        assertEquals(1, recibo.getId(), "O ID do recibo deveria ser 1."); //
        assertEquals("JFK", recibo.getBase(), "A base deve ser a do processo associado."); //
        assertEquals("RG 12.345.678-9", recibo.getDocumentoIdentificacaoClienteRetirada(), "O documento do cliente não corresponde."); //
        assertSame(processoItemEsquecido, recibo.getProcessoAssociado(), "A associação ao processo falhou."); //
    }
    
    /**
     * Testa a regra que impede a associação do recibo com um processo de tipo incorreto.
     */
    @Test
    @DisplayName("Deve lançar exceção ao associar com tipo de processo incorreto")
    void testDeveLancarExcecaoComProcessoInvalido() {
        // 1. Preparação (Arrange)
        // Cria um processo de tipo inválido.
        DanificacaoBagagem processoInvalido = new DanificacaoBagagem("CDG", "22222", new Date(), "DA-CDG-222");

        // 2. Ação e Verificação (Act & Assert)
        // Verifica se a exceção correta é lançada ao tentar criar o recibo.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ReciboItemEsquecidoAviao(
                processoInvalido.getBase(),
                processoInvalido.getNumeroProcesso(),
                dataAssinatura,
                processoInvalido, // Associando com o tipo incorreto
                "CPF 111.222.333-44"
            );
        });
        
        // Verifica a mensagem da exceção.
        assertEquals("ReciboItemEsquecidoAviao deve ser associado a um processo de ItemEsquecidoAviao.", exception.getMessage());
    }

    /**
     * Testa a geração sequencial de IDs para múltiplos recibos.
     */
    @Test
    @DisplayName("Deve gerar IDs sequenciais para novos recibos")
    void testGeracaoAutomaticaDeId() {
        // 1. Ação (Act)
        ReciboItemEsquecidoAviao recibo1 = new ReciboItemEsquecidoAviao("LAX", "33333", dataAssinatura, processoItemEsquecido, "DOC-1");
        
        ItemEsquecidoAviao outroProcesso = new ItemEsquecidoAviao("MIA", "44444", new Date(), "BA 208");
        ReciboItemEsquecidoAviao recibo2 = new ReciboItemEsquecidoAviao("MIA", "44444", dataAssinatura, outroProcesso, "DOC-2");

        // 2. Verificação (Assert)
        assertEquals(1, recibo1.getId());
        assertEquals(2, recibo2.getId());
    }

    /**
     * Testa se o método de edição em massa funciona para os atributos da classe.
     */
    @Test
    @DisplayName("Deve editar informações do recibo via Map")
    void testEditarDadosRecibo() throws InterruptedException {
        // 1. Preparação (Arrange)
        ReciboItemEsquecidoAviao recibo = new ReciboItemEsquecidoAviao("SCL", "55555", dataAssinatura, processoItemEsquecido, "Passaporte 123");
        
        TimeUnit.MILLISECONDS.sleep(10);
        Date novaData = new Date();
        
        Map<String, Object> novosDados = new HashMap<>();
        novosDados.put("dataAssinatura", novaData); // Dado da classe pai
        novosDados.put("documentoIdentificacaoClienteRetirada", "CNH 98765"); // Dado da classe filha

        // 2. Ação (Act)
        recibo.editarDadosRecibo(novosDados);

        // 3. Verificação (Assert)
        assertEquals(novaData, recibo.getDataAssinatura());
        assertEquals("CNH 98765", recibo.getDocumentoIdentificacaoClienteRetirada());
    }
}