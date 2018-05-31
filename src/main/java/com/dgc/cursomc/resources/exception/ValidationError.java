package com.dgc.cursomc.resources.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {

	private static final long serialVersionUID = 1L;

	private List<FieldMessage> erros = new ArrayList<FieldMessage>();

	public List<FieldMessage> getErrors() {
		return erros;
	}

	public void addError(String fieldName, String message) {
		this.erros.add(new FieldMessage(fieldName, message));
	}

	public ValidationError(Long timestamp, Integer status, String error, String message, String path) {
		super(timestamp, status, error, message, path);
	}

}
