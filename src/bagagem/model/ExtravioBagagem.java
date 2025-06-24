package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ExtravioBagagem.
 * Representa o processo em que a bagagem foi identificada como extraviada.
 * Herda de {@link bagagem.model.Processo} e adiciona atributos específicos para este tipo de ocorrência.
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public class ExtravioBagagem extends Processo implements java.io.Serializable {

    // Atributo específico para ExtravioBagagem
    private String etiquetaBagagemExtraviada; // Número da etiqueta da mala extraviada

    /**
     * Construtor da classe ExtravioBagagem.
     * Chama o construtor da superclasse {@link bagagem.model.Processo} e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto onde o processo de extravio foi iniciado.
     * @param numeroProcesso O número de identificação do processo de extravio.
     * @param dataAbertura A data de abertura do processo.
     * @param etiquetaBagagemExtraviada O número da etiqueta da bagagem extraviada.
     */
    public ExtravioBagagem(String base, String numeroProcesso, java.util.Date dataAbertura, String etiquetaBagagemExtraviada) {
        super(base, numeroProcesso, dataAbertura);
        this.etiquetaBagagemExtraviada = etiquetaBagagemExtraviada;
    }

    /**
     * Retorna o número da etiqueta da bagagem extraviada.
     *
     * @return A etiqueta da bagagem extraviada.
     */
    public String getEtiquetaBagagemExtraviada() {
        return etiquetaBagagemExtraviada;
    }

    /**
     * Define o número da etiqueta da bagagem extraviada.
     *
     * @param etiquetaBagagemExtraviada A nova etiqueta da bagagem extraviada.
     */
    public void setEtiquetaBagagemExtraviada(String etiquetaBagagemExtraviada) {
        this.etiquetaBagagemExtraviada = etiquetaBagagemExtraviada;
    }

    /**
     * Sobrescrita do método {@link bagagem.model.Processo#editarInformacoes(java.util.Map)}
     * para incluir a edição do atributo específico {@code etiquetaBagagemExtraviada}.
     *
     * @param novosDados Um {@link java.util.Map} onde a chave é o nome do atributo e o valor é o novo dado.
     * Pode incluir "etiquetaBagagemExtraviada".
     */
    @Override
    public void editarInformacoes(java.util.Map<String, Object> novosDados) {
        super.editarInformacoes(novosDados); // Chama o método editarInformacoes da classe pai

        if (novosDados.containsKey("etiquetaBagagemExtraviada")) {
            this.setEtiquetaBagagemExtraviada((String) novosDados.get("etiquetaBagagemExtraviada"));
            System.out.println("Etiqueta da bagagem extraviada atualizada para: " + this.etiquetaBagagemExtraviada);
        }
        System.out.println("Informações de Extravio de Bagagem editadas.");
    }
}