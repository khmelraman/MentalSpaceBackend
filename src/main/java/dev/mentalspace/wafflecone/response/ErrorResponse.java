package dev.mentalspace.wafflecone.response;

import org.json.JSONObject;

public class ErrorResponse extends Response {
	public JSONObject errors;

	public ErrorResponse() {
		super("error");
		this.errors = new JSONObject(); // just {} this is "just-in-case"
	}

	public ErrorResponse(JSONObject json) {
		super("error");
		this.errors = json;
	}

	@Override
	public String toString() {
		super.response.put("errors", this.errors);
		return super.toString();
	}
}
