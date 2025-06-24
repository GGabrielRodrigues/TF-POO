package bagagem.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors; // Importar para usar Streams API

/**
 * A classe {@code ProcessoRepository} é responsável por gerenciar a persistência
 * dos objetos {@link bagagem.model.Processo} e {@link bagagem.model.Recibo}
 * no sistema. Ela lida com o carregamento e salvamento de dados em arquivos
 * locais, bem como a organização de documentos anexados em uma estrutura de pastas.
 * <p>
 * Esta classe utiliza serialização de objetos Java para persistir as listas de processos
 * e recibos, e `DataOutputStream`/`DataInputStream` para os contadores de ID.
 * Documentos são copiados para uma estrutura de diretórios específica no sistema do usuário.
 */
public class ProcessoRepository {

    private static final String PROCESSOS_FILE = "processos.dat";
    private static final String RECIBOS_FILE = "recibos.dat";
    private static final String COUNTERS_FILE = "counters.dat";
    private static final String ROOT_FOLDER_NAME = "BagagemSystemDocs";

    private static java.util.List<Processo> processos = new java.util.ArrayList<>();
    private static java.util.List<Recibo> recibos = new java.util.ArrayList<>();

    static {
        criarEstruturaDePastasDocumentos();
        carregarDados();
    }

    /**
     * Retorna o caminho base para a pasta de documentos do sistema.
     * Esta pasta é criada dentro do diretório "Documents" do usuário.
     *
     * @return O objeto {@link java.nio.file.Path} representando o caminho raiz dos documentos.
     */
    private static java.nio.file.Path getRootDocsPath() {
        String userHome = System.getProperty("user.home");
        return java.nio.file.Paths.get(userHome, "Documents", ROOT_FOLDER_NAME);
    }

    /**
     * Cria a estrutura de pastas necessária para armazenar documentos
     * de processos e recibos, se elas ainda não existirem.
     * As pastas são organizadas por tipo de processo/recibo (Ex: Processos/Danificacao, Recibos/Conserto).
     * Erros durante a criação das pastas são impressos no console de erro.
     */
    private static void criarEstruturaDePastasDocumentos() {
        try {
            java.nio.file.Path root = getRootDocsPath();
            java.nio.file.Files.createDirectories(root.resolve("Processos/Danificacao"));
            java.nio.file.Files.createDirectories(root.resolve("Processos/Extravio"));
            java.nio.file.Files.createDirectories(root.resolve("Processos/ItemEsquecido"));
            java.nio.file.Files.createDirectories(root.resolve("Recibos/Conserto"));
            java.nio.file.Files.createDirectories(root.resolve("Recibos/Indenizacao"));
            java.nio.file.Files.createDirectories(root.resolve("Recibos/EntregaExtraviada"));
            java.nio.file.Files.createDirectories(root.resolve("Recibos/ItemEsquecido"));
        } catch (java.io.IOException e) {
            System.err.println("Erro ao criar estrutura de pastas de documentos: " + e.getMessage());
        }
    }

