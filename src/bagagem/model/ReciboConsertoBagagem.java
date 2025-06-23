package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ReciboConsertoBagagem.
 * Comprovante de que a bagagem danificada foi levada à oficina ou reparada.
 * Herda de Recibo e adiciona atributos específicos.
 */
public class ReciboConsertoBagagem extends Recibo implements Serializable{

    // Atributo específico para ReciboConsertoBagagem
    private String entregaOuRetiradaEmAeroporto; // Indica se o conserto foi entregue ou retirado no aeroporto

    /**
     * Construtor da classe ReciboConsertoBagagem.
     * Chama o construtor da superclasse Recibo e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto do processo de danificação associado.
     * @param numeroProcesso O número do processo de danificação associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto Processo (deve ser DanificacaoBagagem) associado.
     * @param entregaOuRetiradaEmAeroporto Indica o local de entrega/retirada do conserto.
     * @throws IllegalArgumentException Se o processoAssociado não for uma instância de DanificacaoBagagem.
     */
    public ReciboConsertoBagagem(String base, String numeroProcesso, Date dataAssinatura, Processo processoAssociado, String entregaOuRetiradaEmAeroporto) {
        super(base, numeroProcesso, dataAssinatura, processoAssociado); // Chama o construtor da classe pai (Recibo)

        // Validação específica: este recibo deve ser associado a um processo de DanificacaoBagagem
        if (!(processoAssociado instanceof DanificacaoBagagem)) {
            throw new IllegalArgumentException("ReciboConsertoBagagem deve ser associado a um processo de DanificacaoBagagem.");
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
     * Retorna uma representação em String do objeto ReciboConsertoBagagem,
     * incluindo suas informações específicas.
     * @return Uma String com os detalhes do recibo de conserto.
     */
    @Override
    public String toString() {
        return super.toString() +
               "\n  Local de Entrega/Retirada do Conserto: " + entregaOuRetiradaEmAeroporto;
    }

    /**
     * Sobrescrita do método editarDadosRecibo para incluir o atributo específico
     * de ReciboConsertoBagagem, se necessário.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    @Override
    public void editarDadosRecibo(Map<String, Object> novosDados) {
        super.editarDadosRecibo(novosDados); // Chama o método editarDadosRecibo da classe pai

        if (novosDados.containsKey("entregaOuRetiradaEmAeroporto")) {
            this.setEntregaOuRetiradaEmAeroporto((String) novosDados.get("entregaOuRetiradaEmAeroporto"));
            System.out.println("Local de entrega/retirada do conserto atualizado para: " + this.entregaOuRetiradaEmAeroporto);
        }
        System.out.println("Dados do Recibo de Conserto de Bagagem editados.");
    }

    // Você pode adicionar métodos específicos para ReciboConsertoBagagem aqui, se houver.
}