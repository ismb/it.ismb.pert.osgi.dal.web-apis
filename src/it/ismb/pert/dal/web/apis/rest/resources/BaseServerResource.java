package it.ismb.pert.dal.web.apis.rest.resources;

import org.restlet.engine.header.Header;
import org.restlet.resource.Options;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

public class BaseServerResource extends ServerResource{
	
	//Adds custom header
	protected void addCustomHeaders()
	{
		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
		if (responseHeaders == null) {
		    responseHeaders = new Series(Header.class);
		    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
		}
		//allow request from other origins
		responseHeaders.add(new Header("Access-Control-Allow-Origin", "*"));
	}

	//Method added for CORS requests support: pre-flight request use OPTION method
	@Options
	public void doOptions()
	{
		Series<Header> responseHeaders = (Series<Header>) getResponse().getAttributes().get("org.restlet.http.headers");
		if (responseHeaders == null) {
		    responseHeaders = new Series(Header.class);
		    getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
		}
		responseHeaders.add("Access-Control-Allow-Origin", "*"); 
	    responseHeaders.add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    responseHeaders.add("Access-Control-Allow-Headers", "Content-Type"); 

	    responseHeaders.add("Access-Control-Max-Age", "60"); 
	}
}
