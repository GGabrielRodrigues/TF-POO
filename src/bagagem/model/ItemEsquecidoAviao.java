package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ItemEsquecidoAviao.
 * Representa o processo em que um objeto foi esquecido dentro da aeronave
 * e precisa ser devolvido ao passageiro.
 * Herda de {@link bagagem.model.Processo} e adiciona atributos específicos para este tipo de ocorrência.
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public class ItemEsquecidoAviao extends Processo implements java.io.Serializable {

    // Atributo específico para ItemEsquecidoAviao
    private String numeroVoo; // Código do voo no qual o item foi deixado

    /**
     * Construtor da classe ItemEsquecidoAviao.
     * Chama o construtor da superclasse {@link bagagem.model.Processo} e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto onde o processo de item esquecido foi iniciado.
     * @param numeroProcesso O número de identificação do processo de item esquecido.
     * @param dataAbertura A data de abertura do processo.
     * @param numeroVoo O código do voo no qual o item foi deixado.
     */
    public ItemEsquecidoAviao(String base, String numeroProcesso, java.util.Date dataAbertura, String numeroVoo) {
        super(base, numeroProcesso, dataAbertura);
        this.numeroVoo = numeroVoo;
    }

    /**
     * Retorna o número do voo no qual o item foi esquecido.
     *
     * @return O número do voo.
     */
    public String getNumeroVoo() {
        return numeroVoo;
    }

    /**
     * Define o número do voo no qual o item foi esquecido.
     *
     * @param numeroVoo O novo número do voo.
     */
    public void setNumeroVoo(String numeroVoo) {
        this.numeroVoo = numeroVoo;
    }

    /**
     * Sobrescrita do método {@link bagagem.model.Processo#editarInformacoes(java.util.Map)}
     * para incluir a edição do atributo específico {@code numeroVoo}.
     *
     * @param novosDados Um {@link java.util.Map} onde a chave é o nome do atributo e o valor é o novo dado.
     * Pode incluir "numeroVoo".
     */
    @Override
    public void editarInformacoes(java.util.Map<String, Object> novosDados) {
        super.editarInformacoes(novosDados); // Chama o método editarInformacoes da classe pai

        if (novosDados.containsKey("numeroVoo")) {
            this.setNumeroVoo((String) novosDados.get("numeroVoo"));
            System.out.println("Número do voo atualizado para: " + this.numeroVoo);
        }
        System.out.println("Informações de Item Esquecido em Avião editadas.");
    }
}