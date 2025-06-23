package bagagem.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Classe abstrata Recibo.
 * Representa, de maneira genérica, qualquer tipo de comprovante (recibo)
 * vinculado a um processo de bagagem.
 */
public abstract class Recibo implements Serializable {

    // NOVO ATRIBUTO: ID único e sequencial para cada recibo
    private long id;
    // Contador estático para gerar IDs sequenciais para Recibo
    private static long nextIdRecibo = 0; // Inicializa com 0. Será carregado do arquivo ou começará do 1 se for o primeiro.

    // Atributos existentes
    private String base;
    private String numeroProcesso;
    private Date dataAssinatura;
    private Processo processoAssociado;

    // NOVOS ATRIBUTOS para o documento do recibo
    private String caminhoDocumento;
    private String tipoArquivoDocumento;
    private long tamanhoArquivoDocumento;

    public Recibo(String base, String numeroProcesso, Date dataAssinatura, Processo processoAssociado) {
        // Atribui o ID e incrementa o contador
        this.id = ++nextIdRecibo; // Atribui e depois incrementa

        if (processoAssociado == null) {
            throw new IllegalArgumentException("Recibo deve estar vinculado a um Processo.");
        }
        this.base = base;
        this.numeroProcesso = numeroProcesso;
        this.dataAssinatura = dataAssinatura;
        this.processoAssociado = processoAssociado;
    }

    // NOVO MÉTODO: Getter para o ID
    public long getId() {
        return id;
    }

    // NOVO MÉTODO: Setter para o nextIdRecibo (para ser usado pelo repositório ao carregar)
    public static void setNextIdRecibo(long id) {
        nextIdRecibo = id;
    }

    // NOVO MÉTODO: Getter para o nextIdRecibo (para ser usado pelo repositório ao salvar)
    public static long getNextIdRecibo() {
        return nextIdRecibo;
    }

    // Getters e Setters para todos os atributos existentes (omitidos para brevidade, mas devem permanecer)
    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }
    public String getNumeroProcesso() { return numeroProcesso; }
    public void setNumeroProcesso(String numeroProcesso) { this.numeroProcesso = numeroProcesso; }
    public Date getDataAssinatura() { return dataAssinatura; }
    public void setDataAssinatura(Date dataAssinatura) { this.dataAssinatura = dataAssinatura; }
    public Processo getProcessoAssociado() { return processoAssociado; }
    public void setProcessoAssociado(Processo processoAssociado) { this.processoAssociado = processoAssociado; }

    public String getCaminhoDocumento() { return caminhoDocumento; }
    public void setCaminhoDocumento(String caminhoDocumento) { this.caminhoDocumento = caminhoDocumento; }
    public String getTipoArquivoDocumento() { return tipoArquivoDocumento; }
    public void setTipoArquivoDocumento(String tipoArquivoDocumento) { this.tipoArquivoDocumento = tipoArquivoDocumento; }
    public long getTamanhoArquivoDocumento() { return tamanhoArquivoDocumento; }
    public void setTamanhoArquivoDocumento(long tamanhoArquivoDocumento) { this.tamanhoArquivoDocumento = tamanhoArquivoDocumento; }

    public void editarDadosRecibo(Map<String, Object> novosDados) {
        if (novosDados.containsKey("dataAssinatura")) {
            this.setDataAssinatura((Date) novosDados.get("dataAssinatura"));
        }
        // A lógica de edição pode ser expandida aqui conforme a necessidade
    }

    @Override
    public String toString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return "Tipo Recibo: " + this.getClass().getSimpleName() +
               "\n  ID Recibo: " + this.id + // Incluindo o ID no toString
               "\n  Processo Associado: " + base + "-" + numeroProcesso +
               "\n  Data Assinatura: " + sdf.format(dataAssinatura) +
               (caminhoDocumento != null ? "\n  Documento: " + caminhoDocumento : "");
    }
}