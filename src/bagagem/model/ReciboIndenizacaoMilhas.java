package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ReciboIndenizacaoMilhas.
 * Comprovante de indenização concedida em milhas ao passageiro, por danificação irreparável.
 * Herda de Recibo e adiciona atributos específicos.
 */
public class ReciboIndenizacaoMilhas extends Recibo implements Serializable{

    // Atributo específico para ReciboIndenizacaoMilhas
    private int quantidadeMilhas; // Número de milhas concedidas como indenização

    /**
     * Construtor da classe ReciboIndenizacaoMilhas.
     * Chama o construtor da superclasse Recibo e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto do processo de danificação associado.
     * @param numeroProcesso O número do processo de danificação associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto Processo (deve ser DanificacaoBagagem) associado.
     * @param quantidadeMilhas O número de milhas concedidas.
     * @throws IllegalArgumentException Se o processoAssociado não for uma instância de DanificacaoBagagem.
     */
    public ReciboIndenizacaoMilhas(String base, String numeroProcesso, Date dataAssinatura, Processo processoAssociado, int quantidadeMilhas) {
        super(base, numeroProcesso, dataAssinatura, processoAssociado); // Chama o construtor da classe pai (Recibo)

        // Validação específica: este recibo deve ser associado a um processo de DanificacaoBagagem
        if (!(processoAssociado instanceof DanificacaoBagagem)) {
            throw new IllegalArgumentException("ReciboIndenizacaoMilhas deve ser associado a um processo de DanificacaoBagagem.");
        }

        this.quantidadeMilhas = quantidadeMilhas;
    }

    // Método Getter para o atributo específico
    public int getQuantidadeMilhas() {
        return quantidadeMilhas;
    }

    // Método Setter para o atributo específico (se for permitida a edição)
    public void setQuantidadeMilhas(int quantidadeMilhas) {
        this.quantidadeMilhas = quantidadeMilhas;
    }
    
    /**
     * Retorna uma representação em String do objeto ReciboIndenizacaoMilhas,
     * incluindo suas informações específicas.
     * @return Uma String com os detalhes do recibo de indenização em milhas.
     */
    @Override
    public String toString() {
        return super.toString() +
               "\n  Quantidade de Milhas: " + quantidadeMilhas;
    }

    /**
     * Sobrescrita do método editarDadosRecibo para incluir o atributo específico
     * de ReciboIndenizacaoMilhas, se necessário.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    @Override
    public void editarDadosRecibo(Map<String, Object> novosDados) {
        super.editarDadosRecibo(novosDados); // Chama o método editarDadosRecibo da classe pai

        if (novosDados.containsKey("quantidadeMilhas")) {
            // Garante que o valor do Map seja um Integer para evitar ClassCastException
            if (novosDados.get("quantidadeMilhas") instanceof Integer) {
                this.setQuantidadeMilhas((Integer) novosDados.get("quantidadeMilhas"));
                System.out.println("Quantidade de milhas atualizada para: " + this.quantidadeMilhas);
            } else {
                System.err.println("Erro: 'quantidadeMilhas' deve ser um valor inteiro.");
            }
        }
        System.out.println("Dados do Recibo de Indenização em Milhas editados.");
    }

    // Você pode adicionar métodos específicos para ReciboIndenizacaoMilhas aqui, se houver.
}