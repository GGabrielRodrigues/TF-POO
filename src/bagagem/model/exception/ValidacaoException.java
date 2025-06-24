package bagagem.model.exception;

/**
 * Exceção personalizada para erros de validação no sistema.
 * Estende {@link java.lang.Exception} para indicar que é uma exceção checada.
 * É usada para sinalizar que uma entrada de dados ou uma operação não atende
 * aos critérios de validação definidos.
 */
public class ValidacaoException extends Exception {

    /**
     * Construtor da exceção de validação.
     * Cria uma nova instância de {@code ValidacaoException} com a mensagem de erro especificada.
     *
     * @param message A mensagem de erro detalhada, explicando o motivo da validação falha.
     */
    public ValidacaoException(String message) {
        super(message);
    }
}