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
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public abstract class Processo implements Serializable {

    // NOVO ATRIBUTO: ID único e sequencial para cada processo
    private long id;
    // Contador estático para gerar IDs sequenciais para Processo
    private static long nextIdProcesso = 0; // Inicializa com 0. Será carregado do arquivo ou começará do 1 se for o primeiro.

    // Atributos privados existentes
    private String base;
    private String numeroProcesso;
    private java.util.Date dataAbertura;
    private String caminhoDocumento;
    private String tipoArquivoDocumento;
    private long tamanhoArquivoDocumento;

    /**
     * Construtor da classe Processo.
     * Atribui um ID único e sequencial ao novo processo.
     *
     * @param base A sigla do aeroporto onde o processo foi iniciado (Ex: GYN).
     * @param numeroProcesso O número de identificação único do processo.
     * @param dataAbertura A data de abertura do processo.
     */
    public Processo(String base, String numeroProcesso, java.util.Date dataAbertura) {
        this.id = ++nextIdProcesso;
        this.base = base;
        this.numeroProcesso = numeroProcesso;
        this.dataAbertura = dataAbertura;
        this.caminhoDocumento = null;
        this.tipoArquivoDocumento = null;
        this.tamanhoArquivoDocumento = 0;
    }

    /**
     * Retorna o ID único do processo.
     *
     * @return O ID único do processo.
     */
    public long getId() {
        return id;
    }

    /**
     * Define o próximo ID a ser usado para um novo processo.
     * Usado principalmente pelo repositório ao carregar dados persistidos.
     *
     * @param id O valor a ser definido como o próximo ID.
     */
    public static void setNextIdProcesso(long id) {
        nextIdProcesso = id;
    }

    /**
     * Retorna o valor atual do próximo ID a ser gerado para um processo.
     * Usado principalmente pelo repositório ao salvar dados.
     *
     * @return O próximo ID sequencial para um processo.
     */
    public static long getNextIdProcesso() {
        return nextIdProcesso;
    }

    /**
     * Retorna a sigla da base/aeroporto do processo.
     *
     * @return A base do processo.
     */
    public String getBase() {
        return base;
    }

    /**
     * Define a sigla da base/aeroporto do processo.
     *
     * @param base A nova base do processo.
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * Retorna o número de identificação do processo.
     *
     * @return O número do processo.
     */
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    /**
     * Define o número de identificação do processo.
     *
     * @param numeroProcesso O novo número do processo.
     */
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    /**
     * Retorna a data de abertura do processo.
     *
     * @return A data de abertura do processo.
     */
    public java.util.Date getDataAbertura() {
        return dataAbertura;
    }

    /**
     * Define a data de abertura do processo.
     *
     * @param dataAbertura A nova data de abertura do processo.
     */
    public void setDataAbertura(java.util.Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    /**
     * Retorna o caminho do documento anexado ao processo.
     *
     * @return O caminho do arquivo do documento.
     */
    public String getCaminhoDocumento() {
        return caminhoDocumento;
    }

    /**
     * Define o caminho do documento anexado ao processo.
     *
     * @param caminhoDocumento O novo caminho do arquivo do documento.
     */
    public void setCaminhoDocumento(String caminhoDocumento) {
        this.caminhoDocumento = caminhoDocumento;
    }

    /**
     * Retorna o tipo de arquivo do documento anexado (e.g., "JPG", "PDF").
     *
     * @return O tipo do arquivo do documento.
     */
    public String getTipoArquivoDocumento() {
        return tipoArquivoDocumento;
    }

    /**
     * Define o tipo de arquivo do documento anexado.
     *
     * @param tipoArquivoDocumento O novo tipo do arquivo do documento.
     */
    public void setTipoArquivoDocumento(String tipoArquivoDocumento) {
        this.tipoArquivoDocumento = tipoArquivoDocumento;
    }

    /**
     * Retorna o tamanho do arquivo do documento anexado em bytes.
     *
     * @return O tamanho do arquivo do documento.
     */
    public long getTamanhoArquivoDocumento() {
        return tamanhoArquivoDocumento;
    }

    /**
     * Define o tamanho do arquivo do documento anexado em bytes.
     *
     * @param tamanhoArquivoDocumento O novo tamanho do arquivo do documento.
     */
    public void setTamanhoArquivoDocumento(long tamanhoArquivoDocumento) {
        this.tamanhoArquivoDocumento = tamanhoArquivoDocumento;
    }

    /**
     * Aciona a câmera do dispositivo e retorna o caminho/URL da foto capturada.
     * Simula a captura de uma imagem através da câmera do dispositivo, a armazena
     * e atribui metadados simulados.
     *
     * @return Uma String representando o caminho/URL da imagem capturada.
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
     *
     * @param novoNome O novo nome para o arquivo de imagem (sem extensão).
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
     * Permite atualizar atributos do processo, incluindo metadados do documento.
     * As chaves do mapa devem corresponder aos nomes dos atributos (e.g., "dataAbertura", "caminhoDocumento").
     *
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    public void editarInformacoes(java.util.Map<String, Object> novosDados) {
        if (novosDados.containsKey("dataAbertura")) {
            this.setDataAbertura((java.util.Date) novosDados.get("dataAbertura"));
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
     * Simula o armazenamento do documento e seus metadados associados no repositório (nuvem ou local).
     * Em uma implementação real, aqui haveria lógica para persistir os dados e o arquivo.
     */
    public void armazenarDocumento() {
        System.out.println("Documento e metadados armazenados para o processo: " + this.base + " - " + this.numeroProcesso + " (ID: " + this.id + ")");
        System.out.println("Caminho do documento armazenado: " + (caminhoDocumento != null ? caminhoDocumento : "N/A"));
        System.out.println("Metadados do Documento: Tipo=" + (tipoArquivoDocumento != null ? tipoArquivoDocumento : "N/A") +
                ", Tamanho=" + (tamanhoArquivoDocumento > 0 ? (tamanhoArquivoDocumento / 1024) + " KB" : "N/A"));
    }
}