package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ExtravioBagagem.
 * Representa o processo em que a bagagem foi identificada como extraviada.
 * Herda de Processo e adiciona atributos específicos para este tipo de ocorrência.
 */
public class ExtravioBagagem extends Processo implements Serializable{

    // Atributo específico para ExtravioBagagem
    private String etiquetaBagagemExtraviada; // Número da etiqueta da mala extraviada 

    /**
     * Construtor da classe ExtravioBagagem.
     * Chama o construtor da superclasse Processo e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto onde o processo de extravio foi iniciado.
     * @param numeroProcesso O número de identificação do processo de extravio.
     * @param dataAbertura A data de abertura do processo.
     * @param etiquetaBagagemExtraviada O número da etiqueta da bagagem extraviada.
     */
    public ExtravioBagagem(String base, String numeroProcesso, Date dataAbertura, String etiquetaBagagemExtraviada) {
        super(base, numeroProcesso, dataAbertura); // Chama o construtor da classe pai (Processo)
        this.etiquetaBagagemExtraviada = etiquetaBagagemExtraviada;
    }

    // Método Getter para o atributo específico
    public String getEtiquetaBagagemExtraviada() {
        return etiquetaBagagemExtraviada;
    }

    // Método Setter para o atributo específico (se for permitida a edição)
    public void setEtiquetaBagagemExtraviada(String etiquetaBagagemExtraviada) {
        this.etiquetaBagagemExtraviada = etiquetaBagagemExtraviada;
    }

    /**
     * Sobrescrita do método editarInformacoes para incluir o atributo específico
     * da bagagem extraviada, se necessário.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    @Override
    public void editarInformacoes(Map<String, Object> novosDados) {
        super.editarInformacoes(novosDados); // Chama o método editarInformacoes da classe pai

        if (novosDados.containsKey("etiquetaBagagemExtraviada")) {
            this.setEtiquetaBagagemExtraviada((String) novosDados.get("etiquetaBagagemExtraviada"));
            System.out.println("Etiqueta da bagagem extraviada atualizada para: " + this.etiquetaBagagemExtraviada);
        }
        System.out.println("Informações de Extravio de Bagagem editadas.");
    }

    // Você pode adicionar métodos específicos para ExtravioBagagem aqui, se houver.
}
