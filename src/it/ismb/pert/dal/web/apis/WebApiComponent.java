package it.ismb.pert.dal.web.apis;

import it.ismb.pert.dal.web.apis.rest.application.JemmaApplication;
import it.ismb.pert.dal.web.apis.websocket.APIsWebSocketHandler;

import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * Web Apis main component. registers REST and WebSocket servlets with HttpService
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class WebApiComponent {

	public static final String REST_PATH="/api";
	public static final String WEBSOCKET_PATH="/ws";
	
	//the factory for WebSocket handlers, used when creating new WebSocket connections
	private ComponentFactory webSocketFactory;
	
	private HttpService httpService;
	
	public void activate(ComponentContext context)
	{
		try {
		
			httpService.registerServlet(REST_PATH, new RestApplicationServlet(), null, null);
			httpService.registerServlet(WEBSOCKET_PATH, new APIsWebSocketHandler(webSocketFactory), null, null);
			httpService.registerResources("/virtualhome", "/virtualhome", null);
		
		} catch (ServletException | NamespaceException e) {
			
			e.printStackTrace();
		}

	}
	
	public void bindHttpService(HttpService httpService)
	{
		this.httpService=httpService;
	}
	
	public void unbindHttpService(HttpService httpService)
	{
		httpService.unregister(REST_PATH);
		httpService.unregister(WEBSOCKET_PATH);
		httpService=null;
	}
	
	public void bindComponentFactory(ComponentFactory componentFactory)
	{
		this.webSocketFactory=componentFactory;
	}
	
	public void unbindComponentFactory(ComponentFactory componentFactory)
	{
		this.webSocketFactory=null;
	}
	
}
