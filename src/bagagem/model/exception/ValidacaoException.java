package bagagem.model.exception;
public class ValidacaoException extends Exception {

    /**
     * Construtor da exceção de validação.
     * 
     * @param message A mensagem de erro a ser exibida.
     */
    public ValidacaoException(String message) {
        super(message);
    }
    
}
