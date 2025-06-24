package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe DanificacaoBagagem.
 * Representa o processo em que a bagagem foi danificada.
 * Herda de {@link bagagem.model.Processo} e adiciona atributos específicos para este tipo de ocorrência.
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public class DanificacaoBagagem extends Processo implements java.io.Serializable {

    // Atributo específico para DanificacaoBagagem
    private String etiquetaBagagemDanificada; // Número da etiqueta física da mala danificada

    /**
     * Construtor da classe DanificacaoBagagem.
     * Chama o construtor da superclasse {@link bagagem.model.Processo} e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto onde o processo de danificação foi iniciado.
     * @param numeroProcesso O número de identificação do processo de danificação.
     * @param dataAbertura A data de abertura do processo.
     * @param etiquetaBagagemDanificada O número da etiqueta da bagagem danificada.
     */
    public DanificacaoBagagem(String base, String numeroProcesso, java.util.Date dataAbertura, String etiquetaBagagemDanificada) {
        super(base, numeroProcesso, dataAbertura);
        this.etiquetaBagagemDanificada = etiquetaBagagemDanificada;
    }

    /**
     * Retorna o número da etiqueta da bagagem danificada.
     *
     * @return A etiqueta da bagagem danificada.
     */
    public String getEtiquetaBagagemDanificada() {
        return etiquetaBagagemDanificada;
    }

    /**
     * Define o número da etiqueta da bagagem danificada.
     *
     * @param etiquetaBagagemDanificada A nova etiqueta da bagagem danificada.
     */
    public void setEtiquetaBagagemDanificada(String etiquetaBagagemDanificada) {
        this.etiquetaBagagemDanificada = etiquetaBagagemDanificada;
    }

    /**
     * Sobrescrita do método {@link bagagem.model.Processo#editarInformacoes(java.util.Map)}
     * para incluir a edição do atributo específico {@code etiquetaBagagemDanificada}.
     *
     * @param novosDados Um {@link java.util.Map} onde a chave é o nome do atributo e o valor é o novo dado.
     * Pode incluir "etiquetaBagagemDanificada".
     */
    @Override
    public void editarInformacoes(java.util.Map<String, Object> novosDados) {
        super.editarInformacoes(novosDados); // Chama o método editarInformacoes da classe pai

        if (novosDados.containsKey("etiquetaBagagemDanificada")) {
            this.setEtiquetaBagagemDanificada((String) novosDados.get("etiquetaBagagemDanificada"));
            System.out.println("Etiqueta da bagagem danificada atualizada para: " + this.etiquetaBagagemDanificada);
        }
        System.out.println("Informações de Danificação de Bagagem editadas.");
    }
}