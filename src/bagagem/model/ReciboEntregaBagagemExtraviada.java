package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ReciboEntregaBagagemExtraviada.
 * Comprovante que atesta a devolução da bagagem extraviada ao passageiro.
 * Herda de {@link bagagem.model.Recibo} e adiciona atributos específicos.
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public class ReciboEntregaBagagemExtraviada extends Recibo implements java.io.Serializable {

    // Atributo específico para ReciboEntregaBagagemExtraviada
    private String entregaOuRetiradaEmAeroporto; // Indica o local de entrega ou retirada da mala

    /**
     * Construtor da classe ReciboEntregaBagagemExtraviada.
     * Chama o construtor da superclasse {@link bagagem.model.Recibo} e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto do processo de extravio associado.
     * @param numeroProcesso O número do processo de extravio associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto {@link bagagem.model.Processo} (deve ser {@link bagagem.model.ExtravioBagagem}) associado.
     * @param entregaOuRetiradaEmAeroporto O local onde a entrega ou retirada da mala ocorreu.
     * @throws IllegalArgumentException Se o {@code processoAssociado} não for uma instância de {@link bagagem.model.ExtravioBagagem}.
     */
    public ReciboEntregaBagagemExtraviada(String base, String numeroProcesso, java.util.Date dataAssinatura, Processo processoAssociado, String entregaOuRetiradaEmAeroporto) {
        super(base, numeroProcesso, dataAssinatura, processoAssociado);

        // Validação específica: este recibo deve ser associado a um processo de ExtravioBagagem
        if (!(processoAssociado instanceof ExtravioBagagem)) {
            throw new IllegalArgumentException("ReciboEntregaBagagemExtraviada deve ser associado a um processo de ExtravioBagagem.");
        }

        this.entregaOuRetiradaEmAeroporto = entregaOuRetiradaEmAeroporto;
    }

    /**
     * Retorna o local de entrega ou retirada da bagagem extraviada.
     *
     * @return O local de entrega/retirada.
     */
    public String getEntregaOuRetiradaEmAeroporto() {
        return entregaOuRetiradaEmAeroporto;
    }

    /**
     * Define o local de entrega ou retirada da bagagem extraviada.
     *
     * @param entregaOuRetiradaEmAeroporto O novo local de entrega/retirada.
     */
    public void setEntregaOuRetiradaEmAeroporto(String entregaOuRetiradaEmAeroporto) {
        this.entregaOuRetiradaEmAeroporto = entregaOuRetiradaEmAeroporto;
    }

    /**
     * Retorna uma representação em String do objeto ReciboEntregaBagagemExtraviada,
     * incluindo suas informações específicas.
     *
     * @return Uma String com os detalhes do recibo de entrega de bagagem extraviada.
     */
    @Override
    public String toString() {
        return super.toString() +
                "\n  Local de Entrega/Retirada da Bagagem: " + entregaOuRetiradaEmAeroporto;
    }

    /**
     * Sobrescrita do método {@link bagagem.model.Recibo#editarDadosRecibo(java.util.Map)}
     * para incluir a edição do atributo específico {@code entregaOuRetiradaEmAeroporto}.
     *
     * @param novosDados Um {@link java.util.Map} onde a chave é o nome do atributo e o valor é o novo dado.
     * Pode incluir "entregaOuRetiradaEmAeroporto".
     */
    @Override
    public void editarDadosRecibo(java.util.Map<String, Object> novosDados) {
        super.editarDadosRecibo(novosDados); // Chama o método editarDadosRecibo da classe pai

        if (novosDados.containsKey("entregaOuRetiradaEmAeroporto")) {
            this.setEntregaOuRetiradaEmAeroporto((String) novosDados.get("entregaOuRetiradaEmAeroporto"));
            System.out.println("Local de entrega/retirada da bagagem extraviada atualizado para: " + this.entregaOuRetiradaEmAeroporto);
        }
        System.out.println("Dados do Recibo de Entrega de Bagagem Extraviada editados.");
    }
}