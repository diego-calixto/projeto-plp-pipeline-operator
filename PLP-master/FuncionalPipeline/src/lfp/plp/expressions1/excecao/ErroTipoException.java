package lfp.plp.expressions1.excecao;

public class ErroTipoException extends RuntimeException {

	private static final long serialVersionUID = 5279414744949285209L;

	public ErroTipoException() {
		super();
	}

	public ErroTipoException(String message) {
		super(message);
	}

	public ErroTipoException(String message, Throwable cause) {
		super(message, cause);
	}
}
