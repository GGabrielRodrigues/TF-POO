package bagagem.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe de teste para ProcessoRepository.
 * Garante que as operações de adicionar, buscar e remover processos funcionam como esperado.
 */
class ProcessoRepositoryTest {

    /**
     * Este método é executado antes de cada teste (@Test).
     * Usamos ele para limpar o repositório e garantir que cada teste
     * comece com um ambiente limpo e previsível.
     */
    @BeforeEach
    void setUp() {
        // Limpa todos os dados para garantir que um teste não interfira no outro.
        ProcessoRepository.limparRepositorio();
    }

    /**
     * Testa o cadastro de um novo processo e sua busca posterior.
     * Corresponde ao cenário de sucesso do CT-001-S.
     */
    @Test
    void testAdicionarEBuscarProcesso() {
        // 1. Preparação (Arrange)
        // Cria uma instância de um processo de Danificação de Bagagem.
        DanificacaoBagagem processoOriginal = new DanificacaoBagagem(
            "GYN", 
            "12345", 
            new Date(), 
            "AV-98765432"
        );
        // Define um caminho de documento fictício para o teste.
        processoOriginal.setCaminhoDocumento("C:/temp/teste.jpg");
        processoOriginal.setTipoArquivoDocumento("JPG");
        processoOriginal.setTamanhoArquivoDocumento(1024);

        // 2. Ação (Act)
        // Adiciona o processo ao repositório.
        ProcessoRepository.adicionarProcesso(processoOriginal);

        // Busca o processo que acabamos de adicionar.
        Processo processoBuscado = ProcessoRepository.buscarProcessoPorBaseNumero("GYN", "12345");

        // 3. Verificação (Assert)
        // Verifica se o processo buscado não é nulo (ou seja, foi encontrado).
        assertNotNull(processoBuscado, "O processo deveria ter sido encontrado no repositório.");

        // Verifica se os dados do processo buscado são os mesmos do processo original.
        assertEquals("GYN", processoBuscado.getBase(), "A base do processo não corresponde.");
        assertEquals("12345", processoBuscado.getNumeroProcesso(), "O número do processo não corresponde.");
        
        // Verifica se o tipo do processo está correto.
        assertTrue(processoBuscado instanceof DanificacaoBagagem, "O tipo do processo deveria ser DanificacaoBagagem.");
        
        // Converte o processo para o tipo específico para verificar o campo exclusivo.
        DanificacaoBagagem processoDanificacaoBuscado = (DanificacaoBagagem) processoBuscado;
        assertEquals("AV-98765432", processoDanificacaoBuscado.getEtiquetaBagagemDanificada(), "A etiqueta da bagagem não corresponde.");
    }

    /**
     * Testa a tentativa de buscar um processo que não existe.
     */
    @Test
    void testBuscarProcessoInexistente() {
        // 1. Ação (Act)
        // Tenta buscar um processo que sabemos que não foi cadastrado.
        Processo processoBuscado = ProcessoRepository.buscarProcessoPorBaseNumero("XXX", "00000");

        // 2. Verificação (Assert)
        // Verifica se o resultado da busca é nulo, como esperado.
        assertNull(processoBuscado, "A busca por um processo inexistente deveria retornar nulo.");
    }
}