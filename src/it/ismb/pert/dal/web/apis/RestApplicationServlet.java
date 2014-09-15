package it.ismb.pert.dal.web.apis;

import it.ismb.pert.dal.web.apis.rest.application.JemmaApplication;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.ext.servlet.ServerServlet;

/**
 * Class implementing a Restlet Servlet, can be registered with OSGi HttpService 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class RestApplicationServlet extends ServerServlet{
	
	@Override
	protected Application createApplication(Context context)
	{
		return new JemmaApplication();
	}
	@Override
	public void init(ServletConfig config) throws ServletException
	{

		super.init(config);
	}
}
