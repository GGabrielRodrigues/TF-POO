package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ReciboEntregaBagagemExtraviada.
 * Comprovante que atesta a devolução da bagagem extraviada ao passageiro.
 * Herda de Recibo e adiciona atributos específicos.
 */
public class ReciboEntregaBagagemExtraviada extends Recibo implements Serializable{

    // Atributo específico para ReciboEntregaBagagemExtraviada
    private String entregaOuRetiradaEmAeroporto; // Indica o local de entrega ou retirada da mala

    /**
     * Construtor da classe ReciboEntregaBagagemExtraviada.
     * Chama o construtor da superclasse Recibo e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto do processo de extravio associado.
     * @param numeroProcesso O número do processo de extravio associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto Processo (deve ser ExtravioBagagem) associado.
     * @param entregaOuRetiradaEmAeroporto O local onde a entrega ou retirada da mala ocorreu.
     * @throws IllegalArgumentException Se o processoAssociado não for uma instância de ExtravioBagagem.
     */
    public ReciboEntregaBagagemExtraviada(String base, String numeroProcesso, Date dataAssinatura, Processo processoAssociado, String entregaOuRetiradaEmAeroporto) {
        super(base, numeroProcesso, dataAssinatura, processoAssociado); // Chama o construtor da classe pai (Recibo)

        // Validação específica: este recibo deve ser associado a um processo de ExtravioBagagem
        if (!(processoAssociado instanceof ExtravioBagagem)) {
            throw new IllegalArgumentException("ReciboEntregaBagagemExtraviada deve ser associado a um processo de ExtravioBagagem.");
        }

        this.entregaOuRetiradaEmAeroporto = entregaOuRetiradaEmAeroporto;
    }

    // Método Getter para o atributo específico
    public String getEntregaOuRetiradaEmAeroporto() {
        return entregaOuRetiradaEmAeroporto;
    }

    // Método Setter para o atributo específico (se for permitida a edição)
    public void setEntregaOuRetiradaEmAeroporto(String entregaOuRetiradaEmAeroporto) {
        this.entregaOuRetiradaEmAeroporto = entregaOuRetiradaEmAeroporto;
    }
    
    /**
     * Retorna uma representação em String do objeto ReciboEntregaBagagemExtraviada,
     * incluindo suas informações específicas.
     * @return Uma String com os detalhes do recibo de entrega de bagagem extraviada.
     */
    @Override
    public String toString() {
        return super.toString() +
               "\n  Local de Entrega/Retirada da Bagagem: " + entregaOuRetiradaEmAeroporto;
    }

    /**
     * Sobrescrita do método editarDadosRecibo para incluir o atributo específico
     * de ReciboEntregaBagagemExtraviada, se necessário.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    @Override
    public void editarDadosRecibo(Map<String, Object> novosDados) {
        super.editarDadosRecibo(novosDados); // Chama o método editarDadosRecibo da classe pai

        if (novosDados.containsKey("entregaOuRetiradaEmAeroporto")) {
            this.setEntregaOuRetiradaEmAeroporto((String) novosDados.get("entregaOuRetiradaEmAeroporto"));
            System.out.println("Local de entrega/retirada da bagagem extraviada atualizado para: " + this.entregaOuRetiradaEmAeroporto);
        }
        System.out.println("Dados do Recibo de Entrega de Bagagem Extraviada editados.");
    }

    // Você pode adicionar métodos específicos para ReciboEntregaBagagemExtraviada aqui, se houver.
}
