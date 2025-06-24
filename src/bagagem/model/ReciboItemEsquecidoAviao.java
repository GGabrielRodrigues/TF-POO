package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ReciboItemEsquecidoAviao.
 * Comprovante de que um item esquecido na aeronave foi devolvido ao passageiro.
 * Herda de {@link bagagem.model.Recibo} e adiciona atributos específicos.
 * <p>
 * Implementa {@link java.io.Serializable} para permitir a persistência de objetos.
 */
public class ReciboItemEsquecidoAviao extends Recibo implements java.io.Serializable {

    // Atributo específico para ReciboItemEsquecidoAviao
    private String documentoIdentificacaoClienteRetirada; // Número do documento de identificação do cliente na retirada

    /**
     * Construtor da classe ReciboItemEsquecidoAviao.
     * Chama o construtor da superclasse {@link bagagem.model.Recibo} e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto do processo de item esquecido associado.
     * @param numeroProcesso O número do processo de item esquecido associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto {@link bagagem.model.Processo} (deve ser {@link bagagem.model.ItemEsquecidoAviao}) associado.
     * @param documentoIdentificacaoClienteRetirada O número do documento de identificação do cliente na retirada.
     * @throws IllegalArgumentException Se o {@code processoAssociado} não for uma instância de {@link bagagem.model.ItemEsquecidoAviao}.
     */
    public ReciboItemEsquecidoAviao(String base, String numeroProcesso, java.util.Date dataAssinatura, Processo processoAssociado, String documentoIdentificacaoClienteRetirada) {
        super(base, numeroProcesso, dataAssinatura, processoAssociado);

        // Validação específica: este recibo deve ser associado a um processo de ItemEsquecidoAviao
        if (!(processoAssociado instanceof ItemEsquecidoAviao)) {
            throw new IllegalArgumentException("ReciboItemEsquecidoAviao deve ser associado a um processo de ItemEsquecidoAviao.");
        }

        this.documentoIdentificacaoClienteRetirada = documentoIdentificacaoClienteRetirada;
    }

    /**
     * Retorna o número do documento de identificação do cliente para retirada.
     *
     * @return O documento de identificação do cliente.
     */
    public String getDocumentoIdentificacaoClienteRetirada() {
        return documentoIdentificacaoClienteRetirada;
    }

    /**
     * Define o número do documento de identificação do cliente para retirada.
     *
     * @param documentoIdentificacaoClienteRetirada O novo documento de identificação do cliente.
     */
    public void setDocumentoIdentificacaoClienteRetirada(String documentoIdentificacaoClienteRetirada) {
        this.documentoIdentificacaoClienteRetirada = documentoIdentificacaoClienteRetirada;
    }

    /**
     * Retorna uma representação em String do objeto ReciboItemEsquecidoAviao,
     * incluindo suas informações específicas.
     *
     * @return Uma String com os detalhes do recibo de item esquecido em avião.
     */
    @Override
    public String toString() {
        return super.toString() +
                "\n  Documento de Identificação para Retirada: " + documentoIdentificacaoClienteRetirada;
    }

    /**
     * Sobrescrita do método {@link bagagem.model.Recibo#editarDadosRecibo(java.util.Map)}
     * para incluir a edição do atributo específico {@code documentoIdentificacaoClienteRetirada}.
     *
     * @param novosDados Um {@link java.util.Map} onde a chave é o nome do atributo e o valor é o novo dado.
     * Pode incluir "documentoIdentificacaoClienteRetirada".
     */
    @Override
    public void editarDadosRecibo(java.util.Map<String, Object> novosDados) {
        super.editarDadosRecibo(novosDados); // Chama o método editarDadosRecibo da classe pai

        if (novosDados.containsKey("documentoIdentificacaoClienteRetirada")) {
            this.setDocumentoIdentificacaoClienteRetirada((String) novosDados.get("documentoIdentificacaoClienteRetirada"));
            System.out.println("Documento de identificação do cliente para retirada atualizado para: " + this.documentoIdentificacaoClienteRetirada);
        }
        System.out.println("Dados do Recibo de Item Esquecido em Avião editados.");
    }
}