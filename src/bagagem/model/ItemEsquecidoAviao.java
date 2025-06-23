package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ItemEsquecidoAviao.
 * Representa o processo em que um objeto foi esquecido dentro da aeronave
 * e precisa ser devolvido ao passageiro.
 * Herda de Processo e adiciona atributos específicos para este tipo de ocorrência.
 */
public class ItemEsquecidoAviao extends Processo implements Serializable{

    // Atributo específico para ItemEsquecidoAviao
    private String numeroVoo; // Código do voo no qual o item foi deixado

    /**
     * Construtor da classe ItemEsquecidoAviao.
     * Chama o construtor da superclasse Processo e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto onde o processo de item esquecido foi iniciado.
     * @param numeroProcesso O número de identificação do processo de item esquecido.
     * @param dataAbertura A data de abertura do processo.
     * @param numeroVoo O código do voo no qual o item foi deixado.
     */
    public ItemEsquecidoAviao(String base, String numeroProcesso, Date dataAbertura, String numeroVoo) {
        super(base, numeroProcesso, dataAbertura); // Chama o construtor da classe pai (Processo)
        this.numeroVoo = numeroVoo;
    }

    // Método Getter para o atributo específico
    public String getNumeroVoo() {
        return numeroVoo;
    }

    // Método Setter para o atributo específico (se for permitida a edição)
    public void setNumeroVoo(String numeroVoo) {
        this.numeroVoo = numeroVoo;
    }

    /**
     * Sobrescrita do método editarInformacoes para incluir o atributo específico
     * do item esquecido em avião, se necessário.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    @Override
    public void editarInformacoes(Map<String, Object> novosDados) {
        super.editarInformacoes(novosDados); // Chama o método editarInformacoes da classe pai

        if (novosDados.containsKey("numeroVoo")) {
            this.setNumeroVoo((String) novosDados.get("numeroVoo"));
            System.out.println("Número do voo atualizado para: " + this.numeroVoo);
        }
        System.out.println("Informações de Item Esquecido em Avião editadas.");
    }

    // Você pode adicionar métodos específicos para ItemEsquecidoAviao aqui, se houver.
}