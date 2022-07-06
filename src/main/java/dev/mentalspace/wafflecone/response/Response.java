package dev.mentalspace.wafflecone.response;

import org.json.JSONObject;

/**
 * Wrapper class for JSONObject to make it easier to use for responses
 */
public class Response {
	public JSONObject response;

	public Response() {
		this.response = new JSONObject()
			.put("status", "success");
	}

	public Response(String status) {
		this.response = new JSONObject()
			.put("status", status);
	}

	public Response put(String key, String value) {
		this.response.put(key, value);
		return this;
	}

	public Response put(String key, Long value) {
		this.response.put(key, value);
		return this;
	}

	public Response put(String key, JSONObject value) {
		this.response.put(key, value);
		return this;
	}

	@Override
	public String toString() {
		return response.toString();
	}
}
