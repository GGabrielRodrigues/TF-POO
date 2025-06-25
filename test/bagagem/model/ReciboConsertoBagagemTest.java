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
 * Classe de testes para a entidade ReciboConsertoBagagem.
 * Testa a criação, validações de associação e manipulação de dados.
 */
@DisplayName("Testes da Classe ReciboConsertoBagagem")
class ReciboConsertoBagagemTest {

    private DanificacaoBagagem processoDanificacao;
    private Date dataAssinatura;

    /**
     * Prepara um ambiente limpo e consistente para cada teste.
     * Cria um processo válido ao qual o recibo pode ser associado.
     */
    @BeforeEach
    void setUp() {
        // Reseta os contadores estáticos para não haver interferência entre os testes
        Processo.setNextIdProcesso(0);
        Recibo.setNextIdRecibo(0);
        
        dataAssinatura = new Date();
        // Cria um processo do tipo DanificacaoBagagem, que é o tipo válido para este recibo.
        processoDanificacao = new DanificacaoBagagem("BSB", "11223", new Date(), "DA-BSB-001");
    }

    /**
     * Testa a criação bem-sucedida de um ReciboConsertoBagagem.
     * Verifica se todos os atributos, herdados e específicos, são preenchidos corretamente.
     */
    @Test
    @DisplayName("Deve criar um recibo de conserto com os dados corretos")
    void testConstrutorEGetters() {
        // 1. Ação (Act)
        ReciboConsertoBagagem recibo = new ReciboConsertoBagagem(
            processoDanificacao.getBase(),
            processoDanificacao.getNumeroProcesso(),
            dataAssinatura,
            processoDanificacao,
            "Oficina do Aeroporto"
        );

        // 2. Verificação (Assert)
        assertNotNull(recibo, "O recibo não deveria ser nulo.");
        assertEquals(1, recibo.getId(), "O ID do recibo deveria ser 1."); //
        assertEquals("BSB", recibo.getBase(), "A base deveria ser a do processo associado."); //
        assertEquals(dataAssinatura, recibo.getDataAssinatura(), "A data de assinatura não corresponde."); //
        assertEquals("Oficina do Aeroporto", recibo.getEntregaOuRetiradaEmAeroporto(), "O local de entrega não corresponde."); //
        
        // Verifica se a associação com o processo foi feita corretamente.
        assertNotNull(recibo.getProcessoAssociado(), "O processo associado não pode ser nulo.");
        assertSame(processoDanificacao, recibo.getProcessoAssociado(), "O objeto do processo associado deve ser o mesmo.");
    }

    /**
     * Testa a regra de negócio que impede a criação de um ReciboConsertoBagagem
     * associado a um tipo de processo inválido (ex: ExtravioBagagem).
     */
    @Test
    @DisplayName("Deve lançar exceção ao associar com tipo de processo incorreto")
    void testDeveLancarExcecaoComProcessoInvalido() {
        // 1. Preparação (Arrange)
        // Cria um processo de um tipo diferente do esperado.
        ExtravioBagagem processoInvalido = new ExtravioBagagem("VCP", "99999", new Date(), "EX-VCP-001");

        // 2. Ação (Act) e 3. Verificação (Assert)
        // O método assertThrows verifica se a execução do código (a lambda "() -> ...") 
        // lança a exceção esperada (IllegalArgumentException).
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ReciboConsertoBagagem(
                processoInvalido.getBase(),
                processoInvalido.getNumeroProcesso(),
                dataAssinatura,
                processoInvalido, // Associando com o tipo de processo errado
                "Qualquer Lugar"
            );
        });
        
        // Opcional: Verifica se a mensagem de erro é a esperada.
        assertEquals("ReciboConsertoBagagem deve ser associado a um processo de DanificacaoBagagem.", exception.getMessage());
    }
    
    /**
     * Testa a validação da classe pai (Recibo) que impede a criação de
     * um recibo com um processo associado nulo.
     */
    @Test
    @DisplayName("Deve lançar exceção ao criar com processo associado nulo")
    void testDeveLancarExcecaoComProcessoNulo() {
        // Verifica se a tentativa de criar um recibo com 'processoAssociado' nulo
        // lança a exceção correta, conforme definido na superclasse Recibo.
        assertThrows(IllegalArgumentException.class, () -> {
            new ReciboConsertoBagagem("GIG", "40404", dataAssinatura, null, "Local nulo");
        });
    }

    /**
     * Testa o método editarDadosRecibo para garantir que ele atualize
     * tanto os dados da classe pai quanto os da classe filha.
     */
    @Test
    @DisplayName("Deve editar informações do recibo via Map")
    void testEditarDadosRecibo() throws InterruptedException {
        // 1. Preparação (Arrange)
        ReciboConsertoBagagem recibo = new ReciboConsertoBagagem("POA", "33221", dataAssinatura, processoDanificacao, "Balcão da Companhia");
        
        TimeUnit.MILLISECONDS.sleep(10); // Pausa para a nova data ser diferente
        Date novaData = new Date();
        
        Map<String, Object> novosDados = new HashMap<>();
        novosDados.put("dataAssinatura", novaData); // Dado da classe pai
        novosDados.put("entregaOuRetiradaEmAeroporto", "Oficina Parceira"); // Dado da classe filha

        // 2. Ação (Act)
        recibo.editarDadosRecibo(novosDados);

        // 3. Verificação (Assert)
        assertEquals(novaData, recibo.getDataAssinatura(), "A data de assinatura deveria ter sido atualizada.");
        assertEquals("Oficina Parceira", recibo.getEntregaOuRetiradaEmAeroporto(), "O local de entrega deveria ter sido atualizado.");
    }
}