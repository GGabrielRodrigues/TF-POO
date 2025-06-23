package bagagem.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Classe abstrata Processo.
 * Representa de forma genérica qualquer processo de bagagem que será
 * escaneado, organizado e armazenado no sistema.
 */
public abstract class Processo implements Serializable{

    // NOVO ATRIBUTO: ID único e sequencial para cada processo
    private long id; 
    // Contador estático para gerar IDs sequenciais para Processo
    private static long nextIdProcesso = 0; // Inicializa com 0. Será carregado do arquivo ou começará do 1 se for o primeiro.

    // Atributos privados existentes
    private String base;
    private String numeroProcesso;
    private Date dataAbertura;
    private String caminhoDocumento;
    private String tipoArquivoDocumento;
    private long tamanhoArquivoDocumento;

    /**
     * Construtor da classe Processo.
     * @param base A sigla do aeroporto onde o processo foi iniciado.
     * @param numeroProcesso O número de identificação do processo.
     * @param dataAbertura A data de abertura do processo.
     */
    public Processo(String base, String numeroProcesso, Date dataAbertura) {
        // Atribui o ID e incrementa o contador
        this.id = ++nextIdProcesso; // Atribui e depois incrementa
        
        this.base = base;
        this.numeroProcesso = numeroProcesso;
        this.dataAbertura = dataAbertura;
        this.caminhoDocumento = null;
        this.tipoArquivoDocumento = null;
        this.tamanhoArquivoDocumento = 0;
    }

    // NOVO MÉTODO: Getter para o ID
    public long getId() {
        return id;
    }
    
    // NOVO MÉTODO: Setter para o nextIdProcesso (para ser usado pelo repositório ao carregar)
    public static void setNextIdProcesso(long id) {
        nextIdProcesso = id;
    }
    
    // NOVO MÉTODO: Getter para o nextIdProcesso (para ser usado pelo repositório ao salvar)
    public static long getNextIdProcesso() {
        return nextIdProcesso;
    }


