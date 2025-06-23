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

    // Atributos
    private String base;
    private String numeroProcesso;
    private Date dataAssinatura;
    private Processo processoAssociado;

    // NOVOS ATRIBUTOS para o documento do recibo
    private String caminhoDocumento;
    private String tipoArquivoDocumento;
    private long tamanhoArquivoDocumento;

    public Recibo(String base, String numeroProcesso, Date dataAssinatura, Processo processoAssociado) {
        if (processoAssociado == null) {
            throw new IllegalArgumentException("Recibo deve estar vinculado a um Processo.");
        }
        this.base = base;
        this.numeroProcesso = numeroProcesso;
        this.dataAssinatura = dataAssinatura;
        this.processoAssociado = processoAssociado;
    }

    // Getters e Setters para todos os atributos
    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }
    public String getNumeroProcesso() { return numeroProcesso; }
    public void setNumeroProcesso(String numeroProcesso) { this.numeroProcesso = numeroProcesso; }
    public Date getDataAssinatura() { return dataAssinatura; }
    public void setDataAssinatura(Date dataAssinatura) { this.dataAssinatura = dataAssinatura; }
    public Processo getProcessoAssociado() { return processoAssociado; }
    public void setProcessoAssociado(Processo processoAssociado) { this.processoAssociado = processoAssociado; }

    // Getters e Setters para os NOVOS atributos do documento
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
               "\n  Processo Associado: " + base + "-" + numeroProcesso +
               "\n  Data Assinatura: " + sdf.format(dataAssinatura) +
               (caminhoDocumento != null ? "\n  Documento: " + caminhoDocumento : "");
    }
}