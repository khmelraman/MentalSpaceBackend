package dev.mentalspace.wafflecone.response;

import org.springframework.web.bind.annotation.ResponseStatus;

public class RequestError extends Response {
	public RequestError() {
		super("error");
	}
}
