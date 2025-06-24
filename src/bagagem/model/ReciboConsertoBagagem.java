package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ReciboConsertoBagagem.
 * Comprovante de que a bagagem danificada foi levada à oficina ou reparada.
 * Herda de {@link bagagem.model.Recibo} e adiciona atributos específicos.
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public class ReciboConsertoBagagem extends Recibo implements java.io.Serializable {

    // Atributo específico para ReciboConsertoBagagem
    private String entregaOuRetiradaEmAeroporto; // Indica se o conserto foi entregue ou retirado no aeroporto

    /**
     * Construtor da classe ReciboConsertoBagagem.
     * Chama o construtor da superclasse {@link bagagem.model.Recibo} e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto do processo de danificação associado.
     * @param numeroProcesso O número do processo de danificação associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto {@link bagagem.model.Processo} (deve ser {@link bagagem.model.DanificacaoBagagem}) associado.
     * @param entregaOuRetiradaEmAeroporto Indica o local de entrega/retirada do conserto.
     * @throws IllegalArgumentException Se o {@code processoAssociado} não for uma instância de {@link bagagem.model.DanificacaoBagagem}.
     */
    public ReciboConsertoBagagem(String base, String numeroProcesso, java.util.Date dataAssinatura, Processo processoAssociado, String entregaOuRetiradaEmAeroporto) {
        super(base, numeroProcesso, dataAssinatura, processoAssociado);

        // Validação específica: este recibo deve ser associado a um processo de DanificacaoBagagem
        if (!(processoAssociado instanceof DanificacaoBagagem)) {
            throw new IllegalArgumentException("ReciboConsertoBagagem deve ser associado a um processo de DanificacaoBagagem.");
        }

        this.entregaOuRetiradaEmAeroporto = entregaOuRetiradaEmAeroporto;
    }

    /**
     * Retorna o local de entrega ou retirada do conserto da bagagem.
     *
     * @return O local de entrega/retirada.
     */
    public String getEntregaOuRetiradaEmAeroporto() {
        return entregaOuRetiradaEmAeroporto;
    }

    /**
     * Define o local de entrega ou retirada do conserto da bagagem.
     *
     * @param entregaOuRetiradaEmAeroporto O novo local de entrega/retirada.
     */
    public void setEntregaOuRetiradaEmAeroporto(String entregaOuRetiradaEmAeroporto) {
        this.entregaOuRetiradaEmAeroporto = entregaOuRetiradaEmAeroporto;
    }

    /**
     * Retorna uma representação em String do objeto ReciboConsertoBagagem,
     * incluindo suas informações específicas.
     *
     * @return Uma String com os detalhes do recibo de conserto.
     */
    @Override
    public String toString() {
        return super.toString() +
                "\n  Local de Entrega/Retirada do Conserto: " + entregaOuRetiradaEmAeroporto;
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
            System.out.println("Local de entrega/retirada do conserto atualizado para: " + this.entregaOuRetiradaEmAeroporto);
        }
        System.out.println("Dados do Recibo de Conserto de Bagagem editados.");
    }
}