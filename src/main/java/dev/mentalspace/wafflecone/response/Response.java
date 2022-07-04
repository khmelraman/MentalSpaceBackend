package dev.mentalspace.wafflecone.response;

public class Response {
	public String status;

	public Response() {
		this.status = "success";
	}

	public Response(String status) {
		this.status = status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