    /**
     * Salva as listas de processos e recibos, bem como os contadores de ID,
     * em arquivos binários no disco.
     * Os objetos {@link bagagem.model.Processo} e {@link bagagem.model.Recibo} são serializados.
     * Os contadores de ID são salvos como valores long.
     * Erros durante o salvamento são impressos no console de erro.
     */
    public static void salvarDados() {
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(PROCESSOS_FILE))) {
            oos.writeObject(processos);
        } catch (java.io.IOException e) {
            System.err.println("Erro ao salvar processos: " + e.getMessage());
        }
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(RECIBOS_FILE))) {
            oos.writeObject(recibos);
        } catch (java.io.IOException e) {
            System.err.println("Erro ao salvar recibos: " + e.getMessage());
        }

        try (java.io.DataOutputStream dos = new java.io.DataOutputStream(new java.io.FileOutputStream(COUNTERS_FILE))) {
            dos.writeLong(Processo.getNextIdProcesso());
            dos.writeLong(Recibo.getNextIdRecibo());
        } catch (java.io.IOException e) {
            System.err.println("Erro ao salvar contadores de ID: " + e.getMessage());
        }
    }

    /**
     * Carrega as listas de processos e recibos, e os contadores de ID,
     * a partir de arquivos binários no disco.
     * Se os arquivos não existirem ou houver erro na leitura, as listas são inicializadas vazias.
     * Erros durante o carregamento dos contadores são impressos no console de erro.
     */
    @SuppressWarnings("unchecked")
    private static void carregarDados() {
        java.io.File processosFile = new java.io.File(PROCESSOS_FILE);
        if (processosFile.exists() && processosFile.length() > 0) {
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(PROCESSOS_FILE))) {
                processos = (java.util.List<Processo>) ois.readObject();
            } catch (java.io.IOException | ClassNotFoundException e) {
                processos = new java.util.ArrayList<>();
            }
        }
        java.io.File recibosFile = new java.io.File(RECIBOS_FILE);
        if (recibosFile.exists() && recibosFile.length() > 0) {
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(RECIBOS_FILE))) {
                recibos = (java.util.List<Recibo>) ois.readObject();
            } catch (java.io.IOException | ClassNotFoundException e) {
                recibos = new java.util.ArrayList<>();
            }
        }

        java.io.File countersFile = new java.io.File(COUNTERS_FILE);
        if (countersFile.exists() && countersFile.length() > 0) {
            try (java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(COUNTERS_FILE))) {
                Processo.setNextIdProcesso(dis.readLong());
                Recibo.setNextIdRecibo(dis.readLong());
            } catch (java.io.IOException e) {
                System.err.println("Erro ao carregar contadores de ID: " + e.getMessage());
            }
        }
    }

    /**
     * Copia um arquivo de documento de um caminho original para a pasta de documentos
     * interna do sistema, padronizando o nome do arquivo para um processo.
     *
     * @param processo O objeto {@link bagagem.model.Processo} ao qual o documento pertence.
     * @param originalFilePath O caminho completo do arquivo de origem.
     * @return O novo caminho absoluto do arquivo copiado no repositório interno.
     * @throws java.io.IOException Se ocorrer um erro durante a leitura/escrita do arquivo ou criação de diretórios.
     * @throws java.io.FileNotFoundException Se o arquivo de origem não for encontrado.
     */
    private static String copiarDocumentoParaPastaProcesso(Processo processo, String originalFilePath) throws java.io.IOException {
        java.nio.file.Path sourcePath = java.nio.file.Paths.get(originalFilePath);
        if (!java.nio.file.Files.exists(sourcePath)) {
            throw new java.io.FileNotFoundException("Arquivo de origem não encontrado: " + originalFilePath);
        }

        String fileName = sourcePath.getFileName().toString();
        String extensao = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extensao = fileName.substring(lastDotIndex);
        }

        String novoNomeArquivo = processo.getBase() + "-" + processo.getNumeroProcesso() + "_documento" + extensao;

        String tipoPasta = "Outros"; // Pasta padrão caso não seja um tipo específico
        if (processo instanceof DanificacaoBagagem) {
            tipoPasta = "Danificacao";
        } else if (processo instanceof ExtravioBagagem) {
            tipoPasta = "Extravio";
        } else if (processo instanceof ItemEsquecidoAviao) {
            tipoPasta = "ItemEsquecido";
        }

        java.nio.file.Path destinationDir = getRootDocsPath().resolve("Processos").resolve(tipoPasta);
        java.nio.file.Files.createDirectories(destinationDir); // Garante que a pasta de destino exista
        java.nio.file.Path destinationPath = destinationDir.resolve(novoNomeArquivo);

        java.nio.file.Files.copy(sourcePath, destinationPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toString();
    }

    /**
     * Copia um arquivo de documento de um caminho original para a pasta de documentos
     * interna do sistema, padronizando o nome do arquivo para um recibo.
     *
     * @param recibo O objeto {@link bagagem.model.Recibo} ao qual o documento pertence.
     * @param originalFilePath O caminho completo do arquivo de origem.
     * @return O novo caminho absoluto do arquivo copiado no repositório interno.
     * @throws java.io.IOException Se ocorrer um erro durante a leitura/escrita do arquivo ou criação de diretórios.
     * @throws java.io.FileNotFoundException Se o arquivo de origem não for encontrado.
     */
    private static String copiarDocumentoParaPastaRecibo(Recibo recibo, String originalFilePath) throws java.io.IOException {
        java.nio.file.Path sourcePath = java.nio.file.Paths.get(originalFilePath);
        if (!java.nio.file.Files.exists(sourcePath)) {
            throw new java.io.FileNotFoundException("Arquivo de origem do recibo não encontrado: " + originalFilePath);
        }

        String fileName = sourcePath.getFileName().toString();
        String extensao = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extensao = fileName.substring(lastDotIndex);
        }

        String novoNomeArquivo = recibo.getBase() + "-" + recibo.getNumeroProcesso() + "_recibo" + extensao;

        String tipoPasta = "Outros"; // Pasta padrão caso não seja um tipo específico
        if (recibo instanceof ReciboConsertoBagagem) {
            tipoPasta = "Conserto";
        } else if (recibo instanceof ReciboIndenizacaoMilhas) {
            tipoPasta = "Indenizacao";
        } else if (recibo instanceof ReciboEntregaBagagemExtraviada) {
            tipoPasta = "EntregaExtraviada";
        } else if (recibo instanceof ReciboItemEsquecidoAviao) {
            tipoPasta = "ItemEsquecido";
        }

        java.nio.file.Path destinationDir = getRootDocsPath().resolve("Recibos").resolve(tipoPasta);
        java.nio.file.Files.createDirectories(destinationDir); // Garante que a pasta de destino exista
        java.nio.file.Path destinationPath = destinationDir.resolve(novoNomeArquivo);

        java.nio.file.Files.copy(sourcePath, destinationPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toString();
    }

    /**
     * Adiciona um novo processo ao repositório.
     * Se o processo tiver um documento anexado, ele será copiado para a pasta de documentos interna.
     * Após a adição, os dados são salvos no disco.
     *
     * @param processo O objeto {@link bagagem.model.Processo} a ser adicionado.
     */
    public static void adicionarProcesso(Processo processo) {
        if (processo != null) {
            if (processo.getCaminhoDocumento() != null && !processo.getCaminhoDocumento().isEmpty()) {
                try {
                    String novoCaminhoInterno = copiarDocumentoParaPastaProcesso(processo, processo.getCaminhoDocumento());
                    processo.setCaminhoDocumento(novoCaminhoInterno);
                } catch (java.io.IOException e) {
                    System.err.println("Erro ao copiar documento. Processo adicionado sem documento: " + e.getMessage());
                    processo.setCaminhoDocumento(null); // Define o caminho como nulo em caso de erro
                }
            }
            processos.add(processo);
            salvarDados();
        }
    }

    /**
     * Adiciona um novo recibo ao repositório.
     * Se o recibo tiver um documento anexado, ele será copiado para a pasta de documentos interna.
     * Após a adição, os dados são salvos no disco.
     *
     * @param recibo O objeto {@link bagagem.model.Recibo} a ser adicionado.
     */
    public static void adicionarRecibo(Recibo recibo) {
        if (recibo != null) {
            if (recibo.getCaminhoDocumento() != null && !recibo.getCaminhoDocumento().isEmpty()) {
                try {
                    String novoCaminhoInterno = copiarDocumentoParaPastaRecibo(recibo, recibo.getCaminhoDocumento());
                    recibo.setCaminhoDocumento(novoCaminhoInterno);
                } catch (java.io.IOException e) {
                    System.err.println("Erro ao copiar documento do recibo. Recibo adicionado sem documento: " + e.getMessage());
                    recibo.setCaminhoDocumento(null); // Define o caminho como nulo em caso de erro
                }
            }
            recibos.add(recibo);
            salvarDados();
        }
    }

    /**
     * Remove um recibo do repositório.
     * Se o recibo tiver um documento anexado, o arquivo físico também é excluído.
     * Após a remoção, os dados são salvos no disco.
     *
     * @param reciboParaRemover O objeto {@link bagagem.model.Recibo} a ser removido.
     * @return {@code true} se o recibo foi removido com sucesso, {@code false} caso contrário.
     */
    public static boolean removerRecibo(Recibo reciboParaRemover) {
        if (reciboParaRemover != null) {
            if (reciboParaRemover.getCaminhoDocumento() != null) {
                try {
                    java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(reciboParaRemover.getCaminhoDocumento()));
                } catch (java.io.IOException e) {
                    System.err.println("Erro ao deletar arquivo do recibo: " + e.getMessage());
                }
            }
            boolean removido = recibos.remove(reciboParaRemover);
            if (removido) {
                salvarDados();
            }
            return removido;
        }
        return false;
    }

    /**
     * Retorna uma lista imutável de todos os processos cadastrados no repositório.
     *
     * @return Uma {@link java.util.List} de {@link bagagem.model.Processo}s.
     */
    public static java.util.List<Processo> listarTodosProcessos() {
        return java.util.Collections.unmodifiableList(processos);
    }

    /**
     * Retorna uma lista imutável de todos os recibos cadastrados no repositório.
     *
     * @return Uma {@link java.util.List} de {@link bagagem.model.Recibo}s.
     */
    public static java.util.List<Recibo> listarTodosRecibos() {
        return java.util.Collections.unmodifiableList(recibos);
    }

    /**
     * Busca um processo no repositório pela sua base e número.
     *
     * @param base A sigla da base/aeroporto do processo a ser buscado.
     * @param numeroProcesso O número do processo a ser buscado.
     * @return O objeto {@link bagagem.model.Processo} encontrado, ou {@code null} se não for encontrado.
     */
    public static Processo buscarProcessoPorBaseNumero(String base, String numeroProcesso) {
        for (Processo p : processos) {
            if (p.getBase().equals(base) && p.getNumeroProcesso().equals(numeroProcesso)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca um processo no repositório pelo seu ID único.
     *
     * @param id O ID do processo a ser buscado.
     * @return O objeto {@link bagagem.model.Processo} encontrado, ou {@code null} se não for encontrado.
     */
    public static Processo buscarProcessoPorId(long id) {
        for (Processo p : processos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca um recibo no repositório pelo seu ID único.
     *
     * @param id O ID do recibo a ser buscado.
     * @return O objeto {@link bagagem.model.Recibo} encontrado, ou {@code null} se não for encontrado.
     */
    public static Recibo buscarReciboPorId(long id) {
        for (Recibo r : recibos) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    /**
     * Busca um recibo no repositório pela base e número do processo associado.
     * A comparação da base é case-insensitive.
     *
     * @param baseProcesso A sigla da base/aeroporto do processo associado ao recibo.
     * @param numeroProcesso O número do processo associado ao recibo.
     * @return O objeto {@link bagagem.model.Recibo} encontrado, ou {@code null} se não for encontrado.
     */
    public static Recibo buscarReciboPorBaseNumeroProcessoAssociado(String baseProcesso, String numeroProcesso) {
        for (Recibo r : recibos) {
            if (r.getProcessoAssociado() != null &&
                    r.getProcessoAssociado().getBase().equalsIgnoreCase(baseProcesso) &&
                    r.getProcessoAssociado().getNumeroProcesso().equals(numeroProcesso)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Filtra os recibos cadastrados com base na base do processo associado, tipo do recibo e/ou
     * número do processo associado.
     * Os parâmetros nulos ou vazios são ignorados como filtros. A comparação da base é case-insensitive.
     * O tipo de recibo deve corresponder ao nome simples da classe (e.g., "ReciboConsertoBagagem").
     *
     * @param baseProcesso A base do processo associado (pode ser {@code null} ou vazia para não filtrar).
     * @param tipoRecibo O tipo do recibo (pode ser {@code null} ou vazia, ou "Todos" para não filtrar).
     * @param numeroProcesso O número do processo associado (pode ser {@code null} ou vazia para não filtrar).
     * @return Uma {@link java.util.List} imutável de {@link bagagem.model.Recibo}s que correspondem aos filtros.
     */
    public static java.util.List<Recibo> filtrarRecibos(String baseProcesso, String tipoRecibo, String numeroProcesso) {
        java.util.List<Recibo> resultados = new java.util.ArrayList<>();
        for (Recibo r : recibos) {
            boolean matchBase = baseProcesso == null || baseProcesso.trim().isEmpty() || r.getBase().equalsIgnoreCase(baseProcesso.trim());

            boolean matchTipo = tipoRecibo == null || tipoRecibo.trim().isEmpty() ||
                    r.getClass().getSimpleName().equalsIgnoreCase(tipoRecibo.replace("Recibo de ", "").replace(" ", ""));

            boolean matchNumeroProcesso = numeroProcesso == null || numeroProcesso.trim().isEmpty() || r.getNumeroProcesso().equals(numeroProcesso.trim());

            if (matchBase && matchTipo && matchNumeroProcesso) {
                resultados.add(r);
            }
        }
        return java.util.Collections.unmodifiableList(resultados);
    }

    /**
     * Lista todos os recibos associados a um processo específico.
     *
     * @param processo O objeto {@link bagagem.model.Processo} cujos recibos devem ser listados.
     * @return Uma {@link java.util.List} de {@link bagagem.model.Recibo}s associados ao processo.
     */
    public static java.util.List<Recibo> listarRecibosPorProcesso(Processo processo) {
        java.util.List<Recibo> recibosDoProcesso = new java.util.ArrayList<>();
        if (processo != null) {
            for (Recibo r : recibos) {
                if (r.getProcessoAssociado() != null &&
                        r.getProcessoAssociado().getBase().equals(processo.getBase()) &&
                        r.getProcessoAssociado().getNumeroProcesso().equals(processo.getNumeroProcesso())) {
                    recibosDoProcesso.add(r);
                }
            }
        }
        return recibosDoProcesso;
    }

    /**
     * Atualiza um processo existente no repositório.
     * A atualização é baseada no ID do processo.
     * Após a atualização, os dados são salvos no disco.
     *
     * @param processoAtualizado O objeto {@link bagagem.model.Processo} com as informações atualizadas.
     * @return {@code true} se o processo foi encontrado e atualizado, {@code false} caso contrário.
     */
    public static boolean atualizarProcesso(Processo processoAtualizado) {
        if (processoAtualizado == null) return false;
        for (int i = 0; i < processos.size(); i++) {
            Processo p = processos.get(i);
            if (p.getId() == processoAtualizado.getId()) {
                processos.set(i, processoAtualizado);
                salvarDados();
                return true;
            }
        }
        return false;
    }

    /**
     * Atualiza um recibo existente no repositório.
     * A atualização é baseada no ID do recibo.
     * Após a atualização, os dados são salvos no disco.
     *
     * @param reciboAtualizado O objeto {@link bagagem.model.Recibo} com as informações atualizadas.
     * @return {@code true} se o recibo foi encontrado e atualizado, {@code false} caso contrário.
     */
    public static boolean atualizarRecibo(Recibo reciboAtualizado) {
        if (reciboAtualizado == null) return false;
        for (int i = 0; i < recibos.size(); i++) {
            Recibo r = recibos.get(i);
            if (r.getId() == reciboAtualizado.getId()) {
                recibos.set(i, reciboAtualizado);
                salvarDados();
                return true;
            }
        }
        return false;
    }

    /**
     * Remove um processo do repositório utilizando sua base e número.
     * Todos os recibos associados a este processo também são removidos,
     * e os arquivos de documentos do processo e dos recibos são excluídos.
     * Após a remoção, os dados são salvos no disco.
     *
     * @param base A base do processo a ser removido.
     * @param numeroProcesso O número do processo a ser removido.
     * @return {@code true} se o processo foi encontrado e removido, {@code false} caso contrário.
     */
    public static boolean removerProcesso(String base, String numeroProcesso) {
        Processo processoParaRemover = buscarProcessoPorBaseNumero(base, numeroProcesso);
        if (processoParaRemover != null) {
            if (processoParaRemover.getCaminhoDocumento() != null) {
                try {
                    java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(processoParaRemover.getCaminhoDocumento()));
                } catch (java.io.IOException e) {
                    System.err.println("Erro ao deletar arquivo do processo: " + e.getMessage());
                }
            }
            recibos.removeIf(recibo -> recibo.getProcessoAssociado() != null &&
                    recibo.getProcessoAssociado().getId() == processoParaRemover.getId());
            processos.remove(processoParaRemover);
            salvarDados();
            return true;
        }
        return false;
    }

    /**
     * Remove um processo do repositório utilizando seu ID único.
     * Todos os recibos associados a este processo também são removidos,
     * e os arquivos de documentos do processo e dos recibos são excluídos.
     * Após a remoção, os dados são salvos no disco.
     *
     * @param id O ID do processo a ser removido.
     * @return {@code true} se o processo foi encontrado e removido, {@code false} caso contrário.
     */
    public static boolean removerProcesso(long id) {
        Processo processoParaRemover = buscarProcessoPorId(id);
        if (processoParaRemover != null) {
            if (processoParaRemover.getCaminhoDocumento() != null) {
                try {
                    java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(processoParaRemover.getCaminhoDocumento()));
                } catch (java.io.IOException e) {
                    System.err.println("Erro ao deletar arquivo do processo: " + e.getMessage());
                }
            }
            recibos.removeIf(recibo -> recibo.getProcessoAssociado() != null && recibo.getProcessoAssociado().getId() == id);
            processos.remove(processoParaRemover);
            salvarDados();
            return true;
        }
        return false;
    }

    /**
     * Limpa completamente o repositório, removendo todos os processos e recibos
     * da memória, excluindo os arquivos de persistência (.dat) e deletando
     * todas as pastas e arquivos de documentos criados (`BagagemSystemDocs`).
     * Os contadores de ID são resetados para 0.
     * Erros durante a limpeza da pasta de documentos são impressos no console de erro.
     */
    public static void limparRepositorio() {
        processos.clear();
        recibos.clear();
        new java.io.File(PROCESSOS_FILE).delete();
        new java.io.File(RECIBOS_FILE).delete();
        new java.io.File(COUNTERS_FILE).delete();

        Processo.setNextIdProcesso(0);
        Recibo.setNextIdRecibo(0);

        try {
            java.nio.file.Path root = getRootDocsPath();
            if (java.nio.file.Files.exists(root)) {
                // Percorre a árvore de arquivos de forma reversa (de arquivos para diretórios) para deletar
                java.nio.file.Files.walk(root)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(java.nio.file.Path::toFile)
                        .forEach(java.io.File::delete);
            }
        } catch (java.io.IOException e) {
            System.err.println("Erro ao limpar pasta de documentos: " + e.getMessage());
        }
    }

    /**
     * Lista processos no repositório com base em filtros de base e/ou tipo de processo.
     * Se ambos os parâmetros forem nulos, vazios ou "Todos" para o tipo, todos os processos são retornados.
     * A comparação da base e do tipo é case-insensitive.
     *
     * @param base A sigla da base/aeroporto do processo (pode ser {@code null} ou vazia para não filtrar).
     * @param tipoProcesso O tipo do processo (e.g., "DanificacaoBagagem", "Todos" para não filtrar).
     * Pode ser {@code null} ou vazia.
     * @return Uma {@link java.util.List} imutável de {@link bagagem.model.Processo}s que correspondem aos filtros.
     */
    public static java.util.List<Processo> listarProcessosFiltrados(String base, String tipoProcesso) {
        java.util.List<Processo> resultados = new java.util.ArrayList<>();

        if ((base == null || base.trim().isEmpty()) && (tipoProcesso == null || tipoProcesso.trim().isEmpty() || tipoProcesso.equals("Todos"))) {
            return java.util.Collections.unmodifiableList(processos);
        }

        for (Processo p : processos) {
            boolean matchBase = true;
            if (base != null && !base.trim().isEmpty()) {
                matchBase = p.getBase().equalsIgnoreCase(base.trim());
            }

            boolean matchTipo = true;
            if (tipoProcesso != null && !tipoProcesso.trim().isEmpty() && !tipoProcesso.equals("Todos")) {
                matchTipo = p.getClass().getSimpleName().equalsIgnoreCase(tipoProcesso.trim());
            }

            if (matchBase && matchTipo) {
                resultados.add(p);
            }
        }
        return java.util.Collections.unmodifiableList(resultados);
    }
}

