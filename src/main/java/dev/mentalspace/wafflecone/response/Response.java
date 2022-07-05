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

	public void put(String key, String value) {
		this.response.put(key, value);
	}

	@Override
	public String toString() {
		return response.toString();
	}
}
