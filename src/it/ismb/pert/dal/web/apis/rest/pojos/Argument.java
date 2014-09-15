package it.ismb.pert.dal.web.apis.rest.pojos;

/**
 * A pojo representing a Function argument
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class Argument {
	//the Java type as String (e.g. "java.lang.Integer")
	private String type;
	private Object value;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
