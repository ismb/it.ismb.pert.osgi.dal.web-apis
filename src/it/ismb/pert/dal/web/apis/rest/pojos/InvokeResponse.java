package it.ismb.pert.dal.web.apis.rest.pojos;

/**
 * Pojo representing a response to an Invoke request
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class InvokeResponse {

	private int code;
	private Object result;
	private String message;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
}
