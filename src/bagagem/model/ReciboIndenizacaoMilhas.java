package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ReciboIndenizacaoMilhas.
 * Comprovante de indenização concedida em milhas ao passageiro, por danificação irreparável.
 * Herda de {@link bagagem.model.Recibo} e adiciona atributos específicos.
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public class ReciboIndenizacaoMilhas extends Recibo implements java.io.Serializable {

    // Atributo específico para ReciboIndenizacaoMilhas
    private int quantidadeMilhas; // Número de milhas concedidas como indenização

    /**
     * Construtor da classe ReciboIndenizacaoMilhas.
     * Chama o construtor da superclasse {@link bagagem.model.Recibo} e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto do processo de danificação associado.
     * @param numeroProcesso O número do processo de danificação associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto {@link bagagem.model.Processo} (deve ser {@link bagagem.model.DanificacaoBagagem}) associado.
     * @param quantidadeMilhas O número de milhas concedidas.
     * @throws IllegalArgumentException Se o {@code processoAssociado} não for uma instância de {@link bagagem.model.DanificacaoBagagem}.
     */
    public ReciboIndenizacaoMilhas(String base, String numeroProcesso, java.util.Date dataAssinatura, Processo processoAssociado, int quantidadeMilhas) {
        super(base, numeroProcesso, dataAssinatura, processoAssociado);

        // Validação específica: este recibo deve ser associado a um processo de DanificacaoBagagem
        if (!(processoAssociado instanceof DanificacaoBagagem)) {
            throw new IllegalArgumentException("ReciboIndenizacaoMilhas deve ser associado a um processo de DanificacaoBagagem.");
        }

        this.quantidadeMilhas = quantidadeMilhas;
    }

    /**
     * Retorna a quantidade de milhas concedidas como indenização.
     *
     * @return A quantidade de milhas.
     */
    public int getQuantidadeMilhas() {
        return quantidadeMilhas;
    }

    /**
     * Define a quantidade de milhas concedidas como indenização.
     *
     * @param quantidadeMilhas A nova quantidade de milhas.
     */
    public void setQuantidadeMilhas(int quantidadeMilhas) {
        this.quantidadeMilhas = quantidadeMilhas;
    }

    /**
     * Retorna uma representação em String do objeto ReciboIndenizacaoMilhas,
     * incluindo suas informações específicas.
     *
     * @return Uma String com os detalhes do recibo de indenização em milhas.
     */
    @Override
    public String toString() {
        return super.toString() +
                "\n  Quantidade de Milhas: " + quantidadeMilhas;
    }

    /**
     * Sobrescrita do método {@link bagagem.model.Recibo#editarDadosRecibo(java.util.Map)}
     * para incluir a edição do atributo específico {@code quantidadeMilhas}.
     *
     * @param novosDados Um {@link java.util.Map} onde a chave é o nome do atributo e o valor é o novo dado.
     * Pode incluir "quantidadeMilhas".
     */
    @Override
    public void editarDadosRecibo(java.util.Map<String, Object> novosDados) {
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
}