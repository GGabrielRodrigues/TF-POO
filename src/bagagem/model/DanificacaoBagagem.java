package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe DanificacaoBagagem.
 * Representa o processo em que a bagagem foi danificada.
 * Herda de Processo e adiciona atributos específicos para este tipo de ocorrência.
 */
public class DanificacaoBagagem extends Processo implements Serializable{

    // Atributo específico para DanificacaoBagagem
    private String etiquetaBagagemDanificada; // Número da etiqueta física da mala danificada 

    /**
     * Construtor da classe DanificacaoBagagem.
     * Chama o construtor da superclasse Processo e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto onde o processo de danificação foi iniciado.
     * @param numeroProcesso O número de identificação do processo de danificação.
     * @param dataAbertura A data de abertura do processo.
     * @param etiquetaBagagemDanificada O número da etiqueta da bagagem danificada. 
     */
    public DanificacaoBagagem(String base, String numeroProcesso, Date dataAbertura, String etiquetaBagagemDanificada) {
        super(base, numeroProcesso, dataAbertura); // Chama o construtor da classe pai (Processo)
        this.etiquetaBagagemDanificada = etiquetaBagagemDanificada;
    }

    // Método Getter para o atributo específico
    public String getEtiquetaBagagemDanificada() {
        return etiquetaBagagemDanificada;
    }

    // Método Setter para o atributo específico (se for permitida a edição)
    public void setEtiquetaBagagemDanificada(String etiquetaBagagemDanificada) {
        this.etiquetaBagagemDanificada = etiquetaBagagemDanificada;
    }

    /**
     * Sobrescrita do método editarInformacoes para incluir o atributo específico
     * da bagagem danificada, se necessário.
     * Embora o requisito não especifique explicitamente a edição deste atributo via Map,
     * é uma boa prática permitir, ou sobrescrever para validar se este atributo é passado.
     * Por enquanto, apenas chamamos o método pai.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    @Override
    public void editarInformacoes(Map<String, Object> novosDados) {
        super.editarInformacoes(novosDados); // Chama o método editarInformacoes da classe pai

        if (novosDados.containsKey("etiquetaBagagemDanificada")) {
            this.setEtiquetaBagagemDanificada((String) novosDados.get("etiquetaBagagemDanificada"));
            System.out.println("Etiqueta da bagagem danificada atualizada para: " + this.etiquetaBagagemDanificada);
        }
        System.out.println("Informações de Danificação de Bagagem editadas.");
    }

    // Você pode adicionar métodos específicos para DanificacaoBagagem aqui, se houver.
    // Por exemplo, um método para gerar um recibo de conserto ou indenização.
}
