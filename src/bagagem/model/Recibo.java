package bagagem.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Classe abstrata Recibo.
 * Representa, de maneira genérica, qualquer tipo de comprovante (recibo)
 * vinculado a um processo de bagagem.
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public abstract class Recibo implements java.io.Serializable {

    // NOVO ATRIBUTO: ID único e sequencial para cada recibo
    private long id;
    // Contador estático para gerar IDs sequenciais para Recibo
    private static long nextIdRecibo = 0; // Inicializa com 0. Será carregado do arquivo ou começará do 1 se for o primeiro.

    // Atributos existentes
    private String base;
    private String numeroProcesso;
    private java.util.Date dataAssinatura;
    private Processo processoAssociado;

    // NOVOS ATRIBUTOS para o documento do recibo
    private String caminhoDocumento;
    private String tipoArquivoDocumento;
    private long tamanhoArquivoDocumento;

    /**
     * Construtor da classe Recibo.
     * Atribui um ID único e sequencial ao novo recibo e vincula-o a um processo.
     *
     * @param base A sigla do aeroporto do processo associado.
     * @param numeroProcesso O número do processo associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto {@link bagagem.model.Processo} ao qual o recibo está vinculado.
     * @throws IllegalArgumentException Se o {@code processoAssociado} for nulo.
     */
    public Recibo(String base, String numeroProcesso, java.util.Date dataAssinatura, Processo processoAssociado) {
        this.id = ++nextIdRecibo; // Atribui e depois incrementa

        if (processoAssociado == null) {
            throw new IllegalArgumentException("Recibo deve estar vinculado a um Processo.");
        }
        this.base = base;
        this.numeroProcesso = numeroProcesso;
        this.dataAssinatura = dataAssinatura;
        this.processoAssociado = processoAssociado;
    }

    /**
     * Retorna o ID único do recibo.
     *
     * @return O ID único do recibo.
     */
    public long getId() {
        return id;
    }

    /**
     * Define o próximo ID a ser usado para um novo recibo.
     * Usado principalmente pelo repositório ao carregar dados persistidos.
     *
     * @param id O valor a ser definido como o próximo ID.
     */
    public static void setNextIdRecibo(long id) {
        nextIdRecibo = id;
    }

    /**
     * Retorna o valor atual do próximo ID a ser gerado para um recibo.
     * Usado principalmente pelo repositório ao salvar dados.
     *
     * @return O próximo ID sequencial para um recibo.
     */
    public static long getNextIdRecibo() {
        return nextIdRecibo;
    }

    /**
     * Retorna a sigla da base/aeroporto do processo associado ao recibo.
     *
     * @return A base do processo associado.
     */
    public String getBase() {
        return base;
    }

    /**
     * Define a sigla da base/aeroporto do processo associado ao recibo.
     *
     * @param base A nova base do processo associado.
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * Retorna o número do processo associado ao recibo.
     *
     * @return O número do processo associado.
     */
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    /**
     * Define o número do processo associado ao recibo.
     *
     * @param numeroProcesso O novo número do processo associado.
     */
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    /**
     * Retorna a data de assinatura do recibo.
     *
     * @return A data de assinatura.
     */
    public java.util.Date getDataAssinatura() {
        return dataAssinatura;
    }

    /**
     * Define a data de assinatura do recibo.
     *
     * @param dataAssinatura A nova data de assinatura.
     */
    public void setDataAssinatura(java.util.Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    /**
     * Retorna o objeto {@link bagagem.model.Processo} associado a este recibo.
     *
     * @return O processo associado.
     */
    public Processo getProcessoAssociado() {
        return processoAssociado;
    }

    /**
     * Define o objeto {@link bagagem.model.Processo} associado a este recibo.
     *
     * @param processoAssociado O novo processo associado.
     */
    public void setProcessoAssociado(Processo processoAssociado) {
        this.processoAssociado = processoAssociado;
    }

    /**
     * Retorna o caminho do documento anexado ao recibo.
     *
     * @return O caminho do arquivo do documento.
     */
    public String getCaminhoDocumento() {
        return caminhoDocumento;
    }

    /**
     * Define o caminho do documento anexado ao recibo.
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
     * Permite atualizar atributos gerais do recibo.
     * As chaves do mapa devem corresponder aos nomes dos atributos (e.g., "dataAssinatura").
     *
     * @param novosDados Um {@link java.util.Map} onde a chave é o nome do atributo e o valor é o novo dado.
     */
    public void editarDadosRecibo(java.util.Map<String, Object> novosDados) {
        if (novosDados.containsKey("dataAssinatura")) {
            this.setDataAssinatura((java.util.Date) novosDados.get("dataAssinatura"));
        }
    }

    /**
     * Retorna uma representação em String do objeto Recibo,
     * incluindo suas informações gerais e do processo associado.
     *
     * @return Uma String formatada com os detalhes do recibo.
     */
    @Override
    public String toString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return "Tipo Recibo: " + this.getClass().getSimpleName() +
                "\n  ID Recibo: " + this.id +
                "\n  Processo Associado: " + base + "-" + numeroProcesso +
                "\n  Data Assinatura: " + sdf.format(dataAssinatura) +
                (caminhoDocumento != null ? "\n  Documento: " + caminhoDocumento : "");
    }
}