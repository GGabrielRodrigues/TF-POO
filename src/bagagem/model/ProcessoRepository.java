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

public class ProcessoRepository {

    private static final String PROCESSOS_FILE = "processos.dat";
    private static final String RECIBOS_FILE = "recibos.dat";
    private static final String COUNTERS_FILE = "counters.dat"; // NOVO ARQUIVO para os contadores estáticos
    private static final String ROOT_FOLDER_NAME = "BagagemSystemDocs";

    private static List<Processo> processos = new ArrayList<>();
    private static List<Recibo> recibos = new ArrayList<>();

    static {
        criarEstruturaDePastasDocumentos();
        carregarDados();
    }

    private static Path getRootDocsPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Documents", ROOT_FOLDER_NAME);
    }

    private static void criarEstruturaDePastasDocumentos() {
        try {
            Path root = getRootDocsPath();
            Files.createDirectories(root.resolve("Processos/Danificacao"));
            Files.createDirectories(root.resolve("Processos/Extravio"));
            Files.createDirectories(root.resolve("Processos/ItemEsquecido"));
            Files.createDirectories(root.resolve("Recibos/Conserto"));
            Files.createDirectories(root.resolve("Recibos/Indenizacao"));
            Files.createDirectories(root.resolve("Recibos/EntregaExtraviada"));
            Files.createDirectories(root.resolve("Recibos/ItemEsquecido"));
        } catch (IOException e) {
            System.err.println("Erro ao criar estrutura de pastas de documentos: " + e.getMessage());
        }
    }

    public static void salvarDados() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROCESSOS_FILE))) {
            oos.writeObject(processos);
        } catch (IOException e) {
            System.err.println("Erro ao salvar processos: " + e.getMessage());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RECIBOS_FILE))) {
            oos.writeObject(recibos);
        } catch (IOException e) {
            System.err.println("Erro ao salvar recibos: " + e.getMessage());
        }
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(COUNTERS_FILE))) {
            dos.writeLong(Processo.getNextIdProcesso());
            dos.writeLong(Recibo.getNextIdRecibo());
        } catch (IOException e) {
            System.err.println("Erro ao salvar contadores de ID: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void carregarDados() {
        File processosFile = new File(PROCESSOS_FILE);
        if (processosFile.exists() && processosFile.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PROCESSOS_FILE))) {
                processos = (List<Processo>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                processos = new ArrayList<>();
            }
        }
        File recibosFile = new File(RECIBOS_FILE);
        if (recibosFile.exists() && recibosFile.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RECIBOS_FILE))) {
                recibos = (List<Recibo>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                recibos = new ArrayList<>();
            }
        }
        
        // NOVO: Carregar os contadores estáticos
        File countersFile = new File(COUNTERS_FILE);
        if (countersFile.exists() && countersFile.length() > 0) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(COUNTERS_FILE))) {
                Processo.setNextIdProcesso(dis.readLong());
                Recibo.setNextIdRecibo(dis.readLong());
            } catch (IOException e) {
                System.err.println("Erro ao carregar contadores de ID: " + e.getMessage());
                // Se der erro, os contadores permanecerão em 0, e IDs podem ser duplicados em nova execução.
                // Isso pode ser uma falha grave, então é bom alertar.
            }
        }
    }
    
    private static String copiarDocumentoParaPastaProcesso(Processo processo, String originalFilePath) throws IOException {
        Path sourcePath = Paths.get(originalFilePath);
        if (!Files.exists(sourcePath)) {
            throw new FileNotFoundException("Arquivo de origem não encontrado: " + originalFilePath);
        }

        String fileName = sourcePath.getFileName().toString();
        String extensao = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extensao = fileName.substring(lastDotIndex);
        }

        String novoNomeArquivo = processo.getBase() + "-" + processo.getNumeroProcesso() + "_documento" + extensao;

        String tipoPasta = "Outros";
        if (processo instanceof DanificacaoBagagem) tipoPasta = "Danificacao";
        else if (processo instanceof ExtravioBagagem) tipoPasta = "Extravio";
        else if (processo instanceof ItemEsquecidoAviao) tipoPasta = "ItemEsquecido";

        Path destinationDir = getRootDocsPath().resolve("Processos").resolve(tipoPasta);
        Files.createDirectories(destinationDir);
        Path destinationPath = destinationDir.resolve(novoNomeArquivo);

        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toString();
    }
    
    private static String copiarDocumentoParaPastaRecibo(Recibo recibo, String originalFilePath) throws IOException {
        Path sourcePath = Paths.get(originalFilePath);
        if (!Files.exists(sourcePath)) {
            throw new FileNotFoundException("Arquivo de origem do recibo não encontrado: " + originalFilePath);
        }

        String fileName = sourcePath.getFileName().toString();
        String extensao = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extensao = fileName.substring(lastDotIndex);
        }

        String novoNomeArquivo = recibo.getBase() + "-" + recibo.getNumeroProcesso() + "_recibo" + extensao;

        String tipoPasta = "Outros";
        if (recibo instanceof ReciboConsertoBagagem) tipoPasta = "Conserto";
        else if (recibo instanceof ReciboIndenizacaoMilhas) tipoPasta = "Indenizacao";
        else if (recibo instanceof ReciboEntregaBagagemExtraviada) tipoPasta = "EntregaExtraviada";
        else if (recibo instanceof ReciboItemEsquecidoAviao) tipoPasta = "ItemEsquecido";

        Path destinationDir = getRootDocsPath().resolve("Recibos").resolve(tipoPasta);
        Files.createDirectories(destinationDir);
        Path destinationPath = destinationDir.resolve(novoNomeArquivo);

        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        return destinationPath.toString();
    }

    public static void adicionarProcesso(Processo processo) {
        if (processo != null) {
            if (processo.getCaminhoDocumento() != null && !processo.getCaminhoDocumento().isEmpty()) {
                try {
                    String novoCaminhoInterno = copiarDocumentoParaPastaProcesso(processo, processo.getCaminhoDocumento());
                    processo.setCaminhoDocumento(novoCaminhoInterno);
                } catch (IOException e) {
                    System.err.println("Erro ao copiar documento. Processo adicionado sem documento: " + e.getMessage());
                    processo.setCaminhoDocumento(null);
                }
            }
            processos.add(processo);
            salvarDados();
        }
    }

    public static void adicionarRecibo(Recibo recibo) {
        if (recibo != null) {
            if (recibo.getCaminhoDocumento() != null && !recibo.getCaminhoDocumento().isEmpty()) {
                try {
                    String novoCaminhoInterno = copiarDocumentoParaPastaRecibo(recibo, recibo.getCaminhoDocumento());
                    recibo.setCaminhoDocumento(novoCaminhoInterno);
                } catch (IOException e) {
                    System.err.println("Erro ao copiar documento do recibo. Recibo adicionado sem documento: " + e.getMessage());
                    recibo.setCaminhoDocumento(null);
                }
            }
            recibos.add(recibo);
            salvarDados();
        }
    }

    public static boolean removerRecibo(Recibo reciboParaRemover) {
        if (reciboParaRemover != null) {
            if (reciboParaRemover.getCaminhoDocumento() != null) {
                try {
                    Files.deleteIfExists(Paths.get(reciboParaRemover.getCaminhoDocumento()));
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo do recibo: " + e.getMessage());
                }
            }
            boolean removido = recibos.remove(reciboParaRemover);
            if(removido) {
                salvarDados();
            }
            return removido;
        }
        return false;
    }

    public static List<Processo> listarTodosProcessos() {
        return Collections.unmodifiableList(processos);
    }
    
    public static List<Recibo> listarTodosRecibos() {
        return Collections.unmodifiableList(recibos);
    }

    public static Processo buscarProcessoPorBaseNumero(String base, String numeroProcesso) {
        for (Processo p : processos) {
            if (p.getBase().equals(base) && p.getNumeroProcesso().equals(numeroProcesso)) {
                return p;
            }
        }
        return null;
    }

    /**
     * NOVO MÉTODO: Busca um processo pelo seu ID único.
     * @param id O ID do processo a ser buscado.
     * @return O objeto Processo correspondente ao ID, ou null se não encontrado.
     */
    public static Processo buscarProcessoPorId(long id) {
        for (Processo p : processos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
    
    public static List<Recibo> listarRecibosPorProcesso(Processo processo) {
        List<Recibo> recibosDoProcesso = new ArrayList<>();
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

      public static boolean atualizarProcesso(Processo processoAtualizado) {
        // ... (código existente) ...
        // Se você decidiu que Base e NumeroProcesso não podem ser alterados, 
        // a busca para atualização ainda pode ser por eles.
        // Ou, para consistência com o ID, você poderia buscar pelo ID do processoAtualizado.
        // Exemplo:
        if (processoAtualizado == null) return false;
        for (int i = 0; i < processos.size(); i++) {
            Processo p = processos.get(i);
            // Agora que Processo tem um ID, é mais robusto usar o ID para encontrar o processo a ser atualizado
            if (p.getId() == processoAtualizado.getId()) { 
                processos.set(i, processoAtualizado);
                salvarDados();
                return true;
            }
        }
        return false;
    }

    public static boolean removerProcesso(String base, String numeroProcesso) {
        // O ideal seria remover por ID, mas manter a remoção por base/numero por enquanto.
        Processo processoParaRemover = buscarProcessoPorBaseNumero(base, numeroProcesso); // Ou buscarProcessoPorId(id)
        if (processoParaRemover != null) {
            if (processoParaRemover.getCaminhoDocumento() != null) {
                try {
                    Files.deleteIfExists(Paths.get(processoParaRemover.getCaminhoDocumento()));
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo do processo: " + e.getMessage());
                }
            }
            recibos.removeIf(recibo -> recibo.getProcessoAssociado().equals(processoParaRemover));
            processos.remove(processoParaRemover);
            salvarDados();
            return true;
        }
        return false;
    }
    
    // Opcional: Adicionar um método para remover processo por ID
    public static boolean removerProcesso(long id) {
        Processo processoParaRemover = buscarProcessoPorId(id);
        if (processoParaRemover != null) {
            if (processoParaRemover.getCaminhoDocumento() != null) {
                try {
                    Files.deleteIfExists(Paths.get(processoParaRemover.getCaminhoDocumento()));
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo do processo: " + e.getMessage());
                }
            }
            // Importante: garantir que a remoção de recibos associados ainda funcione corretamente.
            // A comparação ideal seria por ID do processo associado, se Recibo tivesse um getter para o ID do processo.
            // Por enquanto, manter a comparação por objeto completo de processo associado.
            recibos.removeIf(recibo -> recibo.getProcessoAssociado() != null && recibo.getProcessoAssociado().getId() == id); // Alterado para usar ID do processo associado
            processos.remove(processoParaRemover);
            salvarDados();
            return true;
        }
        return false;
    }


    public static void limparRepositorio() {
        processos.clear();
        recibos.clear();
        new File(PROCESSOS_FILE).delete();
        new File(RECIBOS_FILE).delete();
        try {
            Path root = getRootDocsPath();
            if (Files.exists(root)) {
                Files.walk(root)
                     .sorted(Comparator.reverseOrder())
                     .map(Path::toFile)
                     .forEach(File::delete);
            }
        } catch (IOException e) {
            System.err.println("Erro ao limpar pasta de documentos: " + e.getMessage());
        }
    }
}