    // Métodos Getters e Setters existentes (omitidos para brevidade, mas devem permanecer no seu código)
    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }
    public String getNumeroProcesso() { return numeroProcesso; }
    public void setNumeroProcesso(String numeroProcesso) { this.numeroProcesso = numeroProcesso; }
    public Date getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(Date dataAbertura) { this.dataAbertura = dataAbertura; }
    public String getCaminhoDocumento() { return caminhoDocumento; }
    public void setCaminhoDocumento(String caminhoDocumento) { this.caminhoDocumento = caminhoDocumento; }
    public String getTipoArquivoDocumento() { return tipoArquivoDocumento; }
    public void setTipoArquivoDocumento(String tipoArquivoDocumento) { this.tipoArquivoDocumento = tipoArquivoDocumento; }
    public long getTamanhoArquivoDocumento() { return tamanhoArquivoDocumento; }
    public void setTamanhoArquivoDocumento(long tamanhoArquivoDocumento) { this.tamanhoArquivoDocumento = tamanhoArquivoDocumento; }

    /**
     * Aciona a câmera do dispositivo e retorna o caminho/URL da foto capturada.
     * Simula a captura de uma imagem através da câmera do dispositivo, a armazena
     * e atribui metadados simulados.
     * @return Uma String representando o caminho/URL da imagem.
     */
    public String capturarImagem() {
        System.out.println("Câmera acionada. Imagem capturada.");
        String novoCaminho = "caminho/para/imagem_capturada_" + System.currentTimeMillis() + ".jpg";
        this.caminhoDocumento = novoCaminho;
        this.tipoArquivoDocumento = "JPG";
        this.tamanhoArquivoDocumento = 1024 * 500;
        return novoCaminho;
    }

    /**
     * Renomeia o arquivo de imagem (por exemplo, para padronizar o nome no repositório).
     * Atualiza o atributo caminhoDocumento com o novo nome, tentando preservar a extensão.
     * @param novoNome O novo nome para o arquivo de imagem.
     */
    public void renomearDocumento(String novoNome) {
        if (this.caminhoDocumento != null && !this.caminhoDocumento.isEmpty()) {
            String extensao = "";
            int indicePonto = this.caminhoDocumento.lastIndexOf('.');
            if (indicePonto > 0) {
                extensao = this.caminhoDocumento.substring(indicePonto);
            } else if (this.tipoArquivoDocumento != null && !this.tipoArquivoDocumento.isEmpty()) {
                extensao = "." + this.tipoArquivoDocumento.toLowerCase();
            }
            this.caminhoDocumento = novoNome + extensao;
            System.out.println("Documento renomeado para: " + this.caminhoDocumento);
        } else {
            System.out.println("Nenhum documento para renomear neste processo.");
        }
    }

    /**
     * Permite atualizar qualquer atributo do processo (até base e numeroProcesso).
     * Permite atualizar qualquer atributo do processo, incluindo metadados do documento.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    public void editarInformacoes(Map<String, Object> novosDados) {
        // Não edita ID, base ou numeroProcesso via Map aqui, eles são chaves.
        // Já estão desabilitados na GUI para edição.
        if (novosDados.containsKey("dataAbertura")) {
            this.setDataAbertura((Date) novosDados.get("dataAbertura"));
            System.out.println("Data de abertura atualizada para: " + this.dataAbertura);
        }
        if (novosDados.containsKey("caminhoDocumento")) {
            this.setCaminhoDocumento((String) novosDados.get("caminhoDocumento"));
            System.out.println("Caminho do documento atualizado para: " + this.caminhoDocumento);
        }
        if (novosDados.containsKey("tipoArquivoDocumento")) {
            this.setTipoArquivoDocumento((String) novosDados.get("tipoArquivoDocumento"));
            System.out.println("Tipo de arquivo do documento atualizado para: " + this.tipoArquivoDocumento);
        }
        if (novosDados.containsKey("tamanhoArquivoDocumento")) {
            if (novosDados.get("tamanhoArquivoDocumento") instanceof Long) {
                this.setTamanhoArquivoDocumento((Long) novosDados.get("tamanhoArquivoDocumento"));
            } else if (novosDados.get("tamanhoArquivoDocumento") instanceof Integer) {
                this.setTamanhoArquivoDocumento(((Integer) novosDados.get("tamanhoArquivoDocumento")).longValue());
            }
            System.out.println("Tamanho do arquivo do documento atualizado para: " + this.tamanhoArquivoDocumento + " bytes.");
        }
        System.out.println("Informações do processo editadas.");
    }

    /**
     * Salva no repositório (nuvem ou local) o arquivo de imagem e os metadados mínimos (base, numeroProcesso e dataAbertura).
     * Agora também inclui os metadados do documento (caminho, tipo, tamanho).
     */
    public void armazenarDocumento() {
        System.out.println("Documento e metadados armazenados para o processo: " + this.base + " - " + this.numeroProcesso + " (ID: " + this.id + ")");
        System.out.println("Caminho do documento armazenado: " + (caminhoDocumento != null ? caminhoDocumento : "N/A"));
        System.out.println("Metadados do Documento: Tipo=" + (tipoArquivoDocumento != null ? tipoArquivoDocumento : "N/A") +
                           ", Tamanho=" + (tamanhoArquivoDocumento > 0 ? (tamanhoArquivoDocumento / 1024) + " KB" : "N/A"));
        // Em uma implementação real, aqui haveria lógica para persistir os dados e o arquivo.
    }

    // Métodos estáticos listarDocumentos e buscarDocumento não são mais diretamente usados na sua GUI,
    // a funcionalidade é centralizada em ProcessoRepository.
    public static List<Processo> listarDocumentos() {
        System.out.println("Listando todos os processos (implementação simplificada).");
        return new ArrayList<>();
    }

    public static Processo buscarDocumento(String base, String numeroProcesso) {
        System.out.println("Buscando processo: " + base + " - " + numeroProcesso + " (implementação simplificada).");
        return null;
    }
}