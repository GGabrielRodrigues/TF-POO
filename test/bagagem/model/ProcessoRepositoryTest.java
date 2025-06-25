package bagagem.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Classe de testes COMPLETA para o ProcessoRepository.
 * Testa a adição, remoção, busca, atualização e persistência de processos e recibos.
 */
@DisplayName("Testes Completos do Repositório de Processos")
class ProcessoRepositoryTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ProcessoRepository.limparRepositorio();
    }
    
    @AfterAll
    static void tearDownAll() {
        ProcessoRepository.limparRepositorio();
    }

    @Test
    @DisplayName("Deve adicionar e buscar um processo com sucesso")
    void testAdicionarEBuscarProcesso() {
        DanificacaoBagagem p1 = new DanificacaoBagagem("GYN", "12345", new Date(), "DA-001");
        ProcessoRepository.adicionarProcesso(p1);
        
        Processo processoBuscado = ProcessoRepository.buscarProcessoPorId(1L);
        
        assertNotNull(processoBuscado);
        assertEquals(1, ProcessoRepository.listarTodosProcessos().size());
        assertEquals("GYN", processoBuscado.getBase());
    }

    @Test
    @DisplayName("Deve copiar o documento para a pasta do sistema ao adicionar processo")
    void testCopiaDeDocumentoDoProcesso() throws IOException {
        File arquivoOrigem = tempDir.resolve("documento_teste.pdf").toFile();
        arquivoOrigem.createNewFile();
        
        DanificacaoBagagem p1 = new DanificacaoBagagem("BSB", "54321", new Date(), "DA-002");
        p1.setCaminhoDocumento(arquivoOrigem.getAbsolutePath());

        ProcessoRepository.adicionarProcesso(p1);

        Processo processoSalvo = ProcessoRepository.buscarProcessoPorId(1L);
        String caminhoFinal = processoSalvo.getCaminhoDocumento();
        
        assertNotNull(caminhoFinal);
        assertTrue(caminhoFinal.contains("BagagemSystemDocs"));
        assertTrue(Files.exists(Path.of(caminhoFinal)));
    }
    
    @Test
    @DisplayName("Deve remover um processo e seus recibos associados em cascata")
    void testRemoverProcessoEmCascata() {
        DanificacaoBagagem p1 = new DanificacaoBagagem("CWB", "10203", new Date(), "DA-004");
        ProcessoRepository.adicionarProcesso(p1);
        ReciboConsertoBagagem r1 = new ReciboConsertoBagagem("CWB", "10203", new Date(), p1, "Loja do Aeroporto");
        ProcessoRepository.adicionarRecibo(r1);

        assertEquals(1, ProcessoRepository.listarTodosProcessos().size());
        assertEquals(1, ProcessoRepository.listarTodosRecibos().size());
        
        boolean removido = ProcessoRepository.removerProcesso(p1.getId());
        
        assertTrue(removido);
        assertTrue(ProcessoRepository.listarTodosProcessos().isEmpty());
        assertTrue(ProcessoRepository.listarTodosRecibos().isEmpty());
    }
    
    @Test
    @DisplayName("Deve persistir os dados em arquivo ao salvar")
    void testPersistenciaAoSalvar() {
        File arquivoDeProcessos = new File("processos.dat");
        assertFalse(arquivoDeProcessos.exists());
        
        DanificacaoBagagem p1 = new DanificacaoBagagem("REC", "30405", new Date(), "DA-005");
        ProcessoRepository.adicionarProcesso(p1); 

        assertTrue(arquivoDeProcessos.exists());
        assertTrue(arquivoDeProcessos.length() > 0);
    }
    
    // --- NOVOS TESTES ADICIONADOS ---

    @Test
    @DisplayName("Deve atualizar um processo existente")
    void testAtualizarProcesso() throws InterruptedException {
        // 1. Arrange
        DanificacaoBagagem p1 = new DanificacaoBagagem("SDU", "11111", new Date(), "DA-SDU-1");
        ProcessoRepository.adicionarProcesso(p1);
        
        TimeUnit.MILLISECONDS.sleep(10); // Garante que a data de atualização seja diferente
        Date novaData = new Date();
        
        // 2. Act
        // Modificamos o objeto em memória e pedimos para o repositório atualizar.
        p1.setDataAbertura(novaData);
        boolean atualizado = ProcessoRepository.atualizarProcesso(p1);

        // 3. Assert
        assertTrue(atualizado);
        Processo processoBuscado = ProcessoRepository.buscarProcessoPorId(p1.getId());
        assertEquals(novaData, processoBuscado.getDataAbertura(), "A data de abertura deveria ter sido atualizada.");
    }
    
    @Test
    @DisplayName("Deve remover apenas o recibo, mantendo o processo")
    void testRemoverReciboApenas() {
        // 1. Arrange
        DanificacaoBagagem p1 = new DanificacaoBagagem("FOR", "22222", new Date(), "DA-FOR-1");
        ProcessoRepository.adicionarProcesso(p1);
        ReciboConsertoBagagem r1 = new ReciboConsertoBagagem("FOR", "22222", new Date(), p1, "Oficina");
        ProcessoRepository.adicionarRecibo(r1);
        
        // 2. Act
        boolean removido = ProcessoRepository.removerRecibo(r1);
        
        // 3. Assert
        assertTrue(removido);
        assertTrue(ProcessoRepository.listarTodosRecibos().isEmpty(), "A lista de recibos deveria estar vazia.");
        assertFalse(ProcessoRepository.listarTodosProcessos().isEmpty(), "A lista de processos NÃO deveria estar vazia.");
        assertEquals(1, ProcessoRepository.listarTodosProcessos().size());
    }
    
    @Test
    @DisplayName("Deve filtrar recibos por base, tipo e número do processo")
    void testFiltrarRecibos() {
        // 1. Arrange
        DanificacaoBagagem p1 = new DanificacaoBagagem("GYN", "100", new Date(), "DA-GYN-1");
        ProcessoRepository.adicionarProcesso(p1);
        ProcessoRepository.adicionarRecibo(new ReciboConsertoBagagem("GYN", "100", new Date(), p1, "Oficina GYN"));

        ExtravioBagagem p2 = new ExtravioBagagem("BSB", "200", new Date(), "EX-BSB-1");
        ProcessoRepository.adicionarProcesso(p2);
        ProcessoRepository.adicionarRecibo(new ReciboEntregaBagagemExtraviada("BSB", "200", new Date(), p2, "Casa do Cliente"));
        
        // 2. Act & Assert
        // Filtra por base
        List<Recibo> recibosGyn = ProcessoRepository.filtrarRecibos("GYN", null, null);
        assertEquals(1, recibosGyn.size());
        
        // Filtra por tipo
        List<Recibo> recibosEntrega = ProcessoRepository.filtrarRecibos(null, "ReciboEntregaBagagemExtraviada", null);
        assertEquals(1, recibosEntrega.size());
        assertTrue(recibosEntrega.get(0) instanceof ReciboEntregaBagagemExtraviada);

        // Filtra por número do processo (que deve ser único)
        List<Recibo> reciboPorNumero = ProcessoRepository.filtrarRecibos(null, null, "100");
        assertEquals(1, reciboPorNumero.size());

        // Filtro sem resultado
        List<Recibo> semResultado = ProcessoRepository.filtrarRecibos("MIA", null, null);
        assertTrue(semResultado.isEmpty());
    }
}