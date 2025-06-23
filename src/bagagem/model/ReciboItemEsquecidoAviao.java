package bagagem.model; // Garanta que o pacote está correto

import java.util.Date;
import java.util.Map;
import java.io.Serializable;

/**
 * Classe ReciboItemEsquecidoAviao.
 * Comprovante de que um item esquecido na aeronave foi devolvido ao passageiro.
 * Herda de Recibo e adiciona atributos específicos.
 */
public class ReciboItemEsquecidoAviao extends Recibo implements Serializable{

    // Atributo específico para ReciboItemEsquecidoAviao
    private String documentoIdentificacaoClienteRetirada; // Número do documento de identificação do cliente na retirada

    /**
     * Construtor da classe ReciboItemEsquecidoAviao.
     * Chama o construtor da superclasse Recibo e inicializa o atributo específico.
     *
     * @param base A sigla do aeroporto do processo de item esquecido associado.
     * @param numeroProcesso O número do processo de item esquecido associado.
     * @param dataAssinatura A data de assinatura ou registro do recibo.
     * @param processoAssociado A referência ao objeto Processo (deve ser ItemEsquecidoAviao) associado.
     * @param documentoIdentificacaoClienteRetirada O número do documento de identificação do cliente na retirada.
     * @throws IllegalArgumentException Se o processoAssociado não for uma instância de ItemEsquecidoAviao.
     */
    public ReciboItemEsquecidoAviao(String base, String numeroProcesso, Date dataAssinatura, Processo processoAssociado, String documentoIdentificacaoClienteRetirada) {
        super(base, numeroProcesso, dataAssinatura, processoAssociado); // Chama o construtor da classe pai (Recibo)

        // Validação específica: este recibo deve ser associado a um processo de ItemEsquecidoAviao
        if (!(processoAssociado instanceof ItemEsquecidoAviao)) {
            throw new IllegalArgumentException("ReciboItemEsquecidoAviao deve ser associado a um processo de ItemEsquecidoAviao.");
        }

        this.documentoIdentificacaoClienteRetirada = documentoIdentificacaoClienteRetirada;
    }

    // Método Getter para o atributo específico
    public String getDocumentoIdentificacaoClienteRetirada() {
        return documentoIdentificacaoClienteRetirada;
    }

    // Método Setter para o atributo específico (se for permitida a edição)
    public void setDocumentoIdentificacaoClienteRetirada(String documentoIdentificacaoClienteRetirada) {
        this.documentoIdentificacaoClienteRetirada = documentoIdentificacaoClienteRetirada;
    }
    
    /**
     * Retorna uma representação em String do objeto ReciboItemEsquecidoAviao,
     * incluindo suas informações específicas.
     * @return Uma String com os detalhes do recibo de item esquecido em avião.
     */
    @Override
    public String toString() {
        return super.toString() +
               "\n  Documento de Identificação para Retirada: " + documentoIdentificacaoClienteRetirada;
    }

    /**
     * Sobrescrita do método editarDadosRecibo para incluir o atributo específico
     * de ReciboItemEsquecidoAviao, se necessário.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    @Override
    public void editarDadosRecibo(Map<String, Object> novosDados) {
        super.editarDadosRecibo(novosDados); // Chama o método editarDadosRecibo da classe pai

        if (novosDados.containsKey("documentoIdentificacaoClienteRetirada")) {
            this.setDocumentoIdentificacaoClienteRetirada((String) novosDados.get("documentoIdentificacaoClienteRetirada"));
            System.out.println("Documento de identificação do cliente para retirada atualizado para: " + this.documentoIdentificacaoClienteRetirada);
        }
        System.out.println("Dados do Recibo de Item Esquecido em Avião editados.");
    }

    // Você pode adicionar métodos específicos para ReciboItemEsquecidoAviao aqui, se houver.
}