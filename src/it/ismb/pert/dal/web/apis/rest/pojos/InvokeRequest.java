package it.ismb.pert.dal.web.apis.rest.pojos;

/**
 * Pojo representing an Invoke Request from a client
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class InvokeRequest {
	private String operation;
	
	private Argument[] arguments;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Argument[] getArguments() {
		return arguments;
	}

	public void setArguments(Argument[] arguments) {
		this.arguments = arguments;
	}

	
}

