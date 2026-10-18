package lfp.plp.functional2.expression;

import lfp.plp.expressions1.excecao.ErroTipoException;

/**
 * Exceção lançada quando tipos incompatíveis são combinados em operações de pipeline ou composição.
 */
public class TiposIncompativeisException extends ErroTipoException {

	private static final long serialVersionUID = 1L;

	public TiposIncompativeisException() {
		super();
	}

	public TiposIncompativeisException(String message) {
		super(message);
	}

	public TiposIncompativeisException(String message, Throwable cause) {
		super(message, cause);
	}
}
