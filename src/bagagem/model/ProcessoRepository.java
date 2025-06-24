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

public class ProcessoRepository {

    private static final String PROCESSOS_FILE = "processos.dat";
    private static final String RECIBOS_FILE = "recibos.dat";
    private static final String COUNTERS_FILE = "counters.dat";
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

        File countersFile = new File(COUNTERS_FILE);
        if (countersFile.exists() && countersFile.length() > 0) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(COUNTERS_FILE))) {
                Processo.setNextIdProcesso(dis.readLong());
                Recibo.setNextIdRecibo(dis.readLong());
            } catch (IOException e) {
                System.err.println("Erro ao carregar contadores de ID: " + e.getMessage());
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

    public static Processo buscarProcessoPorId(long id) {
        for (Processo p : processos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // NOVO MÉTODO: Buscar recibo específico por ID
    public static Recibo buscarReciboPorId(long id) {
        for (Recibo r : recibos) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    // NOVO MÉTODO: Buscar recibo específico por base e número do PROCESSO ASSOCIADO
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

    // NOVO MÉTODO: Filtrar recibos por base e/ou tipo e/ou número do processo associado
    public static List<Recibo> filtrarRecibos(String baseProcesso, String tipoRecibo, String numeroProcesso) {
        List<Recibo> resultados = new ArrayList<>();
        for (Recibo r : recibos) {
            boolean matchBase = baseProcesso == null || baseProcesso.trim().isEmpty() || r.getBase().equalsIgnoreCase(baseProcesso.trim());
            
            // Compara o nome da classe do recibo (ex: "ReciboConsertoBagagem") com o tipo fornecido
            // O tipo fornecido pode vir do ComboBox da GUI (ex: "Recibo de Conserto de Bagagem")
            boolean matchTipo = tipoRecibo == null || tipoRecibo.trim().isEmpty() || 
                                r.getClass().getSimpleName().equalsIgnoreCase(tipoRecibo.replace("Recibo de ", "").replace(" ", ""));
            
            boolean matchNumeroProcesso = numeroProcesso == null || numeroProcesso.trim().isEmpty() || r.getNumeroProcesso().equals(numeroProcesso.trim());

            if (matchBase && matchTipo && matchNumeroProcesso) {
                resultados.add(r);
            }
        }
        return Collections.unmodifiableList(resultados);
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
    
    // NOVO MÉTODO: Atualizar Recibo
    public static boolean atualizarRecibo(Recibo reciboAtualizado) {
        if (reciboAtualizado == null) return false;
        for (int i = 0; i < recibos.size(); i++) {
            Recibo r = recibos.get(i);
            if (r.getId() == reciboAtualizado.getId()) { // Usar o ID do recibo para atualização
                recibos.set(i, reciboAtualizado);
                salvarDados();
                return true;
            }
        }
        return false;
    }

    public static boolean removerProcesso(String base, String numeroProcesso) {
        Processo processoParaRemover = buscarProcessoPorBaseNumero(base, numeroProcesso);
        if (processoParaRemover != null) {
            if (processoParaRemover.getCaminhoDocumento() != null) {
                try {
                    Files.deleteIfExists(Paths.get(processoParaRemover.getCaminhoDocumento()));
                } catch (IOException e) {
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
            recibos.removeIf(recibo -> recibo.getProcessoAssociado() != null && recibo.getProcessoAssociado().getId() == id);
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
        new File(COUNTERS_FILE).delete();
        
        Processo.setNextIdProcesso(0);
        Recibo.setNextIdRecibo(0);

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
    
    public static List<Processo> listarProcessosFiltrados(String base, String tipoProcesso) {
        List<Processo> resultados = new ArrayList<>();
        
        // Se a base e o tipo forem vazios/nulos ou "Todos", retorna todos os processos
        if ((base == null || base.trim().isEmpty()) && (tipoProcesso == null || tipoProcesso.trim().isEmpty() || tipoProcesso.equals("Todos"))) {
            return Collections.unmodifiableList(processos); // Retorna todos sem filtro
        }

        for (Processo p : processos) {
            boolean matchBase = true;
            if (base != null && !base.trim().isEmpty()) {
                matchBase = p.getBase().equalsIgnoreCase(base.trim());
            }

            boolean matchTipo = true;
            if (tipoProcesso != null && !tipoProcesso.trim().isEmpty() && !tipoProcesso.equals("Todos")) {
                // Compara o nome simples da classe do processo (ex: "DanificacaoBagagem")
                matchTipo = p.getClass().getSimpleName().equalsIgnoreCase(tipoProcesso.trim());
            }

            if (matchBase && matchTipo) {
                resultados.add(p);
            }
        }
        return Collections.unmodifiableList(resultados);
    }
    
}

