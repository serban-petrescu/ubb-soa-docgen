package ro.am.docgen.manager.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.am.docgen.manager.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public ErrorWrapper handleNotFound(NotFoundException exception) {
		log.info("Handling not found exception.", exception);
		return new ErrorWrapper("NotFoundException");
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ErrorWrapper handleOther(Exception exception) {
		log.info("Handling other exception.", exception);
		return new ErrorWrapper(exception.getClass().getSimpleName());
	}

	@Data
	private static class ErrorWrapper {
		private final String type;
	}
}
