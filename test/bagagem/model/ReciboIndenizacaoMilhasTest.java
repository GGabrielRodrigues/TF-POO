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
 * Classe de testes para a entidade ReciboIndenizacaoMilhas.
 * Testa a criação, validações de associação e manipulação de dados.
 */
@DisplayName("Testes da Classe ReciboIndenizacaoMilhas")
class ReciboIndenizacaoMilhasTest {

    private DanificacaoBagagem processoDanificacao;
    private Date dataAssinatura;

    /**
     * Prepara um ambiente limpo para cada teste.
     * Cria um processo do tipo DanificacaoBagagem, que é o tipo correto para a associação.
     */
    @BeforeEach
    void setUp() {
        // Reseta os contadores estáticos
        Processo.setNextIdProcesso(0);
        Recibo.setNextIdRecibo(0);
        
        dataAssinatura = new Date();
        // Cria um processo do tipo correto para a associação.
        processoDanificacao = new DanificacaoBagagem("BSB", "11223", new Date(), "DA-BSB-001");
    }

    /**
     * Testa a criação bem-sucedida de um ReciboIndenizacaoMilhas.
     * Verifica se todos os atributos são preenchidos corretamente.
     */
    @Test
    @DisplayName("Deve criar um recibo de indenização com os dados corretos")
    void testConstrutorComProcessoValido() {
        // 1. Ação (Act)
        ReciboIndenizacaoMilhas recibo = new ReciboIndenizacaoMilhas(
            processoDanificacao.getBase(),
            processoDanificacao.getNumeroProcesso(),
            dataAssinatura,
            processoDanificacao,
            5000 // quantidade de milhas
        );

        // 2. Verificação (Assert)
        assertNotNull(recibo, "O recibo não deveria ser nulo.");
        assertEquals(1, recibo.getId(), "O ID do recibo deveria ser 1."); //
        assertEquals("BSB", recibo.getBase(), "A base deveria ser a do processo associado."); //
        assertEquals(5000, recibo.getQuantidadeMilhas(), "A quantidade de milhas não corresponde."); //
        assertSame(processoDanificacao, recibo.getProcessoAssociado(), "A associação ao processo falhou."); //
    }
    
    /**
     * Testa a regra que impede a associação do recibo com um processo de tipo incorreto.
     */
    @Test
    @DisplayName("Deve lançar exceção ao associar com tipo de processo incorreto")
    void testDeveLancarExcecaoComProcessoInvalido() {
        // 1. Preparação (Arrange)
        // Cria um processo de tipo inválido (qualquer um que não seja DanificacaoBagagem).
        ExtravioBagagem processoInvalido = new ExtravioBagagem("VCP", "99999", new Date(), "EX-VCP-001");

        // 2. Ação e Verificação (Act & Assert)
        // Verifica se a exceção correta é lançada ao tentar criar o recibo.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ReciboIndenizacaoMilhas(
                processoInvalido.getBase(),
                processoInvalido.getNumeroProcesso(),
                dataAssinatura,
                processoInvalido, // Associando com o tipo incorreto
                10000
            );
        });
        
        // Verifica a mensagem da exceção.
        assertEquals("ReciboIndenizacaoMilhas deve ser associado a um processo de DanificacaoBagagem.", exception.getMessage());
    }

    /**
     * Testa o método editarDadosRecibo para garantir que ele atualize os dados corretamente.
     */
    @Test
    @DisplayName("Deve editar informações do recibo via Map com tipo correto")
    void testEditarDadosReciboComSucesso() throws InterruptedException {
        // 1. Preparação (Arrange)
        ReciboIndenizacaoMilhas recibo = new ReciboIndenizacaoMilhas("POA", "33221", dataAssinatura, processoDanificacao, 3000);
        
        TimeUnit.MILLISECONDS.sleep(10); // Pausa para a nova data ser diferente
        Date novaData = new Date();
        
        Map<String, Object> novosDados = new HashMap<>();
        novosDados.put("dataAssinatura", novaData); // Dado da classe pai
        novosDados.put("quantidadeMilhas", 7500); // Dado da classe filha com o tipo correto (Integer)

        // 2. Ação (Act)
        recibo.editarDadosRecibo(novosDados);

        // 3. Verificação (Assert)
        assertEquals(novaData, recibo.getDataAssinatura());
        assertEquals(7500, recibo.getQuantidadeMilhas());
    }

    /**
     * Testa a validação de tipo dentro do método editarDadosRecibo.
     * Se o valor para "quantidadeMilhas" não for um Integer, ele não deve ser alterado.
     */
    @Test
    @DisplayName("Não deve alterar milhas se o tipo no Map for incorreto")
    void testEditarDadosReciboComTipoIncorreto() {
        // 1. Preparação (Arrange)
        ReciboIndenizacaoMilhas recibo = new ReciboIndenizacaoMilhas("MCO", "88888", dataAssinatura, processoDanificacao, 5000);

        Map<String, Object> novosDados = new HashMap<>();
        // Colocando um tipo incorreto (String em vez de Integer)
        novosDados.put("quantidadeMilhas", "cinco mil");

        // 2. Ação (Act)
        recibo.editarDadosRecibo(novosDados);

        // 3. Verificação (Assert)
        // O valor não deve ter sido alterado, pois o tipo no mapa era inválido.
        // A lógica atual no seu método imprime um erro no console, mas não lança exceção.
        // Portanto, o teste correto é verificar que o valor permaneceu o original.
        assertEquals(5000, recibo.getQuantidadeMilhas(), "A quantidade de milhas não deveria mudar se o tipo no Map for incorreto.");
    }
